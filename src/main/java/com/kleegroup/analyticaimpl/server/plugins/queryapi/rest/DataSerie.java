package com.kleegroup.analyticaimpl.server.plugins.queryapi.rest;

import java.util.Map;

import vertigo.kernel.lang.Assertion;

/*
 * Couple(categorie, metriques)
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public final class DataSerie {
	private final String category;
	private final Map<String, Double> values;

	public DataSerie(final String category, final Map<String, Double> values) {
		Assertion.checkArgNotEmpty(category);
		//---------------------------------------------------------------------
		this.category = category;
		this.values = values;
	}
}
