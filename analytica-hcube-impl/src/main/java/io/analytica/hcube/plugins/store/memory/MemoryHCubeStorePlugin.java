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
package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.impl.HCubeStorePlugin;
import io.analytica.hcube.query.HCategorySelection;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.result.HSerie;
import io.vertigo.lang.Assertion;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implémentation mémoire du stockage des Cubes.
 *
 * @author npiedeloup, pchretien
 */
public final class MemoryHCubeStorePlugin implements HCubeStorePlugin {
	private final Set<String> appNames = new HashSet<>();
	private final Map<String, AppCubeStore> appCubeStores = new HashMap<>();
	private final Map<String, AppCategoryStore> appCategoryStores = new HashMap<>();
	private static final AppCubeStore EMPTY = new AppCubeStore();

	/** {@inheritDoc} */
	public synchronized void push(final String appName, final HKey key, final HCube cube) {
		Assertion.checkArgNotEmpty(appName);
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		final AppCubeStore appCubeStore;
		final AppCategoryStore appCategoryStore;
		if (appNames.contains(appName)) {
			appCubeStore = appCubeStores.get(appName);
			appCategoryStore = appCategoryStores.get(appName);
		} else {
			appCubeStore = new AppCubeStore();
			appCategoryStore = new AppCategoryStore(appName);
			appCubeStores.put(appName, appCubeStore);
			appCategoryStores.put(appName, appCategoryStore);
			appNames.add(appName);
		}

		appCubeStore.push(key, cube);
		appCategoryStore.addCategories(key.getCategories());
	}

	public synchronized long size(final String appName) {
		final AppCubeStore appCubeStore = appCubeStores.get(appName);
		if (appCubeStore == null) {
			return 0;
		}
		return appCubeStore.size();
	}

	/** {@inheritDoc} */
	public synchronized List<HSerie> execute(final String appName, final HQuery query, final HSelector selector) {
		Assertion.checkArgNotEmpty(appName);
		Assertion.checkNotNull(query);
		Assertion.checkNotNull(selector);
		//---------------------------------------------------------------------
		if (!appNames.contains(appName)) {
			return EMPTY.findAll(query, selector);
		}
		final AppCubeStore appCubeStore = appCubeStores.get(appName);
		return appCubeStore.findAll(query, selector);
	}

	/** {@inheritDoc} */
	public synchronized Set<List<HCategory>> findCategories(final String appName, final HCategorySelection categorySelection) {
		Assertion.checkArgNotEmpty(appName);
		Assertion.checkNotNull(categorySelection);
		//---------------------------------------------------------------------
		if (!appNames.contains(appName)) {
			return Collections.emptySet();
		}
		return appCategoryStores.get(appName).findCategories(categorySelection);
	}

	/** {@inheritDoc} */
	public synchronized Set<String> getAppNames() {
		return Collections.unmodifiableSet(appNames);
	}
}
