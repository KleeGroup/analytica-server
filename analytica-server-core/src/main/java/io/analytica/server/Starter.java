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
package io.analytica.server;

import io.analytica.hcube.HCubeManager;
import io.analytica.hcube.impl.HCubeManagerImpl;
import io.analytica.hcube.plugins.memorystack.MemoryStackProcessStatsPlugin;
import io.analytica.hcube.plugins.socketio.SocketIoProcessStatsPlugin;
import io.analytica.hcube.plugins.store.memory.MemoryCubeStorePlugin;
import io.analytica.restserver.RestServerManager;
import io.analytica.restserver.impl.RestServerManagerImpl;
import io.analytica.server.impl.ServerManagerImpl;
import io.analytica.server.plugins.processapi.rest.RestProcessNetApiPlugin;
import io.analytica.server.plugins.processstore.berkeley.BerkeleyProcessStorePlugin;
import io.analytica.server.plugins.processstore.memory.MemoryProcessStorePlugin;
import io.analytica.server.plugins.queryapi.rest.RestQueryNetApiPlugin;
import io.vertigo.kernel.Home;
import io.vertigo.kernel.di.configurator.ComponentConfigBuilder;
import io.vertigo.kernel.di.configurator.ComponentSpaceConfig;
import io.vertigo.kernel.di.configurator.ComponentSpaceConfigBuilder;
import io.vertigo.kernel.di.configurator.ModuleConfigBuilder;
import io.vertigo.kernel.lang.Assertion;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Charge et démarre un environnement.
 * @author pchretien, npiedeloup
 */
public class Starter implements Runnable {
	private static final String PROCESS_STORE_PATH = "processStorePath";
	private static final String CUBE_STORE_PATH = "cubeStorePath";
	private static final String SOCKET_IO_URL = "socketIoUrl";
	private static final String STACK_PROCESS_STATS = "stackProcessStats";
	private static final String REST_API_PATH = "restApiPath";
	private static final String REST_API_PORT = "restApiPort";
	private static final String TYPE_API_NONE = "NONE";
	private static final String TYPE_API_REST = "REST";
	private static final String PROCESS_API = "processApi";
	private static final String QUERY_API = "queryApi";

	private static boolean SILENCE = false;
	private final Class<?> relativeRootClass;
	private final String propertiesFileName;
	private boolean started;

	/**
	 * @param propertiesFileName Fichier de propriétés
	 * @param relativeRootClass Racine du chemin relatif, le cas echéant
	 */
	public Starter(final String propertiesFileName, final Class<?> relativeRootClass) {
		Assertion.checkNotNull(propertiesFileName);
		Assertion.checkNotNull(relativeRootClass);
		//---------------------------------------------------------------------
		this.propertiesFileName = propertiesFileName;
		this.relativeRootClass = relativeRootClass;
	}

	/**
	 * Lance l'environnement et attend indéfiniment.
	 * @param args "Usage: java kasper.kernel.Starter managers.xml <conf.properties>"
	 */
	public static void main(final String[] args) {
		final String usageMsg = "Usage: java io.analytica.server.Starter <conf.properties>";
		Assertion.checkArgument(args.length == 1, usageMsg + " ( conf attendue : " + args.length + ")");
		Assertion.checkArgument(args[0].endsWith(".properties"), usageMsg + " ( .properties attendu : " + args[0] + ")");
		//---------------------------------------------------------------------
		final String propertiesFileName = args[0];
		final Starter starter = new Starter(propertiesFileName, Starter.class);
		starter.run();
	}

	/** {@inheritDoc} */
	public final void run() {
		try {
			start();

			final Object lock = new Object();
			synchronized (lock) {
				lock.wait(0); //on attend le temps demandé et 0 => illimité
			}
		} catch (final InterruptedException e) {
			//rien arret normal
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			stop();
		}
	}

	/**
	 * Démarre l'application.
	 * @throws IOException Erreur de création du server Web
	 * @throws NumberFormatException Erreur de format du port
	 */
	public final void start() throws NumberFormatException, IOException {
		// Création de l'état de l'application
		// Initialisation de l'état de l'application
		final Properties properties = new Properties();
		appendFileProperties(properties, propertiesFileName, relativeRootClass);
		final ComponentSpaceConfig componentSpaceConfig = createComponentSpaceConfig(properties);
		Home.start(componentSpaceConfig);
		started = true;
	}

	/**
	 * @param properties Propriétés de l'environnement.
	 * @return ComponentSpaceConfig configuration de l'environnement
	 */
	protected final ComponentSpaceConfig createComponentSpaceConfig(final Properties properties) {
		final ComponentSpaceConfigBuilder componentSpaceConfigBuilder = new ComponentSpaceConfigBuilder() //
				.withSilence(SILENCE);
		appendModuleAnalytica(properties, componentSpaceConfigBuilder);
		appendOtherModules(properties, componentSpaceConfigBuilder);
		return componentSpaceConfigBuilder.build();
	}

	/**
	 * Ajoute d'autre modules à la configuration de l'environnement.
	 * @param properties  Propriétés de l'environnement.
	 * @param componentSpaceConfigBuilder Builder de la configuration de l'environnement
	 */
	protected void appendOtherModules(final Properties properties, final ComponentSpaceConfigBuilder componentSpaceConfigBuilder) {
		//Possibilité d'ajouter d'autres modules à la conf.
	}

	private final void appendModuleAnalytica(final Properties properties, final ComponentSpaceConfigBuilder componentSpaceConfigBuilder) {
		final ModuleConfigBuilder moduleConfigBuilder = componentSpaceConfigBuilder.beginModule("analytica");
		if (properties.containsKey(REST_API_PORT) || properties.containsKey(REST_API_PATH)) {
			moduleConfigBuilder.beginComponent(RestServerManager.class, RestServerManagerImpl.class) //
					.withParam("apiPath", properties.getProperty(REST_API_PATH, "/rest/")) //
					.withParam("httpPort", properties.getProperty(REST_API_PORT, "8080")) //
					.endComponent();
		}
		final ComponentConfigBuilder serverConfigBuilder = moduleConfigBuilder.beginComponent(ServerManager.class, ServerManagerImpl.class);
		if (properties.containsKey(PROCESS_STORE_PATH)) {
			serverConfigBuilder.beginPlugin(BerkeleyProcessStorePlugin.class) //
					.withParam("dbPath", properties.getProperty(PROCESS_STORE_PATH)) //
					.endPlugin();
		} else {
			serverConfigBuilder.beginPlugin(MemoryProcessStorePlugin.class) //
					.endPlugin();
		}
		if (TYPE_API_REST.equals(properties.getProperty(PROCESS_API, TYPE_API_NONE))) {
			serverConfigBuilder.beginPlugin(RestProcessNetApiPlugin.class) //
					.endPlugin();
		}
		if (TYPE_API_REST.equals(properties.getProperty(QUERY_API, TYPE_API_NONE))) {
			serverConfigBuilder.beginPlugin(RestQueryNetApiPlugin.class) //
					.endPlugin();
		}
		serverConfigBuilder.endComponent();

		final ComponentConfigBuilder hCubeConfigBuilder = moduleConfigBuilder.beginComponent(HCubeManager.class, HCubeManagerImpl.class);
		if (properties.containsKey(CUBE_STORE_PATH)) {
			throw new UnsupportedOperationException("Cube persistent store not yet supported");
		} else {
			hCubeConfigBuilder.beginPlugin(MemoryCubeStorePlugin.class) //
					.endPlugin();
		}
		if (properties.containsKey(SOCKET_IO_URL)) {
			hCubeConfigBuilder.beginPlugin(SocketIoProcessStatsPlugin.class) //
					.withParam("socketIoUrl", properties.getProperty(SOCKET_IO_URL)) //
					.endPlugin();
		}
		if (Boolean.parseBoolean(properties.getProperty(STACK_PROCESS_STATS, "false"))) {
			hCubeConfigBuilder.beginPlugin(MemoryStackProcessStatsPlugin.class) //
					.endPlugin();
		}
		hCubeConfigBuilder.endComponent();
		moduleConfigBuilder.endModule();
	}

	/**
	 * Stop l'application.
	 */
	public final void stop() {
		if (started) {
			Home.stop();
			started = false;
		}
	}

	/**
	 * Charge le fichier properties.
	 * Par defaut vide, mais il peut-être surchargé.
	 * @param relativeRootClass Racine du chemin relatif, le cas echéant
	 */
	private static final void appendFileProperties(final Properties properties, final String propertiesFileName, final Class<?> relativeRootClass) {
		//---------------------------------------------------------------------
		final String fileName = translateFileName(propertiesFileName, relativeRootClass);
		try {
			final InputStream in = createURL(fileName, relativeRootClass).openStream();
			try {
				properties.load(in);
			} finally {
				in.close();
			}
		} catch (final IOException e) {
			throw new IllegalArgumentException("Impossible de charger le fichier de configuration des tests : " + fileName, e);
		}
	}

	/**
	 * Transforme le chemin vers un fichier local au test en une URL absolue.
	 * @param fileName Path du fichier : soit en absolu (commence par /), soit en relatif à la racine
	 * @param relativeRootClass Racine du chemin relatif, le cas echéant
	 * @return URL du fichier
	 */
	private static final URL createURL(final String fileName, final Class<?> relativeRootClass) {
		Assertion.checkArgNotEmpty(fileName);
		//---------------------------------------------------------------------
		final String absoluteFileName = translateFileName(fileName, relativeRootClass);
		try {
			return new URL(absoluteFileName);
		} catch (final MalformedURLException e) {
			//Si fileName non trouvé, on recherche dans le classPath 
			final URL url = relativeRootClass.getResource(absoluteFileName);
			Assertion.checkNotNull(url, "Impossible de récupérer le fichier [" + absoluteFileName + "]");
			return url;
		}
	}

	private static final String translateFileName(final String fileName, final Class<?> relativeRootClass) {
		Assertion.checkArgNotEmpty(fileName);
		//---------------------------------------------------------------------
		if (fileName.startsWith(".")) {
			//soit en relatif
			return "/" + getRelativePath(relativeRootClass) + "/" + fileName.replace("./", "");
		}

		//soit en absolu		
		if (fileName.startsWith("/")) {
			return fileName;
		}
		return "/" + fileName;
	}

	private static final String getRelativePath(final Class<?> relativeRootClass) {
		return relativeRootClass.getPackage().getName().replace('.', '/');
	}
}
