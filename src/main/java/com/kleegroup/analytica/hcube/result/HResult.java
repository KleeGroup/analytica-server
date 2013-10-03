package com.kleegroup.analytica.hcube.result;

import java.util.Map;
import java.util.Set;

import vertigo.kernel.lang.Assertion;

import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.query.HQuery;

/**
 * Résultat d'une requête.
 * Il s'agit d'une liste de séries.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
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
	public HResult(final HQuery query, Set<HCategory> categories, final Map<HCategory, HSerie> series) {
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
