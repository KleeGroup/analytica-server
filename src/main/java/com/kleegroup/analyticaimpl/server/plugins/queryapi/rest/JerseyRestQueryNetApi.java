package com.kleegroup.analyticaimpl.server.plugins.queryapi.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import vertigo.kernel.Home;
import vertigo.kernel.di.injector.Injector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kleegroup.analytica.hcube.HCubeManager;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.result.HResult;
import com.kleegroup.analytica.server.ServerManager;

/**
 * Api REST avec jersey de requetage des cubes.
 * @author npiedeloup
 * @version $Id: $
 */
@Path("/query")
public class JerseyRestQueryNetApi {
	private final String dTimeTo = "NOW+1h";
	private final String dTimeFrom = "NOW-8h";
	private final String dTimeDim = "Minute";
	private final String dDatas = "duration:mean";

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Inject
	private ServerManager serverManager;
	@Inject
	private HCubeManager cubeManager;

	/**
	 * Constructeur simple, pour instanciation par Jersey.
	 */
	public JerseyRestQueryNetApi() {
		new Injector().injectMembers(this, Home.getComponentSpace());
	}

	@GET
	@Path("/timeLine/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTimeLine(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue(dDatas) @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, false);
		final HResult result = serverManager.execute(query);
		final List<String> dataKeys = Arrays.asList(datas.split(";"));
		final List<TimedDataSerie> dataSeries = Utils.loadDataSeriesByTime(result, dataKeys);
		return gson.toJson(dataSeries);
	}

	@GET
	@Path("/categoryLine/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAggregatedDataByCategory(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue(dDatas) @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, true);
		final HResult result = serverManager.execute(query);
		final List<String> dataKeys = Arrays.asList(datas.split(";"));
		final List<DataSerie> dataSeries = Utils.loadDataSeriesByCategory(result, dataKeys);
		return gson.toJson(dataSeries);
	}

	@GET
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCategories() {
		return gson.toJson(cubeManager.getCategoryDictionary().getAllRootCategories());
	}

	@GET
	@Path("/categories/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCategories(@PathParam("category") final String category) {
		final HCategory hCategory = new HCategory(category);
		return gson.toJson(cubeManager.getCategoryDictionary().getAllSubCategories(hCategory));
	}

	@GET
	@Path("/metrics/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMetrics(@PathParam("category") final String category) {
		final HCategory hCategory = new HCategory(category);
		final HQuery query = Utils.createQuery("NOW-1m", "NOW", "Month", category, false);
		final HResult result = serverManager.execute(query);
		final Collection<HMetric> metrics = result.getSerie(hCategory).getMetrics();
		final List<Map<String, Object>> metricsName = new ArrayList<Map<String, Object>>();
		for (final HMetric metric : metrics) {
			final Map<String, Object> metricName = new HashMap<String, Object>();
			metricsName.add(metricName);
			metricName.put("name", metric.getKey().id());
			final List<String> type = new ArrayList<String>();
			metricName.put("type", type);
			type.add("count");
			type.add("mean");
			type.add("min");
			type.add("max");
			type.add("sum");
			type.add("sqrSum");
			type.add("stdDev");
			if (metric.getKey().isClustered()) {
				type.add("clustered");
			}
		}
		return gson.toJson(metricsName);
	}

	//	@GET
	//	@Path("/bollinger/{category}")
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public String getBollingerBands(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue(dDatasMult) @QueryParam("datas") final String datas) {
	//		final HResult result = Utils.resolveQuery(timeFrom, timeTo, timeDim, category, false);
	//		final Map<String, List<DataPoint>> pointsMap = Utils.loadBollingerBands(result, datas);
	//
	//		return gson.toJson(pointsMap);
	//	}

	@GET
	@Path("/stackedDatas/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCategoriesToStack(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, true);
		final HResult result = serverManager.execute(query);

		return gson.toJson(Utils.loadDataPointsStackedByCategory(result, datas));
	}

	@GET
	@Path("/tableSparkline/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTableSparklineDatas(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, true);
		final HResult result = serverManager.execute(query);

		return gson.toJson(Utils.getSparklinesTableDatas(result, datas));
	}

	@GET
	@Path("/tablePunchcard/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPunchCardDatas(@QueryParam("timeFrom") @DefaultValue("NOW-240h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, false);
		final HResult result = serverManager.execute(query);
		return gson.toJson(Utils.getPunchCardDatas(result, datas));
	}

	@GET
	@Path("/faketablePunchcard/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPunchCardFakeDatas(@QueryParam("timeFrom") @DefaultValue("NOW-240h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, false);
		final HResult result = serverManager.execute(query);
		return gson.toJson(Utils.getPunchCardFakeDatas(result, datas));
	}

	/**
	 * @return Version de l'api
	 */
	@GET
	@Path("/version")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVersion() {
		return "1.0.0";
	}

}
