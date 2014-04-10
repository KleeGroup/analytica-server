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
/**
 * 
 */
package io.analytica.hcube.impl;

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.dimension.HCategory;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author pchretien, npiedeloup 
 */
final class HCategoryDictionaryImpl implements HCategoryDictionary {
	private final Set<HCategory> rootCategories;
	private final Map<HCategory, Set<HCategory>> categories;

	HCategoryDictionaryImpl() {
		rootCategories = new HashSet<>();
		categories = new HashMap<>();
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
	public synchronized void add(HCategory category) {
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
