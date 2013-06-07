package kasperimpl.spaces.spaces.kraft;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import kasper.kernel.Home;
import kasper.kernel.di.injector.Injector;
import kasper.kernel.exception.KRuntimeException;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;
import com.kleegroup.analytica.hcube.HCubeManager;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.result.HResult;
import com.kleegroup.analytica.hcube.result.HSerie;
import com.kleegroup.analytica.server.ServerManager;

@Path("/home")
public class HomeServices {
	private static boolean loaded;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Inject
	private ServerManager serverManager;
	@Inject
	private HCubeManager cubeManager;

	public HomeServices() {
		final Injector injector = new Injector();
		injector.injectMembers(this, Home.getContainer().getRootContainer());
		if (!loaded) {
			//load();
			new VirtualDatas(serverManager).load();
			loaded = true;
		}
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String hello() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("<a href=\"\\/datas\">datas</a>");
		sb.append("<a href=\"\\/categories\">categories</a>");
		sb.append("</body></html>");
		return sb.toString();
	}

	@Path("/bootstrap")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getBootStrapPage() {
		final List<DataPoint> points = loadDataPoints(getResult());
		final Map<String, Object> context = new HashMap<String, Object>();
		context.put("jsonPoints", gson.toJson(firstConvertTojson(points)));
		context.put("points", points);
		context.put("jsonPoints3", firstConvertTojson(points));
		return process("analyticaBootstrap", context);
	}

	@Path("/mydatas")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getMyCorrectDatas() {
		final List<DataPoint> points = loadDataPoints(getResult());
		//final Map<String, Object> context = new HashMap<String, Object>();
		//context.put("jsonPoints", gson.toJson(firstConvertTojson(points)));
		//context.put("points", points);
		//context.put("value", gson.toJson(points));
		return gson.toJson(points);
	}

	@Path("/analytica")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getHtmlPage() {
		load();

		final List<DataPoint> points = convertToJsonPoint(getResult(), new HCategory("SQL"), new HMetricKey("duration", true));
		final List<DataPoint> points2 = convertToJsonPoint(getResult(), new HCategory("SQL"), new HMetricKey("MONTANT", true));
		final Map<String, Object> context = new HashMap<String, Object>();
		context.put("jsonPoints", gson.toJson(points));
		context.put("jsonPoints1", gson.toJson(points2));
		context.put("points", points);
		return process("analytica", context);
	}

	@Path("/analytica1")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getHtmlChartsPage() {
		load();
		final List<DataPoint> points = convertToJsonPoint(getResult(), new HCategory("SQL"), new HMetricKey("duration", true));
		final List<DataPoint> points2 = convertToJsonPoint(getResult(), new HCategory("SQL"), new HMetricKey("MONTANT", true));
		final Map<String, Object> context = new HashMap<String, Object>();
		context.put("jsonPoints", gson.toJson(points));
		context.put("jsonPoints1", gson.toJson(points2));
		context.put("points", points);
		return process("analyticacharts", context);
	}

	private List<DataPoint> convertToJsonPoint(final HResult result, final HCategory category, final HMetricKey metricKey) {
		final List<DataPoint> jsonPoints = new ArrayList<DataPoint>();
		for (final HCube cube : result.getSerie(category).getCubes()) {
			final HMetric metric = cube.getMetric(metricKey);
			jsonPoints.add(new DataPoint(cube.getKey().getTime().getValue(), metric != null ? metric.getMean() : Double.NaN));//Double.NaN
		}
		return Collections.unmodifiableList(jsonPoints);
	}

	private List<DataPoint> loadDataPoints(final HResult result) {
		final HQuery query = result.getQuery();
		final List<HSerie> series = new ArrayList<HSerie>();
		for (final HCategory category : query.getAllCategories()) {
			series.add(result.getSerie(category));
		}
		final List<DataPoint> points = new ArrayList<DataPoint>();
		for (final HCategory category : query.getAllCategories()) {
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final HMetric metric = cube.getMetric(new HMetricKey("duration", true));
				points.add(new DataPoint(cube.getKey().getTime().getValue(), metric != null ? metric.getMean() : Double.NaN));//Double.NaN
			}
		}

		return points;
	}

	private String firstConvertTojson(final List<DataPoint> datas) {
		final StringBuilder result = new StringBuilder();
		result.append("[{ \'values\' : [");
		int i = 0;
		for (final DataPoint point : datas) {
			i++;
			result.append("[");
			final String str = point.getDate().getTime() + " , " + point.getValue();
			result.append(str);
			result.append("]");
			if (i < datas.size()) {
				result.append(",");
			}
		}
		result.append("]}]");
		System.out.println(result.toString());
		return result.toString();
	}

	private final String process(final String name, final Map<String, ?> context) {
		try {
			final StringWriter writer = new StringWriter();
			final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
			final Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(name + ".mustache"));
			try {
				final Mustache mustache = mustacheFactory.compile(reader, name);
				mustache.execute(writer, context);
			} finally {
				reader.close();
			}
			return writer.toString();
		} catch (final Exception e) {
			throw new KRuntimeException(e);
		}
	}

	private final String processHtml(final String name) {
		try {
			final StringWriter writer = new StringWriter();
			final Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(name + ".html"));
			reader.close();
			return writer.toString();
		} catch (final Exception e) {
			throw new KRuntimeException(e);
		}
	}

	private static final HMetricKey MONTANT = new HMetricKey("MONTANT", false);

	private void load() {
		//jeu de données
		//final Date startDate = new date();
		for (int i = 0; i < 120; i++) {
			addProcess(i, Double.valueOf(Math.ceil(Math.random() * 100)).intValue(), 15);

		}
		addProcess(1, 70, 15);
		addProcess(2, 130, 15);
		addProcess(3, 200, 15);
		addProcess(4, 150, 15);
		addProcess(5, 100, 15);
		addProcess(6, 130, 15);
		addProcess(7, 300, 15);
		addProcess(8, 250, 15);
		addProcess(9, 90, 15);
		System.out.println("datas loaded");
	}

	private void addProcess(final int offSetInMinutes, final int processDuration, final double price) {
		final Date startDate = new Date(System.currentTimeMillis() - 60 * offSetInMinutes * 1000);
		final KProcess selectProcess2 = new KProcessBuilder(startDate, processDuration, "SQL", "select * from article")//
				.incMeasure(MONTANT.id(), price)//
				.build();

		serverManager.push(selectProcess2);
	}

	@GET
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCategories() {
		return gson.toJson(cubeManager.getCategoryDictionary().getAllRootCategories());
	}

	@GET
	@Path("/html")
	@Produces(MediaType.TEXT_HTML)
	public String getHTML() {
		return processHtml("index");
	}

	@GET
	@Path("/datas")
	@Produces(MediaType.APPLICATION_JSON)
	public String getloadTestDataAs2Json() {
		final HResult result = getResult();
		return gson.toJson(result.getSerie(new HCategory("PAGE")));
	}

	@GET
	@Path("/datas2")
	@Produces(MediaType.APPLICATION_JSON)
	public String getloadTestDataAsJson(@QueryParam("category") final String category) {
		System.out.println("QueryParam :category>>" + category);
		final HResult result = getResult();
		return gson.toJson(result.getSerie(new HCategory(category)));
	}

	@GET
	@Path("/datas3/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getloadTestDataAs3Json(@PathParam("category") final String category) {
		System.out.println("PathParam :category>>" + category);
		final HResult result = getResult();
		return gson.toJson(result.getSerie(new HCategory(category)));
	}

	/**
	 * @return
	 */
	private HResult getResult() {
		final HQuery query = serverManager.createQueryBuilder() //
				.on(HTimeDimension.Hour)//
				.from(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000))//10 min ==> 10 cubes
				.to(new Date(System.currentTimeMillis() + 4 * 60 * 60 * 1000)) //
				.with("PAGE").build();
		final HResult result = serverManager.execute(query);
		return result;
	}
}
