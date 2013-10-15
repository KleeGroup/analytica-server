package com.kleegroup.analyticaimpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import vertigo.kernel.Home;
import vertigo.kernel.di.configurator.ComponentConfigBuilder;
import vertigo.kernel.di.configurator.ComponentSpaceConfig;
import vertigo.kernel.di.configurator.ComponentSpaceConfigBuilder;
import vertigo.kernel.di.configurator.ModuleConfigBuilder;
import vertigo.kernel.lang.Assertion;

import com.kleegroup.analytica.hcube.HCubeManager;
import com.kleegroup.analytica.server.ServerManager;
import com.kleegroup.analyticaimpl.hcube.HCubeManagerImpl;
import com.kleegroup.analyticaimpl.hcube.plugins.memorystack.MemoryStackProcessStatsPlugin;
import com.kleegroup.analyticaimpl.hcube.plugins.socketio.SocketIoProcessStatsPlugin;
import com.kleegroup.analyticaimpl.hcube.plugins.store.memory.MemoryCubeStorePlugin;
import com.kleegroup.analyticaimpl.server.ServerManagerImpl;
import com.kleegroup.analyticaimpl.server.plugins.processapi.rest.RestProcessNetApiPlugin;
import com.kleegroup.analyticaimpl.server.plugins.processstore.berkeley.BerkeleyProcessStorePlugin;
import com.kleegroup.analyticaimpl.server.plugins.processstore.memory.MemoryProcessStorePlugin;
import com.kleegroup.analyticaimpl.server.plugins.queryapi.rest.RestQueryNetApiPlugin;

/**
 * Charge et démarre un environnement.
 * @author pchretien, npiedeloup
 */
public final class Starter implements Runnable {
	private static final String PROCESS_STORE_PATH = "processStorePath";
	private static final String CUBE_STORE_PATH = "cubeStorePath";
	private static final String SOCKET_IO_URL = "socketIoUrl";
	private static final String STACK_PROCESS_STATS = "stackProcessStats";
	private static final String REST_PROCESS_API_PORT = "restProcessApiPort";
	private static final String REST_QUERY_API_PORT = "restQueryApiPort";
	private static boolean SILENCE = true;
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
		final String usageMsg = "Usage: java com.kleegroup.analytica.Starter <conf.properties>";
		Assertion.checkArgument(args.length == 1, usageMsg + " ( conf attendue : " + args.length + ")");
		Assertion.checkArgument(args[0].endsWith(".properties"), usageMsg + " ( .properties attendu : " + args[0] + ")");
		//---------------------------------------------------------------------
		final String propertiesFileName = args[0];
		final Starter starter = new Starter(propertiesFileName, Starter.class);
		starter.run();
	}

	/** {@inheritDoc} */
	public void run() {
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

	/* Conf équivalente à :
	 * <module name="analytica">
	<component api="ServerManager" class="com.kleegroup.analyticaimpl.server.ServerManagerImpl" >
	   <plugin class="com.kleegroup.analyticaimpl.server.plugins.processstore.berkeley.BerkeleyProcessStorePlugin">
	    	<param name="dbPath" value="d:/analytica/db-test" />
	    </plugin>
	 </component>
	 <component api="HCubeManager" class="com.kleegroup.analyticaimpl.hcube.HCubeManagerImpl">
			<plugin class="com.kleegroup.analyticaimpl.hcube.plugins.store.memory.MemoryCubeStorePlugin"/>
			<plugin class="com.kleegroup.analyticaimpl.hcube.plugins.socketio.SocketIoProcessStatsPlugin">
				<param name="socketIoUrl" value="http://npiedeloup1:8090" />
			</plugin>
		</component>
	</module>*/
	private ComponentSpaceConfig createComponentSpaceConfig(final Properties properties) {
		final ComponentSpaceConfigBuilder componentSpaceConfigBuilder = new ComponentSpaceConfigBuilder() //
				.withSilence(SILENCE);
		final ModuleConfigBuilder moduleConfigBuilder = componentSpaceConfigBuilder.beginModule("analytica");
		final ComponentConfigBuilder serverConfigBuilder = moduleConfigBuilder.beginComponent(ServerManager.class, ServerManagerImpl.class);
		if (properties.containsKey(PROCESS_STORE_PATH)) {
			serverConfigBuilder.beginPlugin(BerkeleyProcessStorePlugin.class) //
					.withParam("dbPath", properties.getProperty(PROCESS_STORE_PATH)) //
					.endPlugin();
		} else {
			serverConfigBuilder.beginPlugin(MemoryProcessStorePlugin.class) //
					.endPlugin();
		}
		if (properties.containsKey(REST_PROCESS_API_PORT)) {
			serverConfigBuilder.beginPlugin(RestProcessNetApiPlugin.class) //
					.withParam("httpPort", properties.getProperty(REST_PROCESS_API_PORT)) //
					.endPlugin();
		}
		if (properties.containsKey(REST_QUERY_API_PORT)) {
			serverConfigBuilder.beginPlugin(RestQueryNetApiPlugin.class) //
					.withParam("httpPort", properties.getProperty(REST_QUERY_API_PORT)) //
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
		return componentSpaceConfigBuilder.build();
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
