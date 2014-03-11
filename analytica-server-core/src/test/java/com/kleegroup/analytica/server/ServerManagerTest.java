/**
 * 
 */
package com.kleegroup.analytica.server;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kleegroup.analytica.AbstractTestCaseJU4Rule;
import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;
import com.kleegroup.analytica.hcube.HCubeManager;
import com.kleegroup.analytica.hcube.cube.HCounterType;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.query.HQueryBuilder;
import com.kleegroup.analytica.hcube.result.HResult;
import com.kleegroup.analytica.museum.Museum;
import com.kleegroup.analytica.museum.PageListener;
import com.kleegroup.analytica.museum.StatsUtil;

/**
 * @author Stephane TATCHUM
 *
 */
public class ServerManagerTest extends AbstractTestCaseJU4Rule {
	private static final HMetricKey MONTANT = new HMetricKey("MONTANT", false);
	private static final String PROCESS_SQL = "SQL";

	@Inject
	private ServerManager serverManager;
	@Inject
	private HCubeManager cubeManager;

	private Date date;
	private final int price = 8;

	@Before
	public void init() throws ParseException {
		date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).parse("08/05/2013 10:10");
	}

	private static void assertMetricEquals(final HMetric metric, final double count, final double sum, final double mean, final double min, final double max) {
		Assert.assertEquals(count, metric.get(HCounterType.count), 0);
		Assert.assertEquals(count, metric.getCount(), 0); //test accesseur rapide
		Assert.assertEquals(sum, metric.get(HCounterType.sum), 0);//test accesseur rapide 0);
		Assert.assertEquals(sum, metric.getSum(), 0);
		Assert.assertEquals(mean, metric.get(HCounterType.mean), 0);
		Assert.assertEquals(mean, metric.getMean(), 0);//test accesseur rapide
		Assert.assertEquals(min, metric.get(HCounterType.min), 0);
		Assert.assertEquals(max, metric.get(HCounterType.max), 0);
	}

	@Test
	public void testSimpleProcess() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		serverManager.push(selectProcess1);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL").build();

		final HCategory processSQLCategory = new HCategory(PROCESS_SQL);
		final HResult result = serverManager.execute(daySqlQuery);
		Assert.assertEquals(1, result.getAllCategories().size());

		final List<HCube> cubes = result.getSerie(processSQLCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		final HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
	}

	@Test
	public void testVirtualData() throws InterruptedException {
		final VirtualDatas virtualDatas = new VirtualDatas(serverManager);
		virtualDatas.load();
	}

	@Test
	public void testRandom() throws InterruptedException {
		final SortedMap<Long, Integer> counts = new TreeMap<>();
		for (int i = 0; i < 10000; i++) {
			final long result = StatsUtil.random(5, 1);
			final int count = counts.containsKey(result) ? counts.get(result) : 0;
			counts.put(result, count + 1);
		}
		System.out.println(counts);
	}

	@Test
	public void testRandom2() throws InterruptedException {
		final SortedMap<Long, Integer> counts = new TreeMap<>();
		for (int i = 0; i < 10000; i++) {
			final long result = StatsUtil.randomValue(100, 1, 100, 0);
			final int count = counts.containsKey(result) ? counts.get(result) : 0;
			counts.put(result, count + 1);
		}
		System.out.println(counts);
	}

	@Test
	public void testFormat() throws InterruptedException {
		final DecimalFormat df = new DecimalFormat("#.00");
		System.out.println(format(456654.456123));
		System.out.println(format(456654.45));
		System.out.println(format(456654.456));
		System.out.println(format(456654.455));
		System.out.println(format(456654.454));
		System.out.println(format(456654.4));

		System.out.println(format(456654.5));

		System.out.println(format(456654.6));
	}

	private String format(final double val) {
		final String value = String.valueOf(Math.round(val * 100) / 100d);
		if (value.indexOf('.') > value.length() - 3) {
			return value + "0"; //it miss a 0 
		}
		return value;
	}

	@Test
	//On charge 10 jours à 50 visites par jourDURATION
	public void testMuseum() throws InterruptedException {
		final int days = 15;
		final int visitsByDay = 200;
		new Museum(new PageListener() {
			@Override
			public void onPage(final KProcess process) {
				serverManager.push(process);
			}
		}).load(days, visitsByDay);
		//Thread.sleep(1000000000);
	}
}
