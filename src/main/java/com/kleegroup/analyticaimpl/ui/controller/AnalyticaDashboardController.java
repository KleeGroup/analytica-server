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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import kasper.config.ConfigManager;
import kasper.jsf.util.JSFUtil;

import org.primefaces.model.chart.ChartModel;

import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.result.HSerie;
import com.kleegroup.analytica.server.ServerManager;



/**
 * @author npiedeloup
 * @version $Id: AnalyticaDashboardController.java,v 1.9 2013/01/25 10:53:37 npiedeloup Exp $
 */
@RequestScoped
@ManagedBean(name = "analyticaDashboardController")
public final class AnalyticaDashboardController {

	@Inject
	private ConfigManager configManager;

	@Inject
	private ServerManager ServerManager;


	@ManagedProperty(value = "#{analyticaDashboardContext}")
	private AnalyticaDashboardContext analyticaDashboardContext;
	@ManagedProperty(value = "#{analyticaDashboardService}")
	private AnalyticaDashboardService analyticaDashboardService;

	private Function<AnalyticaPanelConf, String> loadJsonFunction;
	private Map<AnalyticaPanelConf, String> jsonDataEvalMap;

	private Function<AnalyticaPanelConf, List<HSerie>> loadDataFunction;
	private Map<AnalyticaPanelConf,List<HSerie>> dataEvalMap;

	private Function<AnalyticaPanelConf, List<Map<String, HSerie>>> loadDataWrappedFunction;
	private Map<AnalyticaPanelConf, List<Map<String, HSerie>>> dataWrappedEvalMap;

	private Function<AnalyticaPanelConf, ChartModel> loadChartModelFunction;
	private Map<AnalyticaPanelConf, ChartModel> primefaceChartEvalMap;

	private Function<AnalyticaPanelConf, List<ColumnModel>> loadColumnModelFunction;
	private Map<AnalyticaPanelConf, List<ColumnModel>> columnsEvalMap;


	@PostConstruct
	public void init() {
		if (!analyticaDashboardContext.isInitialize()) {
			// ---------------------------------------------------------------------
			final String dashboards = configManager.getStringValue("analytica.dashboards", "dashboards");
			for (final String dashboard : dashboards.split(";")) {
				final AnalyticaDashboardConfBuilder dashboardConfBuilder = new AnalyticaDashboardConfBuilder("analytica", dashboard, configManager);
				final AnalyticaDashboardConf dashboardConf = dashboardConfBuilder.build();
				//final AnalyticaDashboardConf dashboardConf = configManager.resolve("analytica." + dashboard, AnalyticaDashboardConf.class);
				analyticaDashboardContext.registerDashboard(dashboard, dashboardConf);

				for (final String panelName : dashboardConf.getPanels()) {
					final AnalyticaPanelConfBuilder panelConfBuilder = new AnalyticaPanelConfBuilder("analytica." + dashboard, panelName, configManager,ServerManager);
					//final AnalyticaPanelConf panelConf = configManager.resolve(dashboardContext + "." + panelName, AnalyticaPanelConf.class);
					analyticaDashboardContext.registerPanel(panelName, panelConfBuilder.build());
				}
			}
			analyticaDashboardContext.initialized();
		}

		final String dashboardName = JSFUtil.getRequestParameter("dashboardName");
		if (dashboardName != null) {
			analyticaDashboardContext.setCurrentDashboard(dashboardName);
		}

		loadJsonFunction = new Function<AnalyticaPanelConf, String>() {
			/** {@inheritDoc} */
			public String apply(final AnalyticaPanelConf analyticaPanelConf) {
				return getAnalyticaDashboardService().loadDataAsJson(analyticaPanelConf);
			}
		};
		jsonDataEvalMap = new EvalMap<AnalyticaPanelConf, String>(loadJsonFunction, AnalyticaPanelConf.class);

		loadDataFunction = new Function<AnalyticaPanelConf, List<HSerie>>() {
			/** {@inheritDoc} */
			public List<HSerie> apply(final AnalyticaPanelConf analyticaPanelConf) {
				return getAnalyticaDashboardService().loadData(analyticaPanelConf);
			}
		};
		dataEvalMap = new EvalMap<AnalyticaPanelConf, List<HSerie>>(loadDataFunction, AnalyticaPanelConf.class);

		//		loadDataWrappedFunction = new Function<AnalyticaPanelConf, List<Map<String, ?>>>() {
		//			/** {@inheritDoc} */
		//			public List<Map<String, ?>> apply(final AnalyticaPanelConf analyticaPanelConf) {
		//				final List<DataSet> list = (List<DataSet>) getAnalyticaDashboardService().loadData(analyticaPanelConf);
		//				final List<Map<String, ?>> result = new ArrayList<Map<String, ?>>();
		//				final Map<String, Map<String, Object>> resultIndex = new HashMap<String, Map<String, Object>>();
		//				for (final DataSet<String, ?> dataSet : list) {
		//					for (int i = 0; i < dataSet.getLabels().size(); i++) {
		//						final String label = dataSet.getLabels().get(i);
		//						Map<String, Object> map = resultIndex.get(label);
		//						if (map == null) {
		//							map = new HashMap<String, Object>();
		//							map.put("LABEL", dataSet.getLabels().get(i));
		//							resultIndex.put(label, map);
		//							result.add(map);
		//						}
		//						final Double value = (Double) dataSet.getValues().get(i);
		//						if (value != null) {
		//							map.put(dataSet.getKey().toString(), String.valueOf(Math.round(value)));
		//						}
		//					}
		//				}
		//				return result;
		//			}
		//		};
		//		dataWrappedEvalMap = new EvalMap<AnalyticaPanelConf, List<Map<String, ?>>>(loadDataWrappedFunction, AnalyticaPanelConf.class);

		//		loadChartModelFunction = new Function<AnalyticaPanelConf, ChartModel>() {
		//			/** {@inheritDoc} */
		//			public ChartModel apply(final AnalyticaPanelConf analyticaPanelConf) {
		//				return getAnalyticaDashboardService().loadDataAsChartModel(analyticaPanelConf);
		//			}
		//		};
		//		primefaceChartEvalMap = new EvalMap<AnalyticaPanelConf, ChartModel>(loadChartModelFunction, AnalyticaPanelConf.class);

		loadColumnModelFunction = new Function<AnalyticaPanelConf, List<ColumnModel>>() {
			/** {@inheritDoc} */
			public List<ColumnModel> apply(final AnalyticaPanelConf analyticaPanelConf) {
				final List<ColumnModel> result = new ArrayList<ColumnModel>();
				ColumnModel columnModel = new ColumnModel("Label", "LABEL");
				result.add(columnModel);
				final List<HCategory> categories = new ArrayList<HCategory>(analyticaPanelConf.getQuery().getAllCategories());
				for (int i = 0; i < categories.size(); i++) {
					final HCategory category = categories.get(i);
					columnModel = new ColumnModel(analyticaPanelConf.getLabels().get(i), category.toString());
					result.add(columnModel);
				}
				return result;
			}
		};
		columnsEvalMap = new EvalMap<AnalyticaPanelConf, List<ColumnModel>>(loadColumnModelFunction, AnalyticaPanelConf.class);

	}

	public Map<AnalyticaPanelConf, String> getJsonData() {
		return jsonDataEvalMap;
	}

	public Map<AnalyticaPanelConf, List<HSerie>> getData() {
		return dataEvalMap;
	}

	//	public List<Map<String, ?>> getDataMap(final AnalyticaPanelConf analyticaPanelConf) {
	//		final List<?> list = getAnalyticaDashboardService().loadData(analyticaPanelConf);
	//		final List<Map<String, ?>> result = new ArrayList<Map<String, ?>>();
	//		for (int i = 0; i < list.size(); i++) {
	//			final Object data = list.get(i);
	//			if (data instanceof Data) {
	//				final Map<String, Object> dataMap = new HashMap<String, Object>();
	//				putDataMap(dataMap, (Data) data, analyticaPanelConf.getLabels().get(i));
	//				result.add(dataMap);
	//			} else if (data instanceof DataSet) {
	//				final DataSet dataSet = (DataSet) data;
	//				for (int j = 0; j < dataSet.getValues().size(); j++) {
	//					final Map<String, Object> dataMap = new HashMap<String, Object>();
	//					dataMap.put("key", dataSet.getKey());
	//					final Object value = dataSet.getValues().get(j);
	//					if (value instanceof Double) {
	//						dataMap.put("value", round((Double) value, 2));
	//					} else {
	//						dataMap.put("value", value);
	//					}
	//					dataMap.put("stringValues", String.valueOf(value));
	//					dataMap.put("label", analyticaPanelConf.getLabels().get(i) + " " + dataSet.getLabels().get(j));
	//					result.add(dataMap);
	//				}
	//			}
	//
	//		}
	//		return result;
	//	}

	//	private void putDataMap(final Map<String, Object> dataMap, final Data data, final String label) {
	//		dataMap.put("key", data.getKey());
	//		dataMap.put("value", round(data.getValue(), 2));
	//		dataMap.put("stringValues", data.getStringValues());
	//		dataMap.put("label", label);
	//	}

	public Map<AnalyticaPanelConf, List<Map<String, ?>>> getDataWrapped() {
		return dataWrappedEvalMap;
	}

	public Map<AnalyticaPanelConf, ChartModel> getPrimefaceChart() {
		return primefaceChartEvalMap;
	}

	public Map<AnalyticaPanelConf, List<ColumnModel>> getColumns() {
		return columnsEvalMap;
	}

	static public class ColumnModel implements Serializable {

		private final String header;
		private final String property;

		public ColumnModel(final String header, final String property) {
			this.header = header;
			this.property = property;
		}

		public String getHeader() {
			return header;
		}

		public String getProperty() {
			return property;
		}
	}

	public final AnalyticaDashboardContext getCtx() {
		return analyticaDashboardContext;
	}

	private double round(final double value, final int nbDecimal) {
		final double mult = Math.pow(10, nbDecimal);
		return Math.round(value * mult) / mult;
	}

	//=========================================================================
	//=================Getters et setters pour JSF=============================
	//=========================================================================

	public final AnalyticaDashboardContext getAnalyticaDashboardContext() {
		return analyticaDashboardContext;
	}

	public final void setAnalyticaDashboardContext(final AnalyticaDashboardContext analyticaDashboardContext) {
		this.analyticaDashboardContext = analyticaDashboardContext;
	}

	public final AnalyticaDashboardService getAnalyticaDashboardService() {
		return analyticaDashboardService;
	}

	public final void setAnalyticaDashboardService(final AnalyticaDashboardService analyticaDashboardService) {
		this.analyticaDashboardService = analyticaDashboardService;
	}

	public final ConfigManager getConfigManager() {
		return configManager;
	}

	public final void setConfigManager(final ConfigManager configManager) {
		this.configManager = configManager;
	}

}
