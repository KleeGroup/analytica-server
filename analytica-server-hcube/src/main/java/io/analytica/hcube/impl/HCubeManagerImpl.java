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

import io.analytica.hcube.HCubeManager;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.query.HCategorySelector;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.query.HTimeSelector;
import io.analytica.hcube.result.HResult;
import io.vertigo.kernel.lang.Assertion;

import javax.inject.Inject;

/**
 * @author pchretien, npiedeloup
 */
public final class HCubeManagerImpl implements HCubeManager {
	private final HCubeStorePlugin cubeStore;
	private final HSelector selector;

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
		selector = new HSelector() {
			private HTimeSelector timeSelector = new HTimeSelectorImpl();

			public HTimeSelector getTimeSelector() {
				return timeSelector;
			}

			public HCategorySelector getCategorySelector() {
				return cubeStore.getCategorySelector();
			}
		};
	}

	//	/** {@inheritDoc} */
	//	public HTimeSelector getTimeSelector() {
	//		return timeSelector;
	//	}

	/** {@inheritDoc} */
	public HResult execute(String appName, final HQuery query) {
		return new HResult(query, cubeStore.getCategorySelector().findCategories(appName, query.getCategorySelection()), cubeStore.execute(appName, query, selector));
	}

	/** {@inheritDoc} */
	public HCategorySelector getCategorySelector() {
		return cubeStore.getCategorySelector();
	}

	/** {@inheritDoc} */
	public void push(String appName, HKey key, HCube cube) {
		cubeStore.push(appName, key, cube);
	}

	/** {@inheritDoc} */
	public long size(String appName) {
		return cubeStore.count(appName);
	}

	/** {@inheritDoc} */
	public HSelector getSelector() {
		return selector;
	}
}
