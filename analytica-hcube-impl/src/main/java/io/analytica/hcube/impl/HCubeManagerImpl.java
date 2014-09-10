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
import io.vertigo.core.lang.Assertion;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

/**
 * @author pchretien, npiedeloup
 */
public final class HCubeManagerImpl implements HCubeManager {
	private final HCubeStorePlugin cubeStore;

	/**
	 * Constructeur.
	 * @param cubeStorePlugin Plugin de stockage des Cubes
	 * @param processStatsPlugin Plugin de statistique des process
	 */
	@Inject
	public HCubeManagerImpl(final HCubeStorePlugin cubeStorePlugin) {
		Assertion.checkNotNull(cubeStorePlugin);
		//-----------------------------------------------------------------
		this.cubeStore = cubeStorePlugin;

		APP = new HAppImp(cubeStore, "MY-APP");
		Home.getDefinitionSpace().register(HMetricDefinition.class);

	}

	/** {@inheritDoc} */
	public Set<String> getAppNames() {
		return cubeStore.getAppNames();
	}

	private final HApp APP;

	public Set<HApp> getApps() {
		return Collections.singleton(APP);
	}

	public HApp getApp(final String appName) {
		return APP;
	}

	public void register(final HMetricDefinition metricDefinition) {
		Home.getDefinitionSpace().put(metricDefinition, HMetricDefinition.class);
	}

	public Collection<HMetricDefinition> getMetricDefinitions() {
		return Home.getDefinitionSpace().getAll(HMetricDefinition.class);
	}

	public HMetricDefinition getMetricDefinition(final String name) {
		return Home.getDefinitionSpace().resolve(name, HMetricDefinition.class);
	}
}
