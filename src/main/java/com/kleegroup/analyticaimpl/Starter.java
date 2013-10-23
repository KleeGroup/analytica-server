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
import com.kleegroup.analytica.restserver.RestServerManager;
import com.kleegroup.analytica.server.ServerManager;
import com.kleegroup.analyticaimpl.hcube.HCubeManagerImpl;
import com.kleegroup.analyticaimpl.hcube.plugins.memorystack.MemoryStackProcessStatsPlugin;
import com.kleegroup.analyticaimpl.hcube.plugins.socketio.SocketIoProcessStatsPlugin;
import com.kleegroup.analyticaimpl.hcube.plugins.store.memory.MemoryCubeStorePlugin;
import com.kleegroup.analyticaimpl.restserver.RestServerManagerImpl;
import com.kleegroup.analyticaimpl.server.ServerManagerImpl;
import com.kleegroup.analyticaimpl.server.plugins.processapi.rest.RestProcessNetApiPlugin;
import com.kleegroup.analyticaimpl.server.plugins.processstore.berkeley.BerkeleyProcessStorePlugin;
import com.kleegroup.analyticaimpl.server.plugins.processstore.memory.MemoryProcessStorePlugin;
import com.kleegroup.analyticaimpl.server.plugins.queryapi.rest.RestQueryNetApiPlugin;

/**
 * Charge et d�marre un environnement.
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
	 * @param propertiesFileName Fichier de propri�t�s
	 * @param relativeRootClass Racine du chemin relatif, le cas ech�ant
	 */
	public Starter(final String propertiesFileName, final Class<?> relativeRootClass) {
		Assertion.checkNotNull(propertiesFileName);
		Assertion.checkNotNull(relativeRootClass);
		//---------------------------------------------------------------------
		this.propertiesFileName = propertiesFileName;
		this.relativeRootClass = relativeRootClass;
	}

	/**
	 * Lance l'environnement et attend ind�finiment.
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
	public final void run() {
		try {
			start();

			final Object lock = new Object();
			synchronized (lock) {
				lock.wait(0); //on attend le temps demand� et 0 => illimit�
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
	 * D�marre l'application.
	 * @throws IOException Erreur de cr�ation du server Web
	 * @throws NumberFormatException Erreur de format du port
	 */
	public final void start() throws NumberFormatException, IOException {
		// Cr�ation de l'�tat de l'application
		// Initialisation de l'�tat de l'application
		final Properties properties = new Properties();
		appendFileProperties(properties, propertiesFileName, relativeRootClass);
		final ComponentSpaceConfig componentSpaceConfig = createComponentSpaceConfig(properties);
		Home.start(componentSpaceConfig);
		started = true;
	}

	/* Conf �quivalente � :
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

	/**
	 * @param properties Propri�t�s de l'environnement.
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
	 * Ajoute d'autre modules � la configuration de l'environnement.
	 * @param properties  Propri�t�s de l'environnement.
	 * @param componentSpaceConfigBuilder Builder de la configuration de l'environnement
	 */
	protected void appendOtherModules(final Properties properties, final ComponentSpaceConfigBuilder componentSpaceConfigBuilder) {
		//Possibilit� d'ajouter d'autres modules � la conf.
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
	 * Par defaut vide, mais il peut-�tre surcharg�.
	 * @param relativeRootClass Racine du chemin relatif, le cas ech�ant
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
	 * @param fileName Path du fichier : soit en absolu (commence par /), soit en relatif � la racine
	 * @param relativeRootClass Racine du chemin relatif, le cas ech�ant
	 * @return URL du fichier
	 */
	private static final URL createURL(final String fileName, final Class<?> relativeRootClass) {
		Assertion.checkArgNotEmpty(fileName);
		//---------------------------------------------------------------------
		final String absoluteFileName = translateFileName(fileName, relativeRootClass);
		try {
			return new URL(absoluteFileName);
		} catch (final MalformedURLException e) {
			//Si fileName non trouv�, on recherche dans le classPath 
			final URL url = relativeRootClass.getResource(absoluteFileName);
			Assertion.checkNotNull(url, "Impossible de r�cup�rer le fichier [" + absoluteFileName + "]");
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
