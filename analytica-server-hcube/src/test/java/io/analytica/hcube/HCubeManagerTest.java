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
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HCubeKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;
import io.analytica.hcube.impl.HCubeManagerImpl;
import io.analytica.hcube.plugins.store.memory.MemoryHCubeStorePlugin;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HQueryBuilder;
import io.analytica.hcube.result.HPoint;
import io.analytica.hcube.result.HResult;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.lang.DateBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test.
 * 
 *  - a request ==> page, duration, status 
 * 
 * @author pchretien
 */
public final class HCubeManagerTest {
	private static final String PAGES = "PAGES";
	private final HCubeManager cubeManager = new HCubeManagerImpl(new MemoryHCubeStorePlugin());

	@Test
	public void testQuery() {
		final Date start = new Date();
		HQuery query1 = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from(new DateBuilder(start).addHours(-3).toDateTime())//
				.to(start)//
				.with(PAGES)//
				.build();

		HQuery query2 = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from("NOW-3h")//
				.to("NOW")//
				.with(PAGES)//
				.build();
		//---
		Assert.assertEquals(3, query1.getAllTimes().size()); //3 HOURS
		Assert.assertEquals(3, query2.getAllTimes().size()); //3 HOURS
		Assert.assertTrue(query1.getAllTimes().containsAll(query2.getAllTimes()));
		Assert.assertTrue(query2.getAllTimes().containsAll(query1.getAllTimes()));
		Assert.assertEquals(query1.toString(), query2.toString());

	}

	@Test
	public void testQuery2() {
		HQuery query = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from("NOW")//
				.to("NOW+3d")//
				.with(PAGES)//
				.build();
		//---
		Assert.assertEquals(3 * 24, query.getAllTimes().size()); //72 hours 
	}

	@Test(expected = Exception.class)
	public void testQueryFail() {
		new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from("NOW")//
				.to("NOW-3m")//
				.with(PAGES)//
				.build();
	}

	@Test
	public void testCategoriesDictionnary() throws ParseException {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		final Date start = dateFormat.parse("2012/12/12");
		final int days = 1;
		//----	
		Assert.assertEquals(0, cubeManager.getCategoryDictionary().getAllRootCategories().size());
		Assert.assertEquals(0, cubeManager.getCategoryDictionary().getAllSubCategories(new HCategory(PAGES)).size());
		//---
		populateData(cubeManager, start, days);
		//----	
		Assert.assertEquals(1, cubeManager.getCategoryDictionary().getAllRootCategories().size());
		Assert.assertEquals(1, cubeManager.getCategoryDictionary().getAllSubCategories(new HCategory(PAGES)).size());
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
		populateData(cubeManager, start, days);
		//----	
		HQuery query = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from(start)//
				.to(end)//
				.with(PAGES)//
				.build();

		HResult result = cubeManager.execute(query);

		Assert.assertEquals(query, result.getQuery());

		//Check : 1 category
		Assert.assertEquals(1, result.getAllCategories().size());

		//Check : 24 cubes(per hour) by day
		Assert.assertEquals(24, result.getSerie(new HCategory(PAGES)).getCubes().size());

		//Check : serie contains 2 metric (DURATION and WEIGHT)
		Assert.assertEquals(2, result.getSerie(new HCategory(PAGES)).getMetrics().size());

		//
		HSerie serie = result.getSerie(new HCategory(PAGES));
		Assert.assertEquals(new HCategory(PAGES), serie.getCategory());

		for (HCube cube : serie.getCubes()) {
			Assert.assertTrue(cube.toString().startsWith("{"));
			Assert.assertTrue(cube.toString().endsWith("}"));
			Assert.assertEquals(2, cube.getMetrics().size());
			HMetric metric = cube.getMetric(new HMetricKey("DURATION", true));
			Assert.assertEquals(100 * 60 * 2, metric.getCount(), 0);
			Assert.assertEquals(100, metric.getMean(), 0);
			Assert.assertEquals(1, metric.get(HCounterType.min), 0);
		}
		HMetric metric = serie.getMetric(new HMetricKey("DURATION", true));
		Assert.assertTrue(metric.toString().startsWith("{"));
		Assert.assertTrue(metric.toString().endsWith("}"));

		Assert.assertEquals("DURATION", metric.getKey().getName());
		Assert.assertEquals(100 * 60 * 2 * 24, metric.getCount(), 0);
		Assert.assertEquals(100, metric.getMean(), 0);
		Assert.assertTrue(metric.get(HCounterType.stdDev) > 0);

		Assert.assertEquals(1, metric.get(HCounterType.min), 0);
		//---

		List<HPoint> points = serie.getPoints(new HMetricKey("DURATION", true));
		Assert.assertEquals(24, points.size());
		int h = 0;
		for (HPoint point : serie.getPoints(new HMetricKey("DURATION", true))) {
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
		populateData(cubeManager, start, days);
		//----	
		HQuery query = new HQueryBuilder()//
				.on(HTimeDimension.SixMinutes)//
				.from(start)//
				.to(end)//
				.with(PAGES)//
				.build();

		HResult result = cubeManager.execute(query);

		Assert.assertEquals(query, result.getQuery());

		//Check : 1 category
		Assert.assertEquals(1, result.getAllCategories().size());

		//Check : 10*24 cubes per minute
		Assert.assertEquals(240, result.getSerie(new HCategory(PAGES)).getCubes().size());
	}

	private static void populateData(final HCubeManager cubeManager, final Date startDate, int days) {
		long start = System.currentTimeMillis();
		System.out.println("start = " + startDate);
		final HCategory category = new HCategory(PAGES, "WELCOME");

		for (int day = 0; day < days; day++) {
			for (int h = 0; h < 24; h++) {
				for (int min = 0; min < 60; min++) {
					final Date current = new DateBuilder(startDate).addDays(day).addHours(h).addMinutes(min).toDateTime();
					final HTime time = new HTime(current, HTimeDimension.Minute);
					//--------		
					final HCubeKey cubeKey = new HCubeKey(time, category/*, location*/);

					final HMetricKey duration = new HMetricKey("DURATION", true);
					final HMetricBuilder metricBuilder = new HMetricBuilder(duration);
					for (int i = 0; i < 100; i++) {
						metricBuilder.withValue(100 - i);
						metricBuilder.withValue(100 + i);
					}

					final HMetricKey weight = new HMetricKey("WEIGHT", false);
					final HMetric weightMetric = new HMetricBuilder(weight).withValue(h).build();

					final HCube cube = new HCubeBuilder(cubeKey)//
							.withMetric(metricBuilder.build())//
							.withMetric(weightMetric)//
							.build();

					cubeManager.push(cube);
				}
			}
			if (day % 100 == 0) {
				System.out.println(">>> day = " + day + " in " + (System.currentTimeMillis() - start) + " ms");
			}
		}
	}

	@Test
	public void testTimeDimension() {
		Date start = new Date();
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
}
