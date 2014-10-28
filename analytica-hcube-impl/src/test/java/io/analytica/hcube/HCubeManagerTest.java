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
package io.analytica.hcube;

import io.analytica.hcube.cube.HCounterType;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.cube.HMetricBuilder;
import io.analytica.hcube.cube.HMetricDefinition;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;
import io.analytica.hcube.impl.HCubeManagerImpl;
import io.analytica.hcube.plugins.store.memory.MemoryHCubeStorePlugin;
import io.analytica.hcube.query.HCategorySelection;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HQueryBuilder;
import io.analytica.hcube.result.HPoint;
import io.analytica.hcube.result.HResult;
import io.analytica.hcube.result.HSerie;
import io.vertigo.core.Home;
import io.vertigo.util.DateBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test.
 *
 *  - a request ==> page, duration, status
 *
 * @author pchretien
 */
public final class HCubeManagerTest {
	private static final String HM_WEIGHT = "HM_WEIGHT";
	private static final String HM_TEST = "HM_TEST";
	private static final String HM_DURATION = "HM_DURATION";
	private static final String APP_NAME = "MY_APP";
	private static final String PAGES = "PAGES";
	private final HCubeManager cubeManager = new HCubeManagerImpl(new MemoryHCubeStorePlugin());

	private final HApp app = cubeManager.getApp(APP_NAME);

	@Before
	public void before() {
		final HMetricDefinition duration = new HMetricDefinition(HM_DURATION, true);
		final HMetricDefinition weight = new HMetricDefinition(HM_WEIGHT, false);
		final HMetricDefinition test = new HMetricDefinition(HM_TEST, true);
		cubeManager.register(duration);
		cubeManager.register(weight);
		cubeManager.register(test);
	}

	@After
	public void after() {
		Home.getDefinitionSpace().stop();
	}

	//private final HCubeManager cubeManager = new HCubeManagerImpl(new LuceneHCubeStorePlugin());
	@Test
	public void testAppNames() throws ParseException {
		Assert.assertEquals(1, cubeManager.getApps().size());
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		final Date start = dateFormat.parse("2012/12/12");
		final int days = 1;
		//----
		populateData(start, days);
		//----
		Assert.assertEquals(1, cubeManager.getApps().size());
	}

	@Test
	public void testQuery() {
		final Date start = new Date();
		final HQuery query1 = new HQueryBuilder()//
				.onType(PAGES)//
				.on(HTimeDimension.Hour)//
				.between(new DateBuilder(start).addHours(-3).toDateTime(), start)//
				//.whereCategoryEquals(PAGES)//
				.build();

		final HQuery query2 = new HQueryBuilder()//
				.onType(PAGES)//
				.on(HTimeDimension.Hour)//
				.between("NOW-3h", "NOW")//
				//				.whereCategoryEquals(PAGES)//
				.build();
		//---
		Assert.assertEquals(3, app.getSelector().findTimes(query1.getTimeSelection()).size()); //3 HOURS
		Assert.assertEquals(3, app.getSelector().findTimes(query2.getTimeSelection()).size()); //3 HOURS
		Assert.assertTrue(app.getSelector().findTimes(query1.getTimeSelection()).containsAll(app.getSelector().findTimes(query2.getTimeSelection())));
		Assert.assertTrue(app.getSelector().findTimes(query2.getTimeSelection()).containsAll(app.getSelector().findTimes(query1.getTimeSelection())));
		Assert.assertEquals(query1.toString(), query2.toString());
	}

	@Test
	public void testQuery2() {
		final HQuery query = new HQueryBuilder()//
				.onType(PAGES)//
				.on(HTimeDimension.Hour)//
				.between("NOW", "NOW+3d")//
				//				.whereCategoryEquals(PAGES)//
				.build();
		//---
		Assert.assertEquals(3 * 24, app.getSelector().findTimes(query.getTimeSelection()).size()); //72 hours
	}

	/*
	 * Query must not accept a 'date from' > 'date to'.
	 */
	@Test(expected = Exception.class)
	public void testQueryFail() {
		new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.between("NOW", "NOW-3m")//
				//	.whereCategoryEquals(PAGES)//
				.build();
	}

	/*
	 * Checking categories before and after populating data.
	 */
	@Test
	public void testCategoriesDictionnary() throws ParseException {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		final Date start = dateFormat.parse("2012/12/12");
		final int days = 1;

		//----
		Assert.assertEquals(0, app.getSelector().findCategories(new HCategorySelection("*")).size());
		//---
		populateData(start, days);
		//----
		Assert.assertEquals(2, app.getSelector().findCategories(new HCategorySelection("*")).size());
		Assert.assertEquals(1, app.getSelector().findCategories(new HCategorySelection("welcome")).size());
	}

	/**
	 * data are collected during 10 days from 2012/12/12.
	 */
	@Test
	public void testLoadAndQuery() throws ParseException {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		final Date start = dateFormat.parse("2012/12/12");
		final int days = 10;
		final Date end = dateFormat.parse("2012/12/13");
		//----
		populateData(start, days);
		//----
		final HQuery query = new HQueryBuilder()//
				.onType(PAGES)//
				.on(HTimeDimension.Hour)//
				.between(start, end)//
				//.whereCategoryEquals(PAGES)//
				.build();

		final HResult result = app.execute(query);

		Assert.assertEquals(query, result.getQuery());

		//Check : 2 categories
		Assert.assertEquals(2, result.getAllCategories().size());

		//Check : 24 cubes(per hour) by day
		Assert.assertEquals(24, result.getSerie("").getCubes().size());

		//Check : serie contains 2 metric (DURATION and WEIGHT)
		Assert.assertEquals(2, result.getSerie("").getMetrics().size());

		//
		final HSerie serie = result.getSerie("");
		Assert.assertEquals(Collections.singletonList(new HCategory("")), serie.getCategories());

		for (final Entry<HTime, HCube> entry : serie.getCubes().entrySet()) {
			final HCube cube = entry.getValue();
			//	HTime time = entry.getKey();
			Assert.assertTrue(cube.toString().startsWith("{"));
			Assert.assertTrue(cube.toString().endsWith("}"));
			Assert.assertEquals(2, cube.getMetrics().size());
			final HMetric metric = cube.getMetric(HM_DURATION);
			Assert.assertEquals(100 * 60 * 2, metric.getCount(), 0);
			Assert.assertEquals(100, metric.getMean(), 0);
			Assert.assertEquals(1, metric.get(HCounterType.min), 0);
		}
		final HMetric metric = serie.getMetric(HM_DURATION);
		Assert.assertTrue(metric.toString().startsWith("{"));
		Assert.assertTrue(metric.toString().endsWith("}"));

		Assert.assertEquals(HM_DURATION, metric.getName());
		Assert.assertEquals(100 * 60 * 2 * 24, metric.getCount(), 0);
		Assert.assertEquals(100, metric.getMean(), 0);
		Assert.assertTrue(metric.get(HCounterType.stdDev) > 0);

		Assert.assertEquals(1, metric.get(HCounterType.min), 0);
		//---

		final List<HPoint> points = serie.getPoints(HM_DURATION);
		Assert.assertEquals(24, points.size());
		int h = 0;
		for (final HPoint point : serie.getPoints(HM_DURATION)) {
			Assert.assertEquals(h, point.getDate().getHours());
			h++;
			Assert.assertEquals(100 * 60 * 2, point.getMetric().getCount(), 0);
			Assert.assertEquals(100, point.getMetric().getMean(), 0);
			Assert.assertEquals(1, point.getMetric().get(HCounterType.min), 0);
		}

	}

	/**
	 * data are collected during 10 days from 2012/12/12.
	 */
	@Test
	public void testLoadAndQuery2() throws ParseException {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		final Date start = dateFormat.parse("2012/12/12");
		final int days = 1;
		final Date end = dateFormat.parse("2012/12/13");
		//----
		populateData(start, days);
		//----
		final HQuery query = new HQueryBuilder()//
				.onType(PAGES)//
				.on(HTimeDimension.SixMinutes)//
				.between(start, end)//
				//.whereCategoryEquals(PAGES)//
				.build();

		final HResult result = app.execute(query);

		Assert.assertEquals(query, result.getQuery());

		//Check : 1 category
		Assert.assertEquals(2, result.getAllCategories().size());

		//Check : 10*24 cubes per minute
		Assert.assertEquals(240, result.getSerie("").getCubes().size());
	}

	@Test
	public void testMetrics() {
		//---
		final HMetricBuilder metricBuilder = new HMetricBuilder(HM_TEST);
		double sqrSum = 0;
		for (int i = 0; i < 100; i++) {
			metricBuilder.withValue(1000 - i);
			metricBuilder.withValue(1000 + i);
			sqrSum += (1000 - i) * (1000 - i) + (1000 + i) * (1000 + i);
		}
		final HMetric metric = metricBuilder.build();
		Assert.assertEquals(200, metric.getCount());
		Assert.assertEquals(1000, metric.getMean(), 0);
		Assert.assertEquals(100 * 2000, metric.get(HCounterType.sum), 0);
		Assert.assertEquals(1000 - 99, metric.get(HCounterType.min), 0);
		Assert.assertEquals(1000 + 99, metric.get(HCounterType.max), 0);
		Assert.assertEquals(sqrSum, metric.get(HCounterType.sqrSum), 0);
	}

	@Test
	public void testHistogram() {
		final HMetric metric = new HMetricBuilder(HM_TEST)//
				.withValue(0)//<0
				.withValue(1)//<1
				.withValue(2)//<2
				.withValue(3)//<5
				.withValue(4)//<5
				.withValue(5)//<5
				.withValue(6)//<10
				.withValue(7)//<10
				.withValue(8)//<10
				.withValue(9)//<10
				.withValue(10)//<10
				.withValue(11)//<20
				.withValue(35)//<50
				.withValue(455)//<500
				.withValue(355)//<500
				.withValue(111222333)//<200000000
				.build();
		final Map<Double, Long> histogram = metric.getDistribution().getData();

		Assert.assertEquals(1, histogram.get(0d), 0);
		Assert.assertEquals(1, histogram.get(1d), 0);
		Assert.assertEquals(1, histogram.get(2d), 0);
		Assert.assertEquals(3, histogram.get(5d), 0);
		Assert.assertEquals(5, histogram.get(10d), 0);
		Assert.assertEquals(1, histogram.get(20d), 0);
		Assert.assertEquals(1, histogram.get(50d), 0);
		Assert.assertEquals(2, histogram.get(500d), 0);
		Assert.assertEquals(1, histogram.get(200000000d), 0);
	}

	@Test
	public void testTimeDimension() {
		final Date start = new Date();
		HTime now;
		//---
		now = new HTime(start, HTimeDimension.Minute);
		Assert.assertEquals((start.getMinutes() + 1) % 60, new Date(now.next().inMillis()).getMinutes());
		//---
		now = new HTime(start, HTimeDimension.Hour);
		Assert.assertEquals((start.getHours() + 1) % 24, new Date(now.next().inMillis()).getHours());
		//---
		now = new HTime(start, HTimeDimension.Day);
		Assert.assertEquals((start.getDay() + 1) % 7, new Date(now.next().inMillis()).getDay()); //getDay = day of the week
		//---
		now = new HTime(start, HTimeDimension.Month);
		Assert.assertEquals((start.getMonth() + 1) % 12, new Date(now.next().inMillis()).getMonth());
		//---
		now = new HTime(start, HTimeDimension.Year);
		Assert.assertEquals((start.getYear() + 1), new Date(now.next().inMillis()).getYear());
	}

	/**
	 * data are collected during 10 days from 2012/12/12.
	 * @throws ParseException Date parsing error
	 */
	@Test
	public void testMergeOnHTime() throws ParseException {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		final Date current = dateFormat.parse("2012/12/12 12:12:12");
		//----
		addCube(current, 64);
		//----
		checkMergedMetric(HTimeDimension.Minute, //
				new DateBuilder(current).addMinutes(-1).toDateTime(), //
				new DateBuilder(current).addMinutes(2).toDateTime(), //
				1, 64, 64, 64);
		checkMergedMetric(HTimeDimension.SixMinutes, //
				new DateBuilder(current).addMinutes(-6).toDateTime(), //
				new DateBuilder(current).addMinutes(12).toDateTime(), //
				1, 64, 64, 64);
		checkMergedMetric(HTimeDimension.Hour, //
				new DateBuilder(current).addHours(-1).toDateTime(), //
				new DateBuilder(current).addHours(2).toDateTime(), //
				1, 64, 64, 64);
		checkMergedMetric(HTimeDimension.Day, //
				new DateBuilder(current).addDays(-1).toDateTime(), //
				new DateBuilder(current).addDays(2).toDateTime(), //
				1, 64, 64, 64);
		checkMergedMetric(HTimeDimension.Month, //
				new DateBuilder(current).addMonths(-1).toDateTime(), //
				new DateBuilder(current).addMonths(2).toDateTime(), //
				1, 64, 64, 64);
		checkMergedMetric(HTimeDimension.Year, //
				new DateBuilder(current).addYears(-1).toDateTime(), //
				new DateBuilder(current).addYears(2).toDateTime(), //
				1, 64, 64, 64);
	}

	@Test
	public void testMergeOnHTime2() throws ParseException {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		final Date current = dateFormat.parse("2012/12/12 12:12:12");
		//----
		addCube(current, 64);
		addCube(new DateBuilder(current).addMinutes(2).toDateTime(), 256);
		addCube(new DateBuilder(current).addMinutes(2 * 6).toDateTime(), 1024);
		addCube(new DateBuilder(current).addHours(2).toDateTime(), 4096);
		addCube(new DateBuilder(current).addDays(2).toDateTime(), 16384);
		addCube(new DateBuilder(current).addMonths(-2).toDateTime(), 65536); //remove 2 months in order to stay the same year
		addCube(new DateBuilder(current).addYears(2).toDateTime(), 262144);

		//----
		checkMergedMetric(HTimeDimension.Minute, //
				new DateBuilder(current).addMinutes(-1).toDateTime(), //
				new DateBuilder(current).addMinutes(2).toDateTime(), //
				1, 64, 64, 64);
		checkMergedMetric(HTimeDimension.SixMinutes, //
				new DateBuilder(current).addMinutes(-1 * 6).toDateTime(), //
				new DateBuilder(current).addMinutes(2 * 6).toDateTime(), //
				2, 64 + 256, 64, 256);
		checkMergedMetric(HTimeDimension.Hour, //
				new DateBuilder(current).addHours(-1).toDateTime(), //
				new DateBuilder(current).addHours(2).toDateTime(), //
				3, 64 + 256 + 1024, 64, 1024);
		checkMergedMetric(HTimeDimension.Day, //
				new DateBuilder(current).addDays(-1).toDateTime(), //
				new DateBuilder(current).addDays(2).toDateTime(), //
				4, 64 + 256 + 1024 + 4096, 64, 4096);
		checkMergedMetric(HTimeDimension.Month, //
				new DateBuilder(current).addMonths(-1).toDateTime(), //
				new DateBuilder(current).addMonths(2).toDateTime(), //
				5, 64 + 256 + 1024 + 4096 + 16384, 64, 16384);
		checkMergedMetric(HTimeDimension.Year, //
				new DateBuilder(current).addYears(-1).toDateTime(), //
				new DateBuilder(current).addYears(2).toDateTime(), //
				6, 64 + 256 + 1024 + 4096 + 16384 + 65536, 64, 65536);
	}

	//-------------------------------------------------------------------------
	//---------------------------STATIC ---------------------------------------
	//-------------------------------------------------------------------------
	private void addCube(final Date current, final int weightValue) {
		final HTime time = new HTime(current, HTimeDimension.Minute);
		final HKey key = new HKey(PAGES, time, "welcome");
		final HMetric durationMetric = new HMetricBuilder(HM_DURATION)//
				.withValue(100)//
				.build();
		final HMetric weightMetric = new HMetricBuilder(HM_WEIGHT)//
				.withValue(weightValue)//
				.build();
		final HCube cube = new HCubeBuilder()//
				.withMetric(durationMetric)//
				.withMetric(weightMetric)//
				.build();
		app.push(key, cube);
	}

	private void populateData(final Date startDate, final int days) {
		final long start = System.currentTimeMillis();
		System.out.println("start = " + startDate);

		long mc = 0;
		for (int day = 0; day < days; day++) {
			for (int h = 0; h < 24; h++) {
				for (int min = 0; min < 60; min++) {
					final Date current = new DateBuilder(startDate).addDays(day).addHours(h).addMinutes(min).toDateTime();
					final HTime time = new HTime(current, HTimeDimension.Minute);
					//--------
					final HKey key = new HKey(PAGES, time, "welcome");

					final HMetricBuilder durationMetricBuilder = new HMetricBuilder(HM_DURATION);
					for (int i = 0; i < 100; i++) {
						durationMetricBuilder.withValue(100 - i);
						durationMetricBuilder.withValue(100 + i);
					}

					final HMetric weightMetric = new HMetricBuilder(HM_WEIGHT).withValue(h).build();

					final HCube cube = new HCubeBuilder()//
							.withMetric(durationMetricBuilder.build())//
							.withMetric(weightMetric)//
							.build();

					app.push(key, cube);
					//--
					checkMemory(day);
					if ((mc++) % (60 * 24 * 10) == 0) {
						System.out.println(">>> day/hour/min = " + day + "/" + h + "/" + min + " in " + (System.currentTimeMillis() - start) + " ms");
					}
				}
			}
		}

	}

	private void checkMemory(final long day) {
		if ((Runtime.getRuntime().totalMemory() / Runtime.getRuntime().maxMemory()) > 0.9) {
			app.size();
			System.gc();
			//---
			if ((Runtime.getRuntime().totalMemory() / Runtime.getRuntime().maxMemory()) > 0.9) {
				System.out.println(">>>> total mem =" + Runtime.getRuntime().totalMemory());
				System.out.println(">>>> max  mem =" + Runtime.getRuntime().maxMemory());
				System.out.println(">>>> mem total > 90% - days =" + day);
				System.out.println(">>>> cubes count =" + app.size());

				System.out.println(">>>> cube footprint =" + (Runtime.getRuntime().maxMemory() / app.size()) + " octets");
				try {
					Thread.sleep(1000 * 20);
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		}
	}

	private void checkMergedMetric(final HTimeDimension timeDimension, final Date start, final Date end, final int expectedCount, final double expectedSum, final double expectedMin, final double expectedMax) {
		//----
		final HQuery query = new HQueryBuilder()//
				.onType(PAGES)//
				.on(timeDimension)//
				.between(start, end)//
				//	.whereCategoryEquals(PAGES)//
				.build();

		final HResult result = app.execute(query);
		Assert.assertEquals(query, result.getQuery());
		//Check : 1 category
		Assert.assertEquals(2, result.getAllCategories().size());
		//Check : 10*24 cubes per minute
		final Map<HTime, HCube> cubes = result.getSerie("").getCubes();
		Assert.assertEquals(3, cubes.size());
		//on vérifie
		final HTime startTime = new HTime(start, timeDimension);
		assertMetricEquals(cubes.get(startTime), HM_WEIGHT, 0, 0, Double.NaN, Double.NaN, Double.NaN);
		assertMetricEquals(cubes.get(startTime.next()), HM_WEIGHT, expectedCount, expectedSum, expectedSum / expectedCount, expectedMin, expectedMax);
		assertMetricEquals(cubes.get(startTime.next().next()), HM_WEIGHT, 0, 0, Double.NaN, Double.NaN, Double.NaN);
	}

	private static void assertMetricEquals(final HCube hCube, final String metricname, final double count, final double sum, final double mean, final double min, final double max) {
		assertMetricEquals(hCube.getMetric(metricname), count, sum, mean, min, max);
	}

	private static void assertMetricEquals(final HMetric metric, final double count, final double sum, final double mean, final double min, final double max) {
		if (metric != null) {
			Assert.assertEquals(count, metric.get(HCounterType.count), 0);
			Assert.assertEquals(count, metric.getCount(), 0); //test accesseur rapide
			Assert.assertEquals(sum, metric.get(HCounterType.sum), 0);//test accesseur rapide 0);
			Assert.assertEquals(mean, metric.get(HCounterType.mean), 0);
			Assert.assertEquals(mean, metric.getMean(), 0);//test accesseur rapide
			Assert.assertEquals(min, metric.get(HCounterType.min), 0);
			Assert.assertEquals(max, metric.get(HCounterType.max), 0);
		} else {
			Assert.assertEquals(count, 0, 0);
			Assert.assertEquals(sum, 0, 0);
			Assert.assertTrue(Double.isNaN(mean));
			Assert.assertTrue(Double.isNaN(min));
			Assert.assertTrue(Double.isNaN(max));
		}
	}
}
