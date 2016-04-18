package io.analytica.ui;

import java.util.Properties;

import javax.inject.Inject;

import io.analytica.restserver.RestServerManager;
import io.analytica.restserver.impl.RestServerManagerImpl;
import io.analytica.server.ServerManager;
import io.analytica.server.aggregator.impl.influxDB.InfluxDBProcessAggregatorPlugin;
import io.analytica.server.impl.ServerManagerImpl;
import io.analytica.server.plugins.processapi.rest.RestProcessNetApiPlugin;
import io.analytica.server.plugins.processstats.memorystack.MemoryStackProcessStatsPlugin;
import io.analytica.server.plugins.processstats.socketio.SocketIoProcessStatsPlugin;
import io.analytica.server.plugins.processstore.berkeley.BerkeleyProcessStorePlugin;
import io.analytica.server.plugins.queryapi.rest.RestQueryNetApiPlugin;
import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.app.config.ComponentConfigBuilder;
import io.vertigo.app.config.LogConfig;
import io.vertigo.app.config.ModuleConfigBuilder;
import io.vertigo.core.param.ParamManager;
import io.vertigo.core.plugins.param.properties.PropertiesParamPlugin;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.core.plugins.resource.local.LocalResourceResolverPlugin;

public class AnalyticaServerUiConfigurator {
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
	
	private static final String AGGREGATOR_HTTP_ADRESSE = "aggregatorHttpAdresse";
	private static final String AGGREGATOR_HTTP_PORT = "aggregatorHttpPort";
	private static final String AGGREGATOR_USERNAME = "aggregatorUsername";
	private static final String AGGREGATOR_PASSWORD = "aggregatorPassword";
	private static final String AGGREGATOR_MIN_SIZE = "aggregatorCacheMinSize";
	final String locales = "fr_FR";
	
	@Inject
	private static ParamManager paramManager;
	
	public static AppConfig config(final String propertiesFileName){
	
		
		final AppConfigBuilder appConfigBuilder = new AppConfigBuilder()
				.beginBootModule("locales")
					.addPlugin(ClassPathResourceResolverPlugin.class)
					.beginPlugin( PropertiesParamPlugin.class)
					.addParam("url", "io/analytica/ui/analyticaConf.properties")
				.endPlugin()
				.endModule();
		appConfigBuilder.beginModule("conf").endModule();
//		final ModuleConfigBuilder moduleConfigBuilder = appConfigBuilder.beginModule("analytica");
//		moduleConfigBuilder.beginComponent(RestServerManager.class, RestServerManagerImpl.class) //
//		.addParam("apiPath",  "/rest/")
//		.addParam("httpPort",  "8080")
//		.endComponent();
//		moduleConfigBuilder.endModule();
		return appConfigBuilder.build();
	
		
//		return null;
//		final Properties properties = new Properties();
//		appendFileProperties(properties, propertiesFileName, relativeRootClass);
//		final ModuleConfigBuilder moduleConfigBuilder = appConfigBuilder.beginModule("analytica");
//		if (properties.containsKey(REST_API_PORT) || properties.containsKey(REST_API_PATH)) {
//			moduleConfigBuilder.beginComponent(RestServerManager.class, RestServerManagerImpl.class) //
//					.addParam("apiPath", properties.getProperty(REST_API_PATH, "/rest/"))
//					.addParam("httpPort", properties.getProperty(REST_API_PORT, "8080"))
//					.endComponent();
//		}
//		final ComponentConfigBuilder serverConfigBuilder = moduleConfigBuilder.beginComponent(ServerManager.class, ServerManagerImpl.class);
//		if (properties.containsKey(PROCESS_STORE_PATH)) {
//			moduleConfigBuilder.beginPlugin(BerkeleyProcessStorePlugin.class)
//					.addParam("dbPath", properties.getProperty(PROCESS_STORE_PATH))
//					.endPlugin();
//		}
//
//		if (TYPE_API_REST.equals(properties.getProperty(PROCESS_API, TYPE_API_NONE))) {
//			moduleConfigBuilder.addPlugin(RestProcessNetApiPlugin.class);
//		}
//		if (TYPE_API_REST.equals(properties.getProperty(QUERY_API, TYPE_API_NONE))) {
//			moduleConfigBuilder.addPlugin(RestQueryNetApiPlugin.class);
//		}
//		moduleConfigBuilder.beginPlugin(InfluxDBProcessAggregatorPlugin.class)
//		.addParam("httpAddresse", properties.getProperty(AGGREGATOR_HTTP_ADRESSE))
//		.addParam("port", properties.getProperty(AGGREGATOR_HTTP_PORT))
//		.addParam("username", properties.getProperty(AGGREGATOR_USERNAME))
//		.addParam("password", properties.getProperty(AGGREGATOR_PASSWORD))
//		.addParam("flushMinSize", properties.getProperty(AGGREGATOR_MIN_SIZE))
//		.endPlugin();
//		
////		moduleConfigBuilder.addComponent(HCubeManager.class, HCubeManagerImpl.class)
////				.beginPlugin(LuceneHCubeStorePlugin.class)
////				.addParam("path", properties.getProperty(CUBE_STORE_PATH))
////				.endPlugin();
//		if (properties.containsKey(SOCKET_IO_URL)) {
//			moduleConfigBuilder.beginPlugin(SocketIoProcessStatsPlugin.class)
//					.addParam("socketIoUrl", properties.getProperty(SOCKET_IO_URL))
//					.endPlugin();
//		}
//		if (Boolean.parseBoolean(properties.getProperty(STACK_PROCESS_STATS, "false"))) {
//			moduleConfigBuilder.addPlugin(MemoryStackProcessStatsPlugin.class);
//		}
//		moduleConfigBuilder.endModule();
	}
}