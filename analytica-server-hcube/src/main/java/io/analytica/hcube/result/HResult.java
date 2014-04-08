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
import io.vertigo.kernel.lang.Assertion;

import java.util.Map;
import java.util.Set;

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
	private final Set<HCategory> categories;

	/**
	 * Constructeur.
	 * @param query Requete initiale
	 * @param series Liste des séries par catégorie
	 */
	public HResult(final HQuery query, final Set<HCategory> categories, final Map<HCategory, HSerie> series) {
		Assertion.checkNotNull(query);
		Assertion.checkNotNull(categories);
		Assertion.checkNotNull(series);
		//---------------------------------------------------------------------
		this.query = query;
		this.categories = categories;
		this.series = series;
	}

	//-----------------------What----------------------------------------------
	/**
	 * Liste triée par ordre alphabétique des catégories matchant la sélection
	 * @return
	 */
	public Set<HCategory> getAllCategories() {
		return categories;
	}

	/**
	 * @return Requete initiale
	 */
	public HQuery getQuery() {
		return query;
	}

	/**
	 * @param category Catégorie demandée
	 * @return Serie de cette catégorie
	 */
	public HSerie getSerie(final HCategory category) {
		Assertion.checkNotNull(category);
		Assertion.checkArgument(series.containsKey(category), "{0} not in resultSet", category);
		//-------------------------------------------------------------------------
		return series.get(category);
	}
}
