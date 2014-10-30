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
package io.analytica.hcube.dimension;

import io.vertigo.lang.Assertion;

import java.util.regex.Pattern;

/**
 * Une catégorie est composée d'une hierarchie de termes.
 *  * exemple :
 * sql; select  all products
 * Cet exemple illustre une requête de tous les produits (appellée 'all products') classée dans la catégorie sql, sous catégorie select.
 *
 * Il doit ncessairement y avoir une catégorie parente (sql) dans notre exemple.
 * @author npiedeloup, pchretien
 */
public final class HCategory {
	public static final Pattern NAME_REGEX = Pattern.compile("[a-z][a-zA-Z]*");
	private static char SEPARATOR = '/';
	private final String[] categoryTerms;
	private final String categoryPath;

	public HCategory(final String... categoryTerms) {
		Assertion.checkNotNull(categoryTerms);
		for (final String categoryTerm : categoryTerms) {
			if (!NAME_REGEX.matcher(categoryTerm).matches()) {
				throw new IllegalArgumentException("categoryTerm " + categoryTerm + " must match regex :" + NAME_REGEX);
			}
		}
		//---------------------------------------------------------------------
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String categoryTerm : categoryTerms) {
			if (!first) {
				sb.append(SEPARATOR);
			}
			sb.append(categoryTerm);
			first = false;
		}
		this.categoryTerms = categoryTerms.clone();
		this.categoryPath = sb.toString();
	}

	/**
	 * @return Upper HCategory  or null.
	 */
	public HCategory drillUp() {
		if (categoryTerms.length == 0) {
			return null;
		}
		final String[] upCategoryTerms = new String[categoryTerms.length - 1];
		for (int i = 0; i < categoryTerms.length - 1; i++) {
			upCategoryTerms[i] = categoryTerms[i];
		}
		return new HCategory(upCategoryTerms);
	}

	public final String getPath() {
		return categoryPath;
	}

	@Override
	public final int hashCode() {
		return categoryPath.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof HCategory) {
			return categoryPath.equals(((HCategory) object).categoryPath);
		}
		return false;
	}

	@Override
	public final String toString() {
		return categoryPath;
	}
}
