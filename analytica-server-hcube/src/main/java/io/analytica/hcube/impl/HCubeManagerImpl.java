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

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.HCubeManager;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HResult;
import io.vertigo.kernel.lang.Assertion;

import javax.inject.Inject;

/**
 * @author pchretien, npiedeloup
 */
public final class HCubeManagerImpl implements HCubeManager {
	private final HCubeStorePlugin cubeStorePlugin;

	/**
	 * Constructeur.
	 * @param cubeStorePlugin Plugin de stockage des Cubes
	 * @param processStatsPlugin Plugin de statistique des process
	 */
	@Inject
	public HCubeManagerImpl(final HCubeStorePlugin cubeStorePlugin) {
		Assertion.checkNotNull(cubeStorePlugin);
		//-----------------------------------------------------------------
		this.cubeStorePlugin = cubeStorePlugin;
		//this.processStatsPlugin = Option.option(processStatsPlugin);
	}

	/** {@inheritDoc} */
	public void push(String appName, final HCube cube) {
		//---Alimentation du dictionnaire des catégories puis des cubes
		cubeStorePlugin.merge(appName, cube);
	}

	/** {@inheritDoc} */
	public HResult execute(String appName, final HQuery query) {
		return new HResult(query, query.getAllCategories(getCategoryDictionary()), cubeStorePlugin.findAll(appName, query, getCategoryDictionary()));
	}

	/** {@inheritDoc} */
	public HCategoryDictionary getCategoryDictionary() {
		return cubeStorePlugin;
	}
}
