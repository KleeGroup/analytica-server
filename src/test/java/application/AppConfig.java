package application;

import kasper.kernel.configurator.HomeConfig;
import kasper.kernel.configurator.HomeConfigBuilder;
import kasper.resource.ResourceManager;
import kasperimpl.resource.ResourceManagerImpl;
import kasperimpl.resource.plugins.java.ClassPathResourceResolverPlugin;
import kasperimpl.spaces.spaces.SpacesManager;
import kasperimpl.spaces.spaces.SpacesManagerImpl;

import com.kleegroup.analytica.hcube.HCubeManager;
import com.kleegroup.analytica.server.ServerManager;
import com.kleegroup.analyticaimpl.hcube.HCubeManagerImpl;
import com.kleegroup.analyticaimpl.server.ServerManagerImpl;
import com.kleegroup.analyticaimpl.server.plugins.processstore.berkeley.BerkeleyProcessStorePlugin;

final class AppConfig {

	static HomeConfig createHomeConfig() {
		//@formatter:off
		return new HomeConfigBuilder()
				.beginModule("core")
					.beginManager(ResourceManager.class, ResourceManagerImpl.class)
						.beginPlugin("ResourceResolver", ClassPathResourceResolverPlugin.class).endPlugin()
					.endManager()
					.beginManager(SpacesManager.class, SpacesManagerImpl.class).endManager()
				.endModule()
				
				.beginModule("analytica")
					.beginManager(HCubeManager.class, HCubeManagerImpl.class)
						.beginPlugin("CubeStorePlugin", com.kleegroup.analyticaimpl.hcube.plugins.store.memory.MemoryCubeStorePlugin.class).endPlugin()
					.endManager()
					.beginManager(ServerManager.class, ServerManagerImpl.class)
						.beginPlugin("ProcessStorePlugin", BerkeleyProcessStorePlugin.class)
							.withParam("dbPath", "d:/analytica/db")
						.endPlugin()
					.endManager()
				.endModule()
			.build();
		//@formatter:on
	}
}
