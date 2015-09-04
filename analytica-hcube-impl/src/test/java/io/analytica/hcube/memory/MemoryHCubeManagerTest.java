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
package io.analytica.hcube.memory;

import io.analytica.hcube.HCubeManager;
import io.analytica.hcube.AbstractHCubeManagerTest;
import io.analytica.hcube.impl.HCubeManagerImpl;
import io.analytica.hcube.plugins.store.memory.MemoryHCubeStorePlugin;
import io.vertigo.core.config.AppConfig;
import io.vertigo.core.config.AppConfigBuilder;

/**
 * Test.
 *
 *  - a request ==> page, duration, status
 *
 * @author pchretien
 */
public final class MemoryHCubeManagerTest extends AbstractHCubeManagerTest {
	@Override
	public AppConfig buildAppConfig() {
		return new AppConfigBuilder()
				.beginModule("analytica")
				.addComponent(HCubeManager.class, HCubeManagerImpl.class)
				.addPlugin(MemoryHCubeStorePlugin.class)
				.endModule()
				.build();
	}
}
