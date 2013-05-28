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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import kasperimpl.spaces.spaces.kraft.DataPoint;

import com.google.gson.Gson;
import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;
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
		//if (analyticaPanelConf.isAggregateTime() && analyticaPanelConf.isAggregateWhat()) {
		load();
		final HQuery query = serverManager.createQueryBuilder() //
				.on(HTimeDimension.Minute)//
				.from(new Date(System.currentTimeMillis()-60*60*1000))//10 min ==> 10 cubes
				.to(new Date()) //
				.with("SQL")
				.build();
		final HResult result = serverManager.execute(query);
		final List<HSerie> series = new ArrayList<HSerie>();
		for(final HCategory category : result.getQuery().getAllCategories()){
			series.add(result.getSerie(category));
		}
		final List<String> metricKey = analyticaPanelConf.getMetricKeys();
		//final List<DataPoint>  points  = convertToJsonPoint(result,new HCategory("SQL"),new HMetricKey("duration", true));

		final List<DataPoint> points = new ArrayList<DataPoint>();
		for (final String metrick : metricKey){
			final HCategory category = new HCategory("SQL");
			for (final HCube cube :result.getSerie(category).getCubes()) {
				final HMetric metric = cube.getMetric(new HMetricKey(metrick, true));
				points.add(new DataPoint(cube.getKey().getTime().getValue(),metric!=null? metric.getMean(): Double.NaN));//Double.NaN
			}
		}

		final Gson gson = new Gson();
		return gson.toJson(points);
	}



	private List<DataPoint> convertToJsonPoint(final HResult result, final HCategory category,final HMetricKey metricKey ) {
		final List<DataPoint> jsonPoints  = new ArrayList<DataPoint>();
		for (final HCube cube :result.getSerie(category).getCubes()) {
			final HMetric metric = cube.getMetric(metricKey);
			jsonPoints.add(new DataPoint(cube.getKey().getTime().getValue(),metric!=null? metric.getMean(): Double.NaN));//Double.NaN
		}
		return Collections.unmodifiableList(jsonPoints);
	}

	private static final HMetricKey MONTANT = new HMetricKey("MONTANT", true);
	private static final HMetricKey POIDS = new HMetricKey("POIDS", true);

	private  void load (){
		//jeu de données
		//final Date startDate = new date();
		for(int i=0;i<120;i++){
			addProcess(i, Double.valueOf(Math.ceil(Math.random()*100)).intValue(), 15);
		}
		addProcess(1,70, 15);
		addProcess(2,130, 15);
		addProcess(3,200, 15);
		addProcess(4,150, 15);
		addProcess(5,100, 15);
		addProcess(6,214, 15);
		addProcess(7,300, 15);
		addProcess(8,250, 15);
		addProcess(9,90, 1);
		addProcess(10,600,55);
		System.out.println("datas loaded");
	}

	private void addProcess(final int offSetInMinutes, final int processDuration, final double price) {
		final Date startDate = new Date(System.currentTimeMillis()-60*offSetInMinutes*1000);
		final KProcess selectProcess2 = new KProcessBuilder(startDate, processDuration, "SQL", "select * from article")//
		.incMeasure(MONTANT.id(), price)//
		.build();
		final KProcess healthProcess = new KProcessBuilder(startDate, 0, "HEALTH",
				"/technical/cache").incMeasure(MONTANT.id(), price).incMeasure(POIDS.id(), price).build();
		final KProcess pageProcess = new KProcessBuilder(startDate, processDuration,
				"PAGE", "/user/searchUser.jsf").incMeasure(MONTANT.id(), price).build();
		final KProcess serviceProcess = new KProcessBuilder(startDate,processDuration,
				"FACADE", "/UserService/loadUserByCriteria()").incMeasure(POIDS.id(), price).build();
		serverManager.push(selectProcess2);
		serverManager.push(healthProcess);
		serverManager.push(pageProcess);
		serverManager.push(serviceProcess);
	}


	public final String loadDataAsJson(final AnalyticaPanelConf analyticaPanelConf) {
		final List<HSerie> series = loadData(analyticaPanelConf);
		final Gson gson = new Gson();
		return gson.toJson(series);
	}

	//	public final ChartModel loadDataAsChartModel(final AnalyticaPanelConf analyticaPanelConf) {
	//		final CartesianChartModel result = new CartesianChartModel();
	//		if (analyticaPanelConf.isAggregateTime() && analyticaPanelConf.isAggregateWhat()) {
	//
	//			final List<HSerie> datas = serverManager.getData(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
	//
	//			final ChartSeries serie = new ChartSeries();
	//			serie.setLabel(analyticaPanelConf.getPanelTitle());
	//			for (final HCube data : datas) {
	//				serie.set(data.);
	//				//serie.set(data.getKey().getName() + "(" + data.getKey().getType().name() + ")", data.getValue());
	//			}
	//			result.addSeries(serie);
	//		} else if (analyticaPanelConf.isAggregateTime()) {
	//			final List<DataSet<String, ?>> datas = serverManager.getDataWhatLine(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
	//			for (final DataSet<String, ?> dataSet : datas) {
	//				final LineChartSeries serie = new LineChartSeries();
	//				serie.setLabel(dataSet.getKey().getName() + "(" + dataSet.getKey().getType().name() + ")");
	//				final List<String> labels = dataSet.getLabels();
	//				final List<?> values = dataSet.getValues();
	//				for (int i = 0; i < labels.size(); i++) {
	//					serie.set(labels.get(i).substring(1), (Double) values.get(i));
	//				}
	//				result.addSeries(serie);
	//			}
	//
	//		} else {
	//			final List<DataSet<Date, ?>> datas = serverManager.getDataTimeLine(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
	//			for (final DataSet<Date, ?> dataSet : datas) {
	//				final LineChartSeries serie = new LineChartSeries();
	//				serie.setLabel(dataSet.getKey().getName() + "(" + dataSet.getKey().getType().name() + ")");
	//				final List<Date> labels = dataSet.getLabels();
	//				final List<?> values = dataSet.getValues();
	//				for (int i = 0; i < labels.size(); i++) {
	//					serie.set(labels.get(i).getTime(), (Double) values.get(i));
	//				}
	//				result.addSeries(serie);
	//			}
	//		}
	//		return result;
	//	}

	public List<HSerie> loadData(final AnalyticaPanelConf analyticaPanelConf) {
		final HQuery query = serverManager.createQueryBuilder() //
				.on(HTimeDimension.Minute)//
				.from(new Date(System.currentTimeMillis()-60*60*1000))//10 min ==> 10 cubes
				.to(new Date()) //
				.with("SQL")
				.build();
		load();
		final List<HSerie> series;
		//if (analyticaPanelConf.isAggregateTime() && analyticaPanelConf.isAggregateWhat()) {
		final HResult result = serverManager.execute(analyticaPanelConf.getQuery());
		series = new ArrayList<HSerie>();
		for(final HCategory category : result.getQuery().getAllCategories()){
			series.add(result.getSerie(category));
		}
		return series;
	}

	// NOT SURE ABOUT THIS
	public Map<String, String> getDataPoints(final AnalyticaPanelConf analyticaPanelConf) {
		final HQuery query = serverManager.createQueryBuilder() //
				.on(HTimeDimension.Minute)//
				.from(new Date(System.currentTimeMillis()-60*60*1000))//10 min ==> 10 cubes
				.to(new Date()) //
				.with("SQL")
				.build();
		load();
		final Gson gson = new Gson();
		final Map<String, String> mapPoints = new HashMap<String, String>();
		List<DataPoint> list;
		final List<HSerie> series = loadData(analyticaPanelConf);
		for (final String metricKey : analyticaPanelConf.getMetricKeys()) {
			list = new ArrayList<DataPoint>();
			for (final HSerie hSerie : series) {
				for (final HCube hCube : hSerie.getCubes()) {
					final HMetric hMetric = hCube.getMetric(new HMetricKey(metricKey, true));
					list.add(new DataPoint(hCube.getKey().getTime().getValue(),hMetric != null ? hMetric.getMean() : Double.NaN));
				}
			}
			mapPoints.put(metricKey, gson.toJson(list));
		}
		return mapPoints;
	}
	// A rectifier, utilisé pour parcourir la map dans la jsf
	public List<String> getKeyAsList(final AnalyticaPanelConf analyticaPanelConf) {
		final Map<String, String> map = getDataPoints(analyticaPanelConf);
		return new ArrayList<String>(map.keySet());
	}
	// A rectifier, utilisé pour parcourir la map dans la jsf
	public List<String> getKeyAsList2(final AnalyticaPanelConf analyticaPanelConf) {
		final Map<String, Map<String, String>> map = getAllDataPoints(analyticaPanelConf);
		return new ArrayList<String>(map.keySet());
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
					pointsList.add(new DataPoint(cube.getKey().getTime()
							.getValue(), metric != null ? metric.getMean(): Double.NaN));
				}
				dataPoints.put(hCategory.id(), gson.toJson(pointsList));
			}
			allDataPoints.put(metricKey, dataPoints);
		}
		return allDataPoints;
	}


	//	public Map <String,List<DataPoint>> getDataPoints(final AnalyticaPanelConf analyticaPanelConf){
	//		final Map<String,List<DataPoint>> dataMap = new HashMap<String,List<DataPoint>>();
	//		List<DataPoint> dataPoints ;
	//		final HResult result = serverManager.execute(analyticaPanelConf.getQuery());
	//		for(final HCategory category : result.getQuery().getAllCategories()){
	//			for (final String metrickey : analyticaPanelConf.getMetricKeys()){
	//				dataPoints = new ArrayList<DataPoint>();
	//				for (final HCube cube :result.getSerie(category).getCubes()) {
	//					final HMetric metric = cube.getMetric(new HMetricKey(metrickey, true));
	//					dataPoints.add(new DataPoint(cube.getKey().getTime().getValue(), metric!=null? metric.getMean(): Double.NaN));
	//				}
	//				dataMap.put(metrickey, dataPoints);
	//			}
	//		}
	//
	//		return dataMap;
	//	}

	//=========================================================================
	//=================Getters et setters pour JSF=============================
	//=========================================================================

	public final ServerManager getServerManager() {
		return serverManager;
	}

	public final void setServerManager(final ServerManager serverManager) {
		this.serverManager = serverManager;
	}

}
