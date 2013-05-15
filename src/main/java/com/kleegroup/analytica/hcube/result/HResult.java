package com.kleegroup.analytica.hcube.result;

import java.util.Map;

import kasper.kernel.util.Assertion;

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

	/**
	 * Constructeur.
	 * @param query Requete initiale
	 * @param series Liste des séries par catégorie
	 */
	public HResult(final HQuery query, final Map<HCategory, HSerie> series) {
		Assertion.notNull(query);
		Assertion.notNull(series);
		//---------------------------------------------------------------------
		this.query = query;
		this.series = series;
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
		Assertion.notNull(category);
		Assertion.precondition(series.containsKey(category), "{0} not in resultSet", category);
		//-------------------------------------------------------------------------
		return series.get(category);
	}
}
