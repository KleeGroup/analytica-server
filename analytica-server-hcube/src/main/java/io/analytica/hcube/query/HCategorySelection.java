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
package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HCategory;
import io.vertigo.kernel.lang.Assertion;

/**
 * Selection de catégories permettant de définir un ensemble de positions sur un niveau donné. 
 * exemple : 
 * - toutes les catégories racine  : new HCategorySelection() 
 * - toutes les sous-catégories de SQL, select : HCategorySelection(new HCategoryPosition("SQL", "select"), true);
 * 
 * @author npiedeloup, pchretien, statchum
 */
public final class HCategorySelection {
	private final String pattern;

	public HCategorySelection(final String pattern) {
		Assertion.checkNotNull(pattern);
		// ---------------------------------------------------------------------
		this.pattern = pattern;
	}

	public boolean matches(HCategory category) {
		Assertion.checkNotNull(category);
		// ---------------------------------------------------------------------
		if (pattern.endsWith("*")) {
			return category.getPath().startsWith(pattern.substring(pattern.length()));
		}
		return category.getPath().equals(pattern);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "{ pattern:" + pattern + "}";
	}
}
