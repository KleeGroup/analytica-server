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

import java.util.Arrays;
import java.util.List;

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
	private static final String REGEX = "[a-zA-Z]*";
	private static char SEPARATOR = '/';
	private final int level;
	private final String id;

	public HCategory(final String type) {
		this(Arrays.asList(type.split("/")));
	}

	public HCategory(final List<String> subTypes) {
		Assertion.checkNotNull(subTypes);
		//---------------------------------------------------------------------
		id = buildKey(subTypes);
		level = subTypes.size();
		//		this.subTypes = subTypes;
		//		this.type = type;
	}

	/**
	 * @return Upper HCategory  or null.
	 */
	public HCategory drillUp() {
		if (level == 1) {
			return null;
		}
		int i = id.length();
		while (id.charAt(i - 1) != SEPARATOR) {
			i--;
		}
		return new HCategory(id.substring(0, i));
	}

	public final String getId() {
		return id;
	}

	private static String buildKey(final List<String> subTypes) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String subType : subTypes) {
			Assertion.checkArgument(subType.matches(REGEX), " type and subtypes must contain only letters");
			if (first) {
				first = false;
			} else {
				sb.append(SEPARATOR);
			}
			sb.append(subType);
		}
		return sb.toString();
	}

	@Override
	public final int hashCode() {
		return id.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof HCategory) {
			return id.equals(((HCategory) object).id);
		}
		return false;
	}

	@Override
	public final String toString() {
		return id;
	}
}
