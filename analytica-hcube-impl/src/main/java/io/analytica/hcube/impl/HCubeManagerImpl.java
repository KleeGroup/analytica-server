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
package io.analytica.hcube.impl;

import io.analytica.hcube.HApp;
import io.analytica.hcube.HCubeManager;
import io.analytica.hcube.cube.HMetricDefinition;
import io.vertigo.core.Home;
import io.vertigo.lang.Assertion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author pchretien, npiedeloup
 */
public final class HCubeManagerImpl implements HCubeManager {

	private final HCubeStorePlugin cubeStore;
	private final Map<String, HApp> APPS;

	/**
	 * Constructeur.
	 * @param cubeStorePlugin Plugin de stockage des Cubes
	 */
	@Inject
	public HCubeManagerImpl(final HCubeStorePlugin cubeStorePlugin) {
		Assertion.checkNotNull(cubeStorePlugin);
		//-----------------------------------------------------------------
		cubeStore = cubeStorePlugin;
		APPS = new HashMap<>();
		Home.getDefinitionSpace().register(HMetricDefinition.class);

	}

	@Override
	public Map<String, HApp> getApps() {
		return APPS;
	}

	@Override
	public HApp getApp(final String appName) {
		if (!APPS.containsKey(appName)) {
			APPS.put(appName, new HAppImp(cubeStore, appName));
		}
		return APPS.get(appName);
	}

	@Override
	public void register(final HMetricDefinition metricDefinition) {
		Home.getDefinitionSpace().put(metricDefinition, HMetricDefinition.class);
	}

	@Override
	public Collection<HMetricDefinition> getMetricDefinitions() {
		return Home.getDefinitionSpace().getAll(HMetricDefinition.class);
	}

	@Override
	public HMetricDefinition getMetricDefinition(final String name) {
		return Home.getDefinitionSpace().resolve(name, HMetricDefinition.class);
	}
}
