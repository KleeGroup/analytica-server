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
package com.kleegroup.analytica.hcube.query;

import java.util.Collections;
import java.util.Set;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.HCategoryDictionary;
import com.kleegroup.analytica.hcube.dimension.HCategory;

/**
 * Selection de catégories permettant de définir un ensemble de positions sur un niveau donné. 
 * exemple : 
 * - toutes les catégories racine  : new HCategorySelection() 
 * - toutes les sous-catégories de SQL, select : HCategorySelection(new HCategoryPosition("SQL", "select"), true);
 * 
 * @author npiedeloup, pchretien, statchum
 */
final class HCategorySelection {
	private final HCategory selectedCategory;
	private final boolean children;
	private final Set<HCategory> categories;

	HCategorySelection(final HCategoryDictionary categoryDictionary, final HCategory category, final boolean children) {
		Assertion.notNull(categoryDictionary);
		Assertion.notNull(category);
		// ---------------------------------------------------------------------
		this.children = children;
		selectedCategory = category;
		if (children) {
			categories = categoryDictionary.getAllSubCategories(category);
		} else {
			categories = Collections.singleton(category);
		}
	}

	Set<HCategory> getAllCategories() {
		return categories;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "categories = " + (children ? "children of " : "") + selectedCategory;
	}
}
