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
package com.kleegroup.analyticaimpl.ui.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import kasperimpl.spaces.spaces.kraft.DataPoint;

import com.google.gson.Gson;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.result.HResult;
import com.kleegroup.analytica.hcube.result.HSerie;
import com.kleegroup.analytica.server.ServerManager;

/**
 * @author npiedeloup
 * @version $Id: AnalyticaDashboardService.java,v 1.4 2013/01/14 16:35:20 npiedeloup Exp $
 */
@ApplicationScoped()
@ManagedBean(name = "analyticaDashboardService")
public final class AnalyticaDashboardService implements Serializable {
	private static final long serialVersionUID = 855858989288016205L;
	@Inject
	private ServerManager serverManager;

	public String loadTestDataAsJson(final AnalyticaPanelConf analyticaPanelConf) {
		final Gson gson = new Gson();
		return gson.toJson(loadTestData(analyticaPanelConf));
	}

	public List<DataPoint> loadTestData(final AnalyticaPanelConf analyticaPanelConf) {
		System.out.println("-----------------------------------------------------------------------------------------------------------------");
		final HQuery query = analyticaPanelConf.getQuery();
		final HResult result = serverManager.execute(query);
		final List<HSerie> series = new ArrayList<HSerie>();
		System.out.println("Toutes les catégories" + query.getAllCategories());
		System.out.println("Nombre de categories enregistres " + query.getAllCategories().size());
		for (final HCategory category : query.getAllCategories()) {
			series.add(result.getSerie(category));
			System.out.println("Category In result:" + category.toString() + "---------------------------------------------------------------------------------------------");
		}
		final List<String> metricKey = analyticaPanelConf.getMetricKeys();
		final List<DataPoint> points = new ArrayList<DataPoint>();
		for (final String metrick : metricKey) {
			//			final String[] subcategories = analyticaPanelConf.getSubcategories().toArray(new String[analyticaPanelConf.getSubcategories().size()]);
			//			final HCategory category = new HCategory(analyticaPanelConf.getCategory(), subcategories);//Revoir ça!
			//			System.out.println("Category for the request in the panel:" + category.toString() + "---------------------------------------------------------------------------------------------");
			for (final HCategory category : query.getAllCategories()) {
				for (final HCube cube : result.getSerie(category).getCubes()) {
					final HMetric metric = cube.getMetric(new HMetricKey(metrick, true));
					points.add(new DataPoint(cube.getKey().getTime().getValue(), metric != null ? metric.getCount() : Double.NaN));//Double.NaN
				}
			}
		}
		return points;
	}

	// Pr l'instant retourne la liste des labels
	public final String getRickShawOPtions(final AnalyticaPanelConf analyticaPanelConf) {
		final List<String> labels = analyticaPanelConf.getLabels();
		return new Gson().toJson(labels);
	}

	private List<DataPoint> convertToJsonPoint(final HResult result, final HCategory category, final HMetricKey metricKey) {
		final List<DataPoint> jsonPoints = new ArrayList<DataPoint>();
		for (final HCube cube : result.getSerie(category).getCubes()) {
			final HMetric metric = cube.getMetric(metricKey);
			jsonPoints.add(new DataPoint(cube.getKey().getTime().getValue(), metric != null ? metric.getMean() : Double.NaN));//Double.NaN
		}
		return Collections.unmodifiableList(jsonPoints);
	}

	public final String loadDataAsJson(final AnalyticaPanelConf analyticaPanelConf) {
		final List<HSerie> series = loadData(analyticaPanelConf);
		final Gson gson = new Gson();
		return gson.toJson(series);
	}

	public List<HSerie> loadData(final AnalyticaPanelConf analyticaPanelConf) {
		//	new BuildDatas(serverManager).load();
		final List<HSerie> series;
		final HResult result = serverManager.execute(analyticaPanelConf.getQuery());
		series = new ArrayList<HSerie>();
		for (final HCategory category : result.getQuery().getAllCategories()) {
			series.add(result.getSerie(category));
		}
		return series;
	}

	// Pour Tests : retourne les listes de points par métriques:
	public Map<String, List<DataPoint>> getDataPoints(final AnalyticaPanelConf analyticaPanelConf) {
		//	new BuildDatas(serverManager).load();
		final Map<String, List<DataPoint>> mapPoints = new HashMap<String, List<DataPoint>>();
		List<DataPoint> list;
		final List<HSerie> series = loadData(analyticaPanelConf);
		for (final String metricKey : analyticaPanelConf.getMetricKeys()) {
			list = new ArrayList<DataPoint>();
			for (final HSerie hSerie : series) {
				for (final HCube hCube : hSerie.getCubes()) {
					final HMetric hMetric = hCube.getMetric(new HMetricKey("duration", true));//Trouver comment introduire la métrickey ici
					double val = 0;
					switch (metricKey) {
						case "duration":
							val = hMetric != null ? hMetric.getMean() : Double.NaN;
							break;
						case "count":
							val = hMetric != null ? hMetric.getCount() : Double.NaN;
						default:
							break;
					}
					list.add(new DataPoint(hCube.getKey().getTime().getValue(), val));
				}
			}
			mapPoints.put(metricKey, list);
		}
		return mapPoints;
	}

	// <Metric,Map<Category,DataPoint>>
	public Map<String, Map<String, String>> getAllDataPoints(final AnalyticaPanelConf analyticaPanelConf) {
		final Gson gson = new Gson();
		final Map<String, Map<String, String>> allDataPoints = new HashMap<String, Map<String, String>>();
		Map<String, String> dataPoints;// = new HashMap<String,
		// List<DataPoint>>();
		List<DataPoint> pointsList;
		final HResult hResult = serverManager.execute(analyticaPanelConf.getQuery());
		for (final String metricKey : analyticaPanelConf.getMetricKeys()) {
			dataPoints = new HashMap<String, String>();
			for (final HCategory hCategory : hResult.getQuery().getAllCategories()) {
				pointsList = new ArrayList<DataPoint>();
				for (final HCube cube : hResult.getSerie(hCategory).getCubes()) {
					final HMetric metric = cube.getMetric(new HMetricKey(metricKey, true));
					pointsList.add(new DataPoint(cube.getKey().getTime().getValue(), metric != null ? metric.getMean() : Double.NaN));
				}
				dataPoints.put(hCategory.id(), gson.toJson(pointsList));
			}
			allDataPoints.put(metricKey, dataPoints);
		}
		return allDataPoints;
	}

	//=========================================================================
	//=================Getters et setters pour JSF=============================
	//=========================================================================

	// A rectifier, utilisé pour parcourir la map dans la jsf
	public List<String> getKeyAsList2(final AnalyticaPanelConf analyticaPanelConf) {
		final Map<String, Map<String, String>> map = getAllDataPoints(analyticaPanelConf);
		return new ArrayList<String>(map.keySet());
	}

	public final ServerManager getServerManager() {
		return serverManager;
	}

	public final void setServerManager(final ServerManager serverManager) {
		this.serverManager = serverManager;
	}

}
