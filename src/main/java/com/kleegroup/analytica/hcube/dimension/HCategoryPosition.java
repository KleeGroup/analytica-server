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
package com.kleegroup.analytica.hcube.dimension;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.HKey;

/**
 * Une position de type What est composée d'une hierarchie de terms.
 * exemple :
 * sql; select  all products
 * Cet exemple illustre une requête de tous les produits (appellée 'all products') classée dans la catégorie sql, sous catégorie select.
 * 
 * Il doit ncessairement y avoir une catégorie parente (sql) dans notre exemple. 
 * @author npiedeloup, pchretien
 * @version $Id: WhatPosition.java,v 1.2 2012/04/17 09:11:15 pchretien Exp $
 */
public final class HCategoryPosition extends HKey implements HPosition<HCategoryPosition> {
	private final String category;
	private final String[] subCategories;

	public HCategoryPosition(final String category, final String[] subCategories) {
		super(buildKey(category, subCategories));
		Assertion.notEmpty(category);
		//---------------------------------------------------------------------
		this.subCategories = subCategories;
		this.category = category;
	}

	/** {@inheritDoc} */
	public HCategoryPosition drillUp() {
		if (subCategories.length == 0) {
			return null;
		}
		String[] redux = new String[subCategories.length - 1];
		for (int i = 0; i < subCategories.length - 1; i++) {
			redux[i] = subCategories[i];
		}
		return new HCategoryPosition(category, redux);
	}

	private static String buildKey(String type, final String[] subCategory) {
		StringBuilder sb = new StringBuilder("subCategory::").append(type);
		for (String element : subCategory) {
			sb.append("/").append(element);
		}
		return sb.toString();
	}
}
