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
import java.util.Date;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

import com.google.gson.Gson;
import com.kleegroup.analytica.server.ServerManager;
import com.kleegroup.analytica.server.data.Data;
import com.kleegroup.analytica.server.data.DataSet;

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

	public final String loadDataAsJson(final AnalyticaPanelConf analyticaPanelConf) {
		final Object objectResult;
		if (analyticaPanelConf.isAggregateTime() && analyticaPanelConf.isAggregateWhat()) {
			final List<Data> datas = serverManager.getData(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			objectResult = datas;
		} else if (analyticaPanelConf.isAggregateTime()) {
			final List<DataSet<String, ?>> datas = serverManager.getDataWhatLine(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			objectResult = datas;
		} else {
			final List<DataSet<Date, ?>> datas = serverManager.getDataTimeLine(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			objectResult = datas;
		}
		final Gson gson = new Gson();
		return gson.toJson(objectResult);
	}

	public final ChartModel loadDataAsChartModel(final AnalyticaPanelConf analyticaPanelConf) {
		final CartesianChartModel result = new CartesianChartModel();
		if (analyticaPanelConf.isAggregateTime() && analyticaPanelConf.isAggregateWhat()) {
			final List<Data> datas = serverManager.getData(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			final ChartSeries serie = new ChartSeries();
			serie.setLabel(analyticaPanelConf.getPanelTitle());
			for (final Data data : datas) {
				serie.set(data.getKey().getName() + "(" + data.getKey().getType().name() + ")", data.getValue());
			}
			result.addSeries(serie);
		} else if (analyticaPanelConf.isAggregateTime()) {
			final List<DataSet<String, ?>> datas = serverManager.getDataWhatLine(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			for (final DataSet<String, ?> dataSet : datas) {
				final LineChartSeries serie = new LineChartSeries();
				serie.setLabel(dataSet.getKey().getName() + "(" + dataSet.getKey().getType().name() + ")");
				final List<String> labels = dataSet.getLabels();
				final List<?> values = dataSet.getValues();
				for (int i = 0; i < labels.size(); i++) {
					serie.set(labels.get(i).substring(1), (Double) values.get(i));
				}
				result.addSeries(serie);
			}

		} else {
			final List<DataSet<Date, ?>> datas = serverManager.getDataTimeLine(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			for (final DataSet<Date, ?> dataSet : datas) {
				final LineChartSeries serie = new LineChartSeries();
				serie.setLabel(dataSet.getKey().getName() + "(" + dataSet.getKey().getType().name() + ")");
				final List<Date> labels = dataSet.getLabels();
				final List<?> values = dataSet.getValues();
				for (int i = 0; i < labels.size(); i++) {
					serie.set(labels.get(i).getTime(), (Double) values.get(i));
				}
				result.addSeries(serie);
			}
		}
		return result;
	}

	public List<?> loadData(final AnalyticaPanelConf analyticaPanelConf) {
		final List<?> result;
		if (analyticaPanelConf.isAggregateTime() && analyticaPanelConf.isAggregateWhat()) {
			final List<Data> datas = serverManager.getData(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			result = datas;
		} else if (analyticaPanelConf.isAggregateTime()) {
			final List<DataSet<String, ?>> datas = serverManager.getDataWhatLine(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			result = datas;
		} else {
			final List<DataSet<Date, ?>> datas = serverManager.getDataTimeLine(analyticaPanelConf.getTimeSelection(), analyticaPanelConf.getWhatSelection(), analyticaPanelConf.getDataKeys());
			result = datas;
		}
		return result;
	}

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
