/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.server.plugins.queryapi.rest;

import io.analytica.server.ServerManager;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.ProcessAggregatorResult;
import io.vertigo.app.Home;
import io.vertigo.core.component.di.injector.Injector;
import io.vertigo.lang.Assertion;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Api REST avec jersey de requetage des cubes.
 * @author npiedeloup
 * @version $Id: $
 */
@Path("/query")
public class JerseyRestQueryNetApi {
	private static final String SEPARATOR = "/";
	private final String dTimeTo = "NOW+1h";
	private final String dTimeFrom = "NOW-8h";
	private final String dTimeDim = "Minute";
	private final String dDatas = "duration:mean";

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Inject
	private ServerManager serverManager;


	/**
	 * Constructeur simple, pour instanciation par Jersey.
	 */
	public JerseyRestQueryNetApi() {
		Injector.injectMembers(this, Home.getApp().getComponentSpace());
	}

	@GET
	@Path("/timeLine/{type}{subcategories:(/.+?)?}")
	//le type est obligatoire les sous catégories (séparées par /) sont optionnelles
	@Produces(MediaType.APPLICATION_JSON)
	public String getTimeLine(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @DefaultValue(dDatas) @QueryParam("datas") final String datas, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws ProcessAggregatorException {
		return gson.toJson(serverManager.getTimeLine(appName,timeFrom,timeTo,timeDim,type,subCategories.startsWith("/", 0)?subCategories.substring(1):subCategories,location,flatDatas(datas)));
	}

//	@GET
//	@Path("/categoryLine/{type}{subcategories:(/.+?)?}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getAggregatedDataByCategory(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @DefaultValue(dDatas) @QueryParam("datas") final String datas, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws HCubeStoreException {
//		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, type, subCategories, true,location);
//		final HResult result = serverManager.execute(appName, type, query);
//		final List<String> dataKeys = Arrays.asList(datas.split(";"));
//		final List<DataSerie> dataSeries = Utils.loadDataSeriesByCategory(result, dataKeys);
//		return gson.toJson(dataSeries);
//	}
//
//	@GET
//	@Path("/metricLine/{type}{subcategories:(/.+?)?}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getAggregatedDataByTimeAndCategory(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @DefaultValue(dDatas) @QueryParam("datas") final String datas, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws HCubeStoreException {
//		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, type, subCategories, true,location);
//		final HResult result = serverManager.execute(appName, type, query);
//		final List<String> dataKeys = Arrays.asList(datas.split(";"));
//		final List<DataSerie> dataSeries = Utils.loadDataSeriesByCategory(result, dataKeys);
//		return gson.toJson(dataSeries);
//	}
//
	@GET
	@Path("/all_categories")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCategories(@QueryParam("appName") final String appName) throws ProcessAggregatorException {
		return gson.toJson(serverManager.findAllCategories(appName));
	}
	
	@GET
	@Path("/all_types")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTypes(@QueryParam("appName") final String appName) throws ProcessAggregatorException {
		return gson.toJson(serverManager.findAllTypes(appName));
	}
//
	@GET
	@Path("/all_locations")
	@Produces(MediaType.APPLICATION_JSON)
	public String getLocations(@QueryParam("appName") final String appName) throws ProcessAggregatorException {
		return gson.toJson(serverManager.findAllLocations(appName));
	}
	
	@GET
	@Path("/categories/{type}{subcategories:(/.+?)?}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCategories(@PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws ProcessAggregatorException {
		return gson.toJson(serverManager.findCategories(appName,type,subCategories.startsWith("/", 0)?subCategories.substring(1):subCategories,location));
	}
//
//	@GET
//	@Path("/metrics/{type}{subcategories:(/.+?)?}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getMetrics(@PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws HCubeStoreException {
//		final HCategory hCategory = new HCategory(type,subCategories,SEPARATOR);
//		final HQuery query = Utils.createQuery("NOW-1m", "NOW", "Month", type, subCategories, false,location);
//		final HResult result = serverManager.execute(appName, type, query);
//		final Collection<HMetric> metrics = result.getSerie(hCategory).getMetrics();
//		final List<Map<String, Object>> metricsName = new ArrayList<>();
//		for (final HMetric metric : metrics) {
//			final Map<String, Object> metricName = new HashMap<>();
//			metricsName.add(metricName);
//			metricName.put("name", metric.getName());
//			final List<String> values = new ArrayList<>();
//			metricName.put("type", values);
//			values.add("count");
//			values.add("mean");
//			values.add("min");
//			values.add("max");
//			values.add("sum");
//			values.add("sqrSum");
//			values.add("stdDev");
//			if (metric.hasDistribution()) {
//				values.add("clustered");
//			}
//		}
//		return gson.toJson(metricsName);
//	}
//
//	@GET
//	@Path("/stackedDatas/{type}{subcategories:(/.+?)?}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getAllCategoriesToStack(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @DefaultValue("duration:count") @QueryParam("datas") final String datas, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws HCubeStoreException {
//		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, type, subCategories, true,location);
//		final HResult result = serverManager.execute(appName, type, query);
//
//		return gson.toJson(Utils.loadDataPointsStackedByCategory(result, datas));
//	}
//
//	@GET
//	@Path("/tableSparkline/{type}{subcategories:(/.+?)?}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getTableSparklineDatas(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @DefaultValue("duration:count") @QueryParam("datas") final String datas, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws HCubeStoreException {
//		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, type, subCategories, true,location);
//		final HResult result = serverManager.execute(appName, type, query);
//
//		return gson.toJson(Utils.getSparklinesTableDatas(result, datas));
//	}
//
//	@GET
//	@Path("/tablePunchcard/{type}{subcategories:(/.+?)?}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getPunchCardDatas(@QueryParam("timeFrom") @DefaultValue("NOW-240h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @DefaultValue("duration:count") @QueryParam("datas") final String datas, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws HCubeStoreException {
//		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, type, subCategories, false,location);
//		final HResult result = serverManager.execute(appName, type, query);
//		return gson.toJson(Utils.getPunchCardDatas(result, datas));
//	}
//
//	@GET
//	@Path("/faketablePunchcard/{type}{subcategories:(/.+?)?}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getPunchCardFakeDatas(@QueryParam("timeFrom") @DefaultValue("NOW-240h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("type") final String type, @PathParam("subcategories") final String subCategories, @DefaultValue("duration:count") @QueryParam("datas") final String datas, @QueryParam("appName") final String appName, @QueryParam("location") final String location) throws HCubeStoreException {
//		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, type, subCategories, false,location);
//		final HResult result = serverManager.execute(appName, type, query);
//		return gson.toJson(Utils.getPunchCardFakeDatas(result, datas));
//	}

	/**
	 * @return Version de l'api
	 */
	@GET
	@Path("/version")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVersion() {
		return "1.3.2";
	}
	
	private Map<String/*metric*/,String/*aggregationRule*/> flatDatas(final String datas){
		Map<String,String> flatData = new HashMap<String, String>();
		
		String[] metricsAndRules = datas.split(";");
		Assertion.checkArgument(metricsAndRules!=null && metricsAndRules.length>0,"Error parsing the datas. No rule found in "+datas);
		for (String metricAndRule : metricsAndRules) {
			String[] values = metricAndRule.split(":");
			Assertion.checkArgument(values!=null && values.length==2,"Error parsing the datas + "+datas);
			flatData.put(values[0], values[1]);
		}
		return flatData;
	}

}