///**
// * Analytica - beta version - Systems Monitoring Tool
// *
// * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
// * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
// *
// * This program is free software; you can redistribute it and/or modify it under the terms
// * of the GNU General Public License as published by the Free Software Foundation;
// * either version 3 of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// * See the GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License along with this program;
// * if not, see <http://www.gnu.org/licenses>
// */
///**
// *
// */
//package io.analytica.server;
//
//import io.analytica.AbstractTestCaseJU4Rule;
//import io.analytica.api.KProcess;
//import io.analytica.api.KProcessBuilder;
//import io.analytica.hcube.HCubeStoreException;
//import io.analytica.hcube.cube.HCounterType;
//import io.analytica.hcube.cube.HCube;
//import io.analytica.hcube.cube.HMetric;
//import io.analytica.hcube.dimension.HCategory;
//import io.analytica.hcube.dimension.HTime;
//import io.analytica.hcube.dimension.HTimeDimension;
//import io.analytica.hcube.query.HQuery;
//import io.analytica.hcube.query.HQueryBuilder;
//import io.analytica.hcube.result.HResult;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.Map;
//import java.util.SortedMap;
//import java.util.TreeMap;
//
//import javax.inject.Inject;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * @author Stephane TATCHUM
// *
// */
//public class ServerManagerTest extends AbstractTestCaseJU4Rule {
//	private static final String APP_NAME = "MY_PTREETY_APP";
//
//	private static final String MONTANT = "MONTANT";
//	private static final String MONTANT_KEY = MONTANT;
//	private static final String PROCESS_SQL = "sql";
//
//	@Inject
//	private ServerManager serverManager;
//
//	private Date date;
//	private final int price = 8;
//
//	//	private void register(HMetricDefinition metricDefinition) {
//	//		Home.getDefinitionSpace().put(metricDefinition, HMetricDefinition.class);
//	//	}
//
//	@Before
//	public void init() throws ParseException {
//		date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).parse("08/05/2013 10:10");
//		//		register(new HMetricDefinition("HM_DURATION", true));
//		//		register(new HMetricDefinition("HM_RAM", true));
//		//		register(new HMetricDefinition("HM_IO", true));
//		//		register(new HMetricDefinition("HM_CPU", true));
//		//		register(new HMetricDefinition("HM_HEALTH", true));
//		//		register(new HMetricDefinition("HM_HEALTH_MAX", true));
//		//		register(new HMetricDefinition("HM_MONTANT", true));
//		//		register(new HMetricDefinition("HM_PERFORMANCE_MAX", true));
//		//		register(new HMetricDefinition("HM_PERFORMANCE", true));
//		//		register(new HMetricDefinition("HM_ACTIVITY_MAX", true));
//		//		register(new HMetricDefinition("HM_ACTIVITY", true));
//		//		register(new HMetricDefinition("HM_SESSION_HTTP", true));
//		//		register(new HMetricDefinition("HM_ERROR", true));
//		//		register(new HMetricDefinition("HM_SUB_DURATION", true));
//		//		register(new HMetricDefinition("HM_SERVICE", true));
//		//		register(new HMetricDefinition("HM_SUB__SERVICE", true));
//		//		register(new HMetricDefinition("HM_SQL", true));
//		//		register(new HMetricDefinition("HM_SEARCH", true));
//		//		register(new HMetricDefinition("HM_PAGE", true));
//		//		register(new HMetricDefinition("HM_SUB__PAGE", true));
//	}
//
//	private static void assertMetricEquals(final HMetric metric, final double count, final double sum, final double mean, final double min, final double max) {
//		Assert.assertEquals(count, metric.get(HCounterType.count), 0);
//		Assert.assertEquals(count, metric.getCount(), 0); //test accesseur rapide
//		Assert.assertEquals(sum, metric.get(HCounterType.sum), 0);//test accesseur rapide 0);
//		Assert.assertEquals(mean, metric.get(HCounterType.mean), 0);
//		Assert.assertEquals(mean, metric.getMean(), 0);//test accesseur rapide
//		Assert.assertEquals(min, metric.get(HCounterType.min), 0);
//		Assert.assertEquals(max, metric.get(HCounterType.max), 0);
//	}
//
//	@Test
//	public void testSimpleProcess() throws HCubeStoreException {
//		final KProcess selectProcess1 = new KProcessBuilder(APP_NAME, PROCESS_SQL, date, 100).withCategory("select article")//
//				.incMeasure(MONTANT, price)//
//				.build();
//		serverManager.push(selectProcess1);
//
//		final HQuery daySqlQuery = new HQueryBuilder()//
//		.between(HTimeDimension.Day, date, date)//
//		.whereCategoryMatches("sql")//
//		.build();
//
//		final HCategory processSQLCategory = new HCategory(PROCESS_SQL);
//		final HResult result = serverManager.execute(APP_NAME, "sql", daySqlQuery);
//		Assert.assertEquals(1, result.getAllCategories().size());
//
//		final Map<HTime, HCube> cubes = result.getSerie(processSQLCategory).getCubes();
//		Assert.assertEquals(1, cubes.size());
//		//
//		final HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
//		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
//	}
//
//	@Test
//	public void testVirtualData() throws InterruptedException {
//		final VirtualDatas virtualDatas = new VirtualDatas(serverManager);
//		virtualDatas.load();
//	}
//
//	@Test
//	public void testRandom() {
//		final SortedMap<Long, Integer> counts = new TreeMap<>();
//		for (int i = 0; i < 10000; i++) {
//			final long result =((long) (Math.random()*5))+1;
//			final int count = counts.containsKey(result) ? counts.get(result) : 0;
//			counts.put(result, count + 1);
//		}
//		System.out.println(counts);
//	}
//
//	@Test
//	public void testRandom2() {
//		final SortedMap<Long, Integer> counts = new TreeMap<>();
//		for (int i = 0; i < 10000; i++) {
//			final long result = ((long)(Math.random()*2))*100;
//			final int count = counts.containsKey(result) ? counts.get(result) : 0;
//			counts.put(result, count + 1);
//		}
//		System.out.println(counts);
//	}
//
//	@Test
//	public void testFormat() {
//		System.out.println(format(456654.456123));
//		System.out.println(format(456654.45));
//		System.out.println(format(456654.456));
//		System.out.println(format(456654.455));
//		System.out.println(format(456654.454));
//		System.out.println(format(456654.4));
//
//		System.out.println(format(456654.5));
//
//		System.out.println(format(456654.6));
//	}
//
//	private String format(final double val) {
//		final String value = String.valueOf(Math.round(val * 100) / 100d);
//		if (value.indexOf('.') > value.length() - 3) {
//			return value + "0"; //it miss a 0
//		}
//		return value;
//	}
//}
