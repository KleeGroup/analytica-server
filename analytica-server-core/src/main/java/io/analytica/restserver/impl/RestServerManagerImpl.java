/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>
 */
package io.analytica.restserver.impl;

import io.analytica.restserver.RestServerManager;
import io.vertigo.core.param.ParamManager;
import io.vertigo.lang.Activeable;
import io.vertigo.lang.Assertion;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

/**
 * InfluxDBProcessAggregatorPlugin gérant l'api reseau en REST avec jersey.
 * @author npiedeloup
 */
public final class RestServerManagerImpl implements RestServerManager, Activeable {
	private static final Logger LOG = Logger.getLogger(RestServerManagerImpl.class);
	private final String apiPath;
	private final int httpPort;
	private HttpServer httpServer;

	private final List<Class<?>> handlers = new ArrayList<>();
	private final Map<String, List<String>> pathsPerContext = new HashMap<>();
	private final Timer delayedStarter = new Timer("RestServerDelayedStarter", true);
	private RestartServerTask startServerTask;

	@Inject
	public ParamManager paramManager;
	
	/**
	 * Constructeur.
	 * @param apiPath Chemin racine des WebServices REST (commence et fini par / et autre que /)
	 * @param httpPort port du serveur web
	 */
	@Inject
	public RestServerManagerImpl(@Named("apiPath") final String apiPath, @Named("httpPort") final int httpPort) {
		Assertion.checkArgNotEmpty(apiPath);
		Assertion.checkArgument(apiPath.startsWith("/") && apiPath.endsWith("/") && !apiPath.equals("/"), "La racine des WebServices (apiPath:{0}) doit commencer et finir par /, et étre différent de /", apiPath);
		//---------------------------------------------------------------------
		String test = paramManager.getStringValue("restApiPath");
		this.apiPath = apiPath;
		this.httpPort = httpPort;
	}

	/** {@inheritDoc} */
	@Override
	public void addResourceHandler(final Class<?> handlerClass) {
		handlers.add(handlerClass);
		restart();
	}

	/** {@inheritDoc} */
	@Override
	public void addStaticPath(final String classPath, final String context) {
		final List<String> paths = obtainPathsPerContext(context);
		paths.add(classPath);
		restart();
	}

	private List<String> obtainPathsPerContext(final String context) {
		List<String> paths = pathsPerContext.get(context);
		if (paths == null) {
			paths = new ArrayList<>();
			pathsPerContext.put(context, paths);
		}
		return paths;
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		restart();
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		startServerTask.cancel();
		startServerTask = null;
		//On programme une fin pour maintenant.
		//On Utilise le timer pour rester dans le méme Thread que le start.
		//Le stop peut étre appellé plusieurs fois.
		delayedStarter.schedule(new StopServerTask(), 0); //stop now
	}

	private void restart() {
		if (startServerTask != null) {
			startServerTask.cancel(); //Si on avait déjé une tache on l'annule
		}
		startServerTask = new RestartServerTask();
		delayedStarter.schedule(startServerTask, 500); //start dans 500ms (sera prolongé si on redemande un démarrage sd'ici lé)
	}

	/**
	 * Relance le server.
	 */
	protected void restartServer() {
		//---------------------------------------------------------------------
		doStopServer();
		try {
			if (handlers.size() > 0) {
				httpServer = doStartServer();
			}
		} catch (final IOException e) {
			throw new RuntimeException("Erreur de lancement du Server Web Analytica.");
		}
	}

	/**
	 * Stoppe le server.
	 */
	protected void doStopServer() {
		if (httpServer != null) {
			httpServer.shutdownNow();
		}
		httpServer = null;
	}

	private final HttpServer doStartServer() throws IOException {
		final URI baseUri = UriBuilder.fromUri("http://0.0.0.0" + apiPath).port(httpPort).build();
		LOG.info("Starting grizzly...");
		final ResourceConfig rc = new ClassNamesResourceConfig(handlers.toArray(new Class[handlers.size()]));
		rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, com.sun.jersey.api.container.filter.GZIPContentEncodingFilter.class.getName());
		rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, com.sun.jersey.api.container.filter.GZIPContentEncodingFilter.class.getName());
		final HttpServer grizzlyServer = GrizzlyServerFactory.createHttpServer(baseUri, rc);
		
		// Add the CLStaticHttpHandler to serve static resources
		for (final Map.Entry<String, List<String>> entry : pathsPerContext.entrySet()) {
			StaticHttpHandler handler = new StaticHttpHandler(entry.getValue().toArray(new String[entry.getValue().size()]));
//			grizzlyServer.getServerConfiguration().addHttpHandler(handler,entry.getKey());
			grizzlyServer.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(Thread.currentThread().getContextClassLoader(), entry.getValue().toArray(new String[entry.getValue().size()])), entry.getKey());
		}
		for (Map.Entry<HttpHandler, String[]>set : grizzlyServer.getServerConfiguration().getHttpHandlers().entrySet()) {
			System.out.println(set.getKey()+" "+ set.getValue());
		}
		for (final NetworkListener listener : grizzlyServer.getListeners()) {
			//if false, local files (html, etc.) can be modified without restarting the server
			listener.getFileCache().setEnabled(false);
		}
		grizzlyServer.start();
		LOG.info(String.format("Jersey scaned packages : " + "%s", handlers));
		LOG.info(String.format("Jersey routes : " + "%s", rc.getRootResourceClasses()));
		LOG.info(String.format("Jersey ClassPath routes : " + "%s", pathsPerContext.entrySet()));
		LOG.info(String.format("Jersey app started with WADL available at " + "%sapplication.wadl", baseUri));
		return grizzlyServer;
	}

	/**
	 * Tache de redémarrage du server.
	 */
	protected class RestartServerTask extends TimerTask {
		/** {@inheritDoc} */
		@Override
		public void run() {
			restartServer();
		}
	}

	/**
	 * Tache d'arret du server.
	 */
	protected class StopServerTask extends TimerTask {
		/** {@inheritDoc} */
		@Override
		public void run() {
			doStopServer();
		}
	}
}
