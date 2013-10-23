package com.kleegroup.analyticaimpl.server.plugins.queryapi.rest;

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
	private final String dDatasMult = "duration:count;duration:mean";

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
	public String getMonoSerieTimeLine(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue(dDatas) @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, false);
		final HResult result = serverManager.execute(query);

		final List<DataPoint> points = Utils.loadDataPointsMonoSerie(result, datas);

		return gson.toJson(points);
	}

	@GET
	@Path("/multitimeLine/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMultiSerieTimeLine(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue(dDatasMult) @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, false);
		final HResult result = serverManager.execute(query);

		final Map<String, List<DataPoint>> pointsMap = Utils.loadDataPointsMuliSerie(result, datas);

		return gson.toJson(pointsMap);
	}

	@GET
	@Path("/agregatedDatasByCategory/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAggregatedDataByCategory(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HQuery query = Utils.createQuery(timeFrom, timeTo, timeDim, category, true);
		final HResult result = serverManager.execute(query);

		return gson.toJson(Utils.getAggregatedValuesByCategory(result, datas));
	}

	@GET
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCategories() {
		return gson.toJson(cubeManager.getCategoryDictionary().getAllRootCategories());
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
