/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package com.kleegroup.analyticaimpl.server.plugins.processapi.rest;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;

import vertigo.kernel.exception.VRuntimeException;
import vertigo.kernel.lang.Activeable;

import com.kleegroup.analyticaimpl.server.ProcessNetApiPlugin;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

/**
 * Plugin gérant l'api reseau en REST avec jersey.
 * @author npiedeloup
 * @version $Id: RestNetApiPlugin.java,v 1.3 2012/10/16 12:39:27 npiedeloup Exp $
 */
public final class RestProcessNetApiPlugin implements ProcessNetApiPlugin, Activeable {
	private static final Logger LOG = Logger.getLogger(RestProcessNetApiPlugin.class);
	private final int httpPort;
	private HttpServer httpServer;

	/**
	 * Constructeur simple pour instanciation par jersey.
	 * @param httpPort port du serveur web
	 */
	@Inject
	public RestProcessNetApiPlugin(@Named("httpPort") final int httpPort) {
		this.httpPort = httpPort;
	}

	/** {@inheritDoc} */
	public void start() {
		try {
			httpServer = startServer();
		} catch (final IOException e) {
			throw new VRuntimeException("Erreur de lancement du Server Web Analytica.");
		}
	}

	/** {@inheritDoc} */
	public void stop() {
		httpServer.stop();
	}

	private final HttpServer startServer() throws IOException {
		final URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(httpPort).build();
		LOG.info("Starting grizzly...");
		final ResourceConfig rc = new PackagesResourceConfig(JerseyRestProcessNetApi.class.getPackage().getName());
		rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, com.sun.jersey.api.container.filter.GZIPContentEncodingFilter.class.getName());
		rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, com.sun.jersey.api.container.filter.GZIPContentEncodingFilter.class.getName());
		final HttpServer grizzlyServer = GrizzlyServerFactory.createHttpServer(baseUri, rc);
		LOG.info(String.format("Jersey app started with WADL available at " + "%sapplication.wadl", baseUri));
		return grizzlyServer;
	}

}
