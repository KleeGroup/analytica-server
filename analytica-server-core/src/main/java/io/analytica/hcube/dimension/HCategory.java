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

import io.analytica.hcube.HKey;
import io.vertigo.kernel.lang.Assertion;

/**
 * Une catégorie est composée d'une hierarchie de termes.
 *  * exemple :
 * sql; select  all products
 * Cet exemple illustre une requête de tous les produits (appellée 'all products') classée dans la catégorie sql, sous catégorie select.
 * 
 * Il doit ncessairement y avoir une catégorie parente (sql) dans notre exemple. 
 * @author npiedeloup, pchretien
 * @version $Id: WhatPosition.java,v 1.2 2012/04/17 09:11:15 pchretien Exp $
 */
public final class HCategory extends HKey implements HPosition<HCategory> {
	private final String type;
	private final String[] subTypes;

	public HCategory(final String type) {
		this(type, new String[0]);
	}

	public HCategory(final String type, final String[] subTypes) {
		super(buildKey(type, subTypes));
		Assertion.checkArgNotEmpty(type);
		//---------------------------------------------------------------------
		this.subTypes = subTypes;
		this.type = type;
	}

	/** {@inheritDoc} */
	public HCategory drillUp() {
		if (subTypes.length == 0) {
			return null;
		}
		final String[] redux = new String[subTypes.length - 1];
		for (int i = 0; i < subTypes.length - 1; i++) {
			redux[i] = subTypes[i];
		}
		return new HCategory(type, redux);
	}

	public String[] getValue() {
		return subTypes;
	}

	private static String buildKey(final String type, final String[] subCategory) {
		final StringBuilder sb = new StringBuilder("subCategory::").append(type);
		for (final String element : subCategory) {
			sb.append("/").append(element);
		}
		return sb.toString();
	}
}
