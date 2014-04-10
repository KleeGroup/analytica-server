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

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.impl.HCubeStorePlugin;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implémentation mémoire du stockage des Cubes.
 * 
 * @author npiedeloup, pchretien
 */
public final class MemoryHCubeStorePlugin implements HCubeStorePlugin {
	private static final AppCubeStore EMPTY = new AppCubeStore();
	private final Map<String, AppCubeStore> appCubeStores = new HashMap<>();
	//===
	private final Set<HCategory> rootCategories;
	private final Map<HCategory, Set<HCategory>> categories;

	public MemoryHCubeStorePlugin() {
		rootCategories = new HashSet<>();
		categories = new HashMap<>();
	}

	/** {@inheritDoc} */
	public synchronized void merge(String appName, final HCube cube) {
		Assertion.checkArgNotEmpty(appName);
		//---------------------------------------------------------------------
		addCategory(cube.getKey().getCategory());
		AppCubeStore appCubeStore = appCubeStores.get(appName);
		if (appCubeStore == null) {
			appCubeStore = new AppCubeStore();
			appCubeStores.put(appName, appCubeStore);
		}
		appCubeStore.merge(cube);
	}

	/** {@inheritDoc} */
	public synchronized Map<HCategory, HSerie> findAll(final String appName, final HQuery query, final HCategoryDictionary categoryDictionary) {
		final AppCubeStore appCubeStore = appCubeStores.get(appName);
		if (appCubeStore == null) {
			return EMPTY.findAll(query, categoryDictionary);
		}
		return appCubeStore.findAll(query, categoryDictionary);
	}

	/** {@inheritDoc} */
	public synchronized Set<HCategory> getAllRootCategories() {
		return Collections.unmodifiableSet(rootCategories);

	}

	/** {@inheritDoc} */
	public synchronized Set<HCategory> getAllSubCategories(HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		Set<HCategory> set = categories.get(category);
		return set == null ? Collections.<HCategory> emptySet() : Collections.unmodifiableSet(set);
	}

	/** {@inheritDoc} */
	private void addCategory(HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		HCategory currentCategory = category;
		HCategory parentCategory;
		boolean drillUp;
		do {
			parentCategory = currentCategory.drillUp();
			//Optim :Si la catégorie existe déjà alors sa partie gauche aussi !!
			//On dispose donc d'une info pour savoir si il faut remonter 
			drillUp = doPut(parentCategory, currentCategory);
			currentCategory = parentCategory;
		} while (drillUp);
	}

	private boolean doPut(HCategory parentCategory, HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		if (parentCategory == null) {
			//category est une catégorie racine
			rootCategories.add(category);
			return false;
		}
		//category n'est pas une catégorie racine
		Set<HCategory> set = categories.get(parentCategory);
		if (set == null) {
			set = new HashSet<>();
			categories.put(parentCategory, set);
		}
		return set.add(category);
	}

}
