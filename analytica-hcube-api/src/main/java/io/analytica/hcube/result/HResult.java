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
package io.analytica.hcube.result;

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HQuery;
import io.vertigo.lang.Assertion;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Résultat d'une requête.
 * Il s'agit d'une liste de séries.
 *
 * @author pchretien, npiedeloup
 */
public final class HResult {
	private final HQuery query;
	private final Map<HCategory, HSerie> series;
	//Liste des catagories matchant la query
	private final List<HCategory> categories;

	/**
	 * Constructeur.
	 * @param query Requete initiale
	 * @param series Liste des séries par catégorie
	 */
	public HResult(final HQuery query, final List<HCategory> categories, final List<HSerie> series) {
		Assertion.checkNotNull(query);
		Assertion.checkNotNull(categories);
		Assertion.checkNotNull(series);
		//---------------------------------------------------------------------
		this.query = query;
		this.categories = categories;
		this.series = new HashMap<>();
		for (final HSerie serie : series) {
			this.series.put(serie.getCategory(), serie);
		}
	}

	//-----------------------What----------------------------------------------
	/**
	 * Liste triée par ordre alphabétique des catégories matchant la sélection
	 */
	public List<HCategory> getAllCategories() {
		return categories;
	}

	/**
	 * @return Requete initiale
	 */
	public HQuery getQuery() {
		return query;
	}

	/**
	 * @return Serie de cette catégorie
	 */
	public HSerie getSerie(final HCategory category) {
		Assertion.checkNotNull(category);
		//	Assertion.checkArgument(series.containsKey(categories), "categories: {0} not in resultSet : ", categories);
		//-------------------------------------------------------------------------
		//		final List<HCategory> list = Arrays.asList(HKey.to(strCategories));
		final HSerie serie = series.get(category);
		Assertion.checkNotNull(serie, "category {0} not in resultSet : ", category);
		return serie;
	}
	
}
