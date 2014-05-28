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

import io.vertigo.kernel.lang.Assertion;

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
	private static final String REGEX = "([a-z]([a-z]|/))*[a-z]";
	private static char SEPARATOR = '/';
	private final String path;

	public HCategory(final String path) {
		Assertion.checkNotNull(path);
		Assertion.checkArgument(path.length() == 0 || path.matches(REGEX), "category '{0}'must contain only letters separated with '/' like 'aaa/bbb/ccc'", path);
		//---------------------------------------------------------------------
		this.path = path;
	}

	/**
	 * @return Upper HCategory  or null.
	 */
	public HCategory drillUp() {
		if (path.indexOf(SEPARATOR) == -1) {
			return (path.length() > 0) ? new HCategory("") : null;
		}
		int i = path.length();
		while (path.charAt(i - 1) != SEPARATOR) {
			i--;
		}
		return new HCategory(path.substring(0, i));
	}

	public final String getPath() {
		return path;
	}

	@Override
	public final int hashCode() {
		return path.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof HCategory) {
			return path.equals(((HCategory) object).path);
		}
		return false;
	}

	@Override
	public final String toString() {
		return path;
	}
}
