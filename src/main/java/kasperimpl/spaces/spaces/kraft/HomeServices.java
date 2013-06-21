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

	final String dTimeTo = "NOW+10h";
	final String dTimeFrom = "NOW-2h";
	final String dTimeDim = "Hour";
	final String dDatas = "duration:mean";
	final String dDatasMult = "duration:count;duration:mean";

	private static boolean loaded;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Inject
	private ServerManager serverManager;
	@Inject
	private HCubeManager cubeManager;
	private final Utils utils;

	// instance of utility class

	public HomeServices() {
		final Injector injector = new Injector();
		injector.injectMembers(this, Home.getContainer().getRootContainer());
		if (!loaded) {
			// load();
			new VirtualDatas(serverManager).load();
			loaded = true;
		}
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
		// Ajouter les valeurs par défaut sauf pour la catégorie
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, false);
		final List<DataPoint> points = utils.loadDataPointsMonoSerie(result, datas);
		//final Map<String, List<DataPoint>> pointsMap = loadDataPoints(result, datas);
		//return gson.toJson(pointsMap);
		return gson.toJson(points);
	}

	@GET
	@Path("/multitimeLine/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMultiSerieTimeLine(@QueryParam("timeFrom") @DefaultValue(dTimeFrom) final String timeFrom, @QueryParam("timeTo") @DefaultValue(dTimeTo) final String timeTo, @DefaultValue(dTimeDim) @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue(dDatasMult) @QueryParam("datas") final String datas) {
		// Ajouter les valeurs par défaut sauf pour la catégorie
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
	@Path("/testAggregatedDatasService/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String testAggregatedDatasService(@QueryParam("timeFrom") final String timeFrom, @QueryParam("timeTo") final String timeTo, @QueryParam("timeDim") final String timeDim, @QueryParam("category") final String category, @QueryParam("datas") final String datas) {
		return gson.toJson(utils.testgetAggregatedValuesByCategory(timeFrom, timeTo, timeDim, category, datas));
	}

	@GET
	@Path("/dataTables/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTablesDatas(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		// Ajouter les valeurs par défaut sauf pour la catégorie
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, true);
		//final Map<String, Collection<HMetric>> pointsMap = utils.getDataTable(result, datas);
		//return gson.toJson(pointsMap);
		return gson.toJson(utils.getDataTable(result, datas));
	}

	@GET
	@Path("/stackedDatas/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCategoriesToStack(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		// Ajouter les valeurs par défaut sauf pour la catégorie
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, true);
		//final Map<String, Collection<HMetric>> pointsMap = utils.getDataTable(result, datas);
		//return gson.toJson(pointsMap);
		return gson.toJson(utils.loadDataPointsStackedByCategory(result, datas));
	}

	@GET
	@Path("/complexTable/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getcomplexTableDatas(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		// Ajouter les valeurs par défaut sauf pour la catégorie
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, true);
		//final Map<String, Collection<HMetric>> pointsMap = utils.getDataTable(result, datas);
		//return gson.toJson(pointsMap);
		return gson.toJson(utils.getComplexTableDatas(result, datas));
	}

	@GET
	@Path("/tableSparkline/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTableSparklineDatas(@QueryParam("timeFrom") @DefaultValue("NOW-12h") final String timeFrom, @QueryParam("timeTo") @DefaultValue("NOW+2h") final String timeTo, @DefaultValue("Hour") @QueryParam("timeDim") final String timeDim, @PathParam("category") final String category, @DefaultValue("duration:count") @QueryParam("datas") final String datas) {
		// Ajouter les valeurs par défaut sauf pour la catégorie
		final HResult result = utils.resolveQuery(timeFrom, timeTo, timeDim, category, true);
		//final Map<String, Collection<HMetric>> pointsMap = utils.getDataTable(result, datas);
		//return gson.toJson(pointsMap);
		return gson.toJson(utils.getSparklinesTableDatas(result, datas));
	}
}

//	private List<String> readKeys(final String datas) {
//		final List<String> cles = new ArrayList<String>();
//		final List<String> dataKeys = Arrays.asList(datas.split(";"));
//		for (final String s : dataKeys) {
//			final String[] list = s.split(":");
//			if (list.length > 1) {
//			} else if (list.length == 1) {
//			}
//			cles.add(list[0]);
//		}
//		return cles;
//	}

// @Path("/bootstrap")
// @GET
// @Produces(MediaType.TEXT_HTML)
// public String getBootStrapPage() {
// final List<DataPoint> points = loadDataPoints(getResult());
// final Map<String, Object> context = new HashMap<String, Object>();
// context.put("jsonPoints", gson.toJson(firstConvertTojson(points)));
// context.put("points", points);
// context.put("jsonPoints3", firstConvertTojson(points));
// return process("analyticaBootstrap", context);
// }

// @Path("/analytica")
// @GET
// @Produces(MediaType.TEXT_HTML)
// public String getHtmlPage() {
// load();
//
// final List<DataPoint> points = convertToJsonPoint(getResult(), new HCategory("SQL"), new HMetricKey("duration",
// true));
// final List<DataPoint> points2 = convertToJsonPoint(getResult(), new HCategory("SQL"), new HMetricKey("MONTANT",
// true));
// final Map<String, Object> context = new HashMap<String, Object>();
// context.put("jsonPoints", gson.toJson(points));
// context.put("jsonPoints1", gson.toJson(points2));
// context.put("points", points);
// return process("analytica", context);
// }
//
// @Path("/analytica1")
// @GET
// @Produces(MediaType.TEXT_HTML)
// public String getHtmlChartsPage() {
// load();
// final List<DataPoint> points = convertToJsonPoint(getResult(), new HCategory("SQL"), new HMetricKey("duration",
// true));
// final List<DataPoint> points2 = convertToJsonPoint(getResult(), new HCategory("SQL"), new HMetricKey("MONTANT",
// true));
// final Map<String, Object> context = new HashMap<String, Object>();
// context.put("jsonPoints", gson.toJson(points));
// context.put("jsonPoints1", gson.toJson(points2));
// context.put("points", points);
// return process("analyticacharts", context);
// }
//
// private List<DataPoint> convertToJsonPoint(final HResult result, final HCategory category, final HMetricKey
// metricKey) {
// final List<DataPoint> jsonPoints = new ArrayList<DataPoint>();
// for (final HCube cube : result.getSerie(category).getCubes()) {
// final HMetric metric = cube.getMetric(metricKey);
// jsonPoints.add(new DataPoint(cube.getKey().getTime().getValue(), metric != null ? metric.getMean() :
// Double.NaN));//Double.NaN
// }
// return Collections.unmodifiableList(jsonPoints);
// }

// private String firstConvertTojson(final List<DataPoint> datas) {
// final StringBuilder result = new StringBuilder();
// result.append("[{ \'values\' : [");
// int i = 0;
// for (final DataPoint point : datas) {
// i++;
// result.append("[");
// final String str = point.getDate().getTime() + " , " + point.getValue();
// result.append(str);
// result.append("]");
// if (i < datas.size()) {
// result.append(",");
// }
// }
// result.append("]}]");
// System.out.println(result.toString());
// return result.toString();
// }
//
// private final String process(final String name, final Map<String, ?> context) {
// try {
// final StringWriter writer = new StringWriter();
// final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
// final Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(name + ".mustache"));
// try {
// final Mustache mustache = mustacheFactory.compile(reader, name);
// mustache.execute(writer, context);
// } finally {
// reader.close();
// }
// return writer.toString();
// } catch (final Exception e) {
// throw new KRuntimeException(e);
// }
// }
//
// private final String processHtml(final String name) {
// try {
// final StringWriter writer = new StringWriter();
// final Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(name + ".html"));
// reader.close();
// return writer.toString();
// } catch (final Exception e) {
// throw new KRuntimeException(e);
// }
// }
//
// private static final HMetricKey MONTANT = new HMetricKey("MONTANT", false);
//
// private void load() {
// //jeu de données
// //final Date startDate = new date();
// for (int i = 0; i < 120; i++) {
// addProcess(i, Double.valueOf(Math.ceil(Math.random() * 100)).intValue(), 15);
//
// }
// addProcess(1, 70, 15);
// addProcess(2, 130, 15);
// addProcess(3, 200, 15);
// addProcess(4, 150, 15);
// addProcess(5, 100, 15);
// addProcess(6, 130, 15);
// addProcess(7, 300, 15);
// addProcess(8, 250, 15);
// addProcess(9, 90, 15);
// System.out.println("datas loaded");
// }
//
// private void addProcess(final int offSetInMinutes, final int processDuration, final double price) {
// final Date startDate = new Date(System.currentTimeMillis() - 60 * offSetInMinutes * 1000);
// final KProcess selectProcess2 = new KProcessBuilder(startDate, processDuration, "SQL", "select * from article")//
// .incMeasure(MONTANT.id(), price)//
// .build();
//
// serverManager.push(selectProcess2);
// }
//
// @GET
// @Path("/html")
// @Produces(MediaType.TEXT_HTML)
// public String getHTML() {
// return processHtml("index");
// }
//
// @GET
// @Path("/datas")
// @Produces(MediaType.APPLICATION_JSON)
// public String getloadTestDataAs2Json() {
// final HResult result = getResult();
// return gson.toJson(result.getSerie(new HCategory("PAGE")));
// }
//
// @GET
// @Path("/datas2")
// @Produces(MediaType.APPLICATION_JSON)
// public String getloadTestDataAsJson(@QueryParam("category") final String category) {
// System.out.println("QueryParam :category>>" + category);
// final HResult result = getResult();
// return gson.toJson(result.getSerie(new HCategory(category)));
// }
//
// @GET
// @Path("/datas3/{category}")
// @Produces(MediaType.APPLICATION_JSON)
// public String getloadTestDataAs3Json(@PathParam("category") final String category) {
// System.out.println("PathParam :category>>" + category);
// final HResult result = getResult();
// return gson.toJson(result.getSerie(new HCategory(category)));
// }
//
// /**
// * @return
// */
// private HResult getResult() {
// final HQuery query = serverManager.createQueryBuilder() //
// .on(HTimeDimension.Hour)//
// .from(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000))//10 min ==> 10 cubes
// .to(new Date(System.currentTimeMillis() + 4 * 60 * 60 * 1000)) //
// .with("PAGE").build();
// final HResult result = serverManager.execute(query);
// return result;
// }
