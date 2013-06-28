package kasperimpl.spaces.spaces.kraft;

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

import kasper.kernel.Home;
import kasper.kernel.di.injector.Injector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kleegroup.analytica.hcube.HCubeManager;
import com.kleegroup.analytica.hcube.result.HResult;
import com.kleegroup.analytica.server.ServerManager;

@Path("/home")
public class HomeServices {
	private final String dTimeTo = "NOW+10h";
	private final String dTimeFrom = "NOW-2h";
	private final String dTimeDim = "Hour";
	private final String dDatas = "duration:mean";
	private final String dDatasMult = "duration:count;duration:mean";

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Inject
	private ServerManager serverManager;
	@Inject
	private HCubeManager cubeManager;
	private final Utils utils;

	public HomeServices() {
		final Injector injector = new Injector();
		injector.injectMembers(this, Home.getContainer().getRootContainer());
		utils = new Utils(serverManager);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String hello() {
		// @formatter:off
		return new StringBuilder()
			.append("<html><body>")
			.append("<a href=\"\\/datas\">datas</a>")
			.append("<a href=\"\\/categories\">categories</a>")
			.append("</body></html>")
			.toString();
		// @formatter:on
	}

	@GET
	@Path("/timeLine/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMonoSerieTimeLine(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue(dDatas) @QueryParam("datas") final String datas) {
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, false);
		final List<DataPoint> points = utils.loadDataPointsMonoSerie(result, datas);

		return gson.toJson(points);
	}

	@GET
	@Path("/multitimeLine/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMultiSerieTimeLine(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue(dDatasMult) @QueryParam("datas") final String datas) {
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, false);
		final Map<String, List<DataPoint>> pointsMap = utils.loadDataPointsMuliSerie(result, datas);

		return gson.toJson(pointsMap);
	}

	@GET
	@Path("/agregatedDatasByCategory/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAggregatedDataByCategory(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, true);

		return gson.toJson(utils.getAggregatedValuesByCategory(result, datas));
	}

	@GET
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCategories() {

		return gson.toJson(cubeManager.getCategoryDictionary().getAllRootCategories());
	}

	@GET
	@Path("/stackedDatas/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCategoriesToStack(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, true);

		return gson.toJson(utils.loadDataPointsStackedByCategory(result, datas));
	}

	@GET
	@Path("/tableSparkline/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTableSparklineDatas(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, true);

		return gson.toJson(utils.getSparklinesTableDatas(result, datas));
	}

	@GET
	@Path("/tablePunchcard/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPunchCardDatas(@QueryParam("timeFrom") @DefaultValue("NOW-240h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, false);
		return gson.toJson(utils.getPunchCardDatas(result, datas));
	}
}
