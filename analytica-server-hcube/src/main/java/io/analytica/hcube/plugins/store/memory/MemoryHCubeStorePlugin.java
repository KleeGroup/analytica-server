/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidi�re - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.HCategorySelector;
import io.analytica.hcube.HTimeSelector;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.impl.HCubeStorePlugin;
import io.analytica.hcube.query.HCategorySelection;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Impl�mentation m�moire du stockage des Cubes.
 * 
 * @author npiedeloup, pchretien
 */
public final class MemoryHCubeStorePlugin implements HCubeStorePlugin, HCategorySelector {
	private static final AppCubeStore EMPTY = new AppCubeStore("EMPTY");
	private final Map<String, AppCubeStore> appCubeStores = new HashMap<>();

	/** {@inheritDoc} */
	public synchronized void push(String appName, final HKey key, final HCube cube) {
		Assertion.checkArgNotEmpty(appName);
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		AppCubeStore appCubeStore = appCubeStores.get(appName);
		if (appCubeStore == null) {
			appCubeStore = new AppCubeStore(appName);
			appCubeStores.put(appName, appCubeStore);
		}
		appCubeStore.push(key, cube);
	}

	public synchronized long count(String appName) {
		final AppCubeStore appCubeStore = appCubeStores.get(appName);
		if (appCubeStore == null) {
			return 0;
		}
		return appCubeStore.count();
	}

	/** {@inheritDoc} */
	public synchronized List<HSerie> execute(String appName, final HQuery query, final HTimeSelector timeSelector) {
		Assertion.checkArgNotEmpty(appName);
		Assertion.checkNotNull(query);
		Assertion.checkNotNull(timeSelector);
		//---------------------------------------------------------------------
		final AppCubeStore appCubeStore = appCubeStores.get(appName);
		if (appCubeStore == null) {
			return EMPTY.findAll(query, timeSelector, this.getCategorySelector());
		}
		return appCubeStore.findAll(query, timeSelector, this.getCategorySelector());
	}

	/** {@inheritDoc} */
	public HCategorySelector getCategorySelector() {
		return this;
	}

	/** {@inheritDoc} */
	public Set<HCategory> findCategories(final String appName, final HCategorySelection categorySelection) {
		Assertion.checkNotNull(appName);
		Assertion.checkNotNull(categorySelection);
		// ---------------------------------------------------------------------
		if (categorySelection.hasChildren()) {
			return findAllSubCategories(appName, categorySelection.getCategory());
		}
		return Collections.singleton(categorySelection.getCategory());
	}

	/** {@inheritDoc} */
	public synchronized Set<HCategory> findAllRootCategories(String appName) {
		Assertion.checkArgNotEmpty(appName);
		//---------------------------------------------------------------------
		final AppCubeStore appCubeStore = appCubeStores.get(appName);
		if (appCubeStore == null) {
			return Collections.emptySet();
		}
		return appCubeStore.getAllRootCategories();
	}

	/** {@inheritDoc} */
	public synchronized Set<HCategory> findAllSubCategories(String appName, HCategory category) {
		Assertion.checkArgNotEmpty(appName);
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		final AppCubeStore appCubeStore = appCubeStores.get(appName);
		if (appCubeStore == null) {
			return Collections.emptySet();
		}
		return appCubeStore.getAllSubCategories(category);
	}
}
