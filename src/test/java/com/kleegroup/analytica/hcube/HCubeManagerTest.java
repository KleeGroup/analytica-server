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
package com.kleegroup.analytica.hcube;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import kasper.AbstractTestCaseJU4;
import kasper.kernel.lang.DateBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;
import com.kleegroup.analytica.hcube.cube.HCounterType;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.result.HResult;

/**
 * Cas de Test JUNIT de l'API Analytics.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManagerMemoryTest.java,v 1.2 2012/03/22 18:33:04 pchretien Exp $
 */
public final class HCubeManagerTest extends AbstractTestCaseJU4 {
	private static final HMetricKey MONTANT = new HMetricKey("MONTANT", false);
	private static final HMetricKey POIDS = new HMetricKey("POIDS", false);
	private static final HMetricKey DURATION = new HMetricKey(KProcess.DURATION, true);

	private static final String PROCESS_SERVICES = "SERVICES";
	private static final String PROCESS_SQL = "SQL";

	@Inject
	private HCubeManager hcubeManager;

	private Date date;
	private final int price = 8;

	@Before
	public void init() throws ParseException {
		//On se place au 10-10-2010  a 10h10
		date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).parse("10/10/2010 10:10");
	}

	//-------------------------------------------------------------------------

	private static void assertMetricEquals(HMetric metric, double count, double sum, double mean, double min, double max) {
		Assert.assertEquals(count, metric.get(HCounterType.count), 0);
		Assert.assertEquals(count, metric.getCount(), 0); //test accesseur rapide
		Assert.assertEquals(sum, metric.get(HCounterType.sum), 0);//test accesseur rapide 0);
		Assert.assertEquals(sum, metric.getSum(), 0);
		Assert.assertEquals(mean, metric.get(HCounterType.mean), 0);
		Assert.assertEquals(mean, metric.getMean(), 0);//test accesseur rapide
		Assert.assertEquals(min, metric.get(HCounterType.min), 0);
		Assert.assertEquals(max, metric.get(HCounterType.max), 0);
	}

	//-------------------------------------------------------------------------
	@Test
	public void testDictionnary() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select * from article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		//On crée un processs identique (même catégorie)
		final KProcess selectProcess2 = new KProcessBuilder(date, 100, PROCESS_SQL, "select * from article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess2);

		final KProcess selectProcess3 = new KProcessBuilder(date, 100, PROCESS_SQL, "select * from user")//
				.build();
		hcubeManager.push(selectProcess3);

		Set<HCategory> rootCategories = hcubeManager.getCategoryDictionary().getAllRootCategories();
		HCategory processSQLCategory = new HCategory(PROCESS_SQL, new String[0]);
		//--- On vérifie la catégorie racine.
		Assert.assertEquals(1, rootCategories.size());
		Assert.assertEquals(processSQLCategory, rootCategories.iterator().next());
		//--- On vérifie les sous-catégories.
		Set<HCategory> categoryPositions = hcubeManager.getCategoryDictionary().getAllCategories(processSQLCategory);
		Assert.assertEquals(2, categoryPositions.size());
	}

	@Test
	public void testSimpleProcess() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		final HQuery daySqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		HCategory sqlCategory = new HCategory("SQL", new String[0]);
		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
	}

	@Test
	public void testSimpleProcessWithAllTimeDimensions() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		final HQuery daySqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		HCategory sqlCategory = new HCategory("SQL", new String[0]);

		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);

		final HQuery monthSqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Month)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		cubes = hcubeManager.execute(monthSqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);

		final HQuery yearSqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Month)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		cubes = hcubeManager.execute(yearSqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
	}

	/**
	 * Test simple 
	 * - d'un processus maitre : //services/get articles
	 * - consititué de n sous-processus : //sql/select article
	 * les sous processus possèdent deux mesures
	*  - Poids des articles (25 kg) par sous processus
	*  - Prix des articles 10€	
	 */
	@Test
	public void testCompositeProcess() throws ParseException {
		//for (int i = 0; i < 60 * 24; i++) {

		final int nbSelect = 12;

		final KProcessBuilder kProcessBuilder = new KProcessBuilder(date, 2000, PROCESS_SERVICES, "get articles");
		Date selectDate = date;
		for (int i = 0; i < nbSelect; i++) {
			final KProcess selectProcess = new KProcessBuilder(selectDate, 100, PROCESS_SQL, "select article")//
					.incMeasure(POIDS.id(), 25)//
					.incMeasure(MONTANT.id(), price)//
					.build();
			selectDate = new DateBuilder(selectDate).addHours(1).toDateTime();

			kProcessBuilder.addSubProcess(selectProcess);
		}
		final KProcess process = kProcessBuilder.build();

		hcubeManager.push(process);
		//---------------------------------------------------------------------
		HQuery daySqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		HCategory sqlCategory = new HCategory("SQL", new String[0]);
		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, nbSelect, price * nbSelect, price, price, price);
		//Durée	
		HMetric durationMetric = cubes.get(0).getMetric(DURATION);
		assertMetricEquals(durationMetric, nbSelect, nbSelect * 100, 100, 100, 100);
		//---------------------------------------------------------------------
		final HQuery hourQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from(date)//
				.to(new DateBuilder(date).addDays(1).build())//
				.with("SQL")//
				.build();
		cubes = hcubeManager.execute(hourQuery).getCubes(sqlCategory);
		Assert.assertEquals(14, cubes.size());
		//cube 0==>10h00, 1==>11h etc
		montantMetric = cubes.get(5).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
		//---------------------------------------------------------------------
		HQuery dayServiceslQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SERVICES")//
				.build();

		HCategory servicesCategoryPosition = new HCategory("SERVICES", new String[0]);

		cubes = hcubeManager.execute(dayServiceslQuery).getCubes(servicesCategoryPosition);
		Assert.assertEquals(1, cubes.size());
		//Vérification de la durée du process principal
		durationMetric = cubes.get(0).getMetric(DURATION);
		assertMetricEquals(durationMetric, 1, 2000, 2000, 2000, 2000);
		//Vérification de la durée des sous-process 
		HMetric sqlMetric = cubes.get(0).getMetric(new HMetricKey(PROCESS_SQL, true));
		assertMetricEquals(sqlMetric, nbSelect, nbSelect * 100, 100, 100, 100);
	}

	@Test
	public void testSimpleProcessWithMultiIncMeasure() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT.id(), price)//
				.incMeasure(MONTANT.id(), price)//
				.incMeasure(MONTANT.id(), price)//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		final HQuery daySqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		HCategory sqlCategory = new HCategory("SQL", new String[0]);
		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 4, 4 * price, 4 * price, 4 * price);
	}

	@Test
	public void testMultiProcesses() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article#1")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		final KProcess selectProcess2 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article#2")//
				//.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess2);

		final KProcess selectProcess3 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article#3")//
				.incMeasure(MONTANT.id(), price * 3)//
				.build();
		hcubeManager.push(selectProcess3);

		final HQuery daySqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		HCategory sqlCategory = new HCategory("SQL", new String[0]);
		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 2, price * 4, 2 * price, price, 3 * price);
		//Check SQL/select article#1
		final HQuery daySelectQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL", "select article#3")//
				.build();

		HCategory selectArticle3Category = new HCategory("SQL", new String[] { "select article#3" });

		cubes = hcubeManager.execute(daySelectQuery).getCubes(selectArticle3Category);
		Assert.assertEquals(1, cubes.size());
		//
		montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 3, price * 3, price * 3, price * 3);
	}

	@Test
	public void testMultiThread() throws InterruptedException {
		final long start = System.currentTimeMillis();
		final ExecutorService workersPool = Executors.newFixedThreadPool(20);
		for (int i = 0; i < 50; i++) {
			workersPool.execute(new Runnable() {
				@Override
				public void run() {
					final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article")//
							.incMeasure(MONTANT.id(), price)//
							.build();
					hcubeManager.push(selectProcess1);
				}
			});
		}
		workersPool.shutdown();
		workersPool.awaitTermination(30, TimeUnit.SECONDS); //On laisse 30 secondes pour vider la pile   
		Assert.assertTrue("Les threads ne sont pas tous stoppés", workersPool.isTerminated());

		final HQuery daySqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		HCategory sqlCategory = new HCategory("SQL", new String[0]);

		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 50, price * 50, price, price, price);
	}

	@Test
	public void testHCube() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article#1")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		final KProcess selectProcess2 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article#2")//
				.incMeasure(MONTANT.id(), price * 3)//
				.incMeasure(POIDS.id(), 50)//
				.build();
		hcubeManager.push(selectProcess2);

		final KProcess selectProcess3 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article#3")//
				.incMeasure(POIDS.id(), 70)//
				.build();
		hcubeManager.push(selectProcess3);

		final HQuery daySqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		HResult hresult = hcubeManager.execute(daySqlQuery);

		assertMetricEquals(hresult.getMetric(POIDS), 2, 120, 60, 50, 70);
		assertMetricEquals(hresult.getMetric(MONTANT), 2, price * 4, price * 2, price, price * 3);
	}

	private KProcess createSqlProcess(int duration) {
		return new KProcessBuilder(date, duration, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
	}

	@Test
	public void testClusteredValuesProcess() {
		hcubeManager.push(createSqlProcess(0)); //<=0
		hcubeManager.push(createSqlProcess(1)); //<=1
		hcubeManager.push(createSqlProcess(2)); //<=2
		hcubeManager.push(createSqlProcess(3)); //<=5
		hcubeManager.push(createSqlProcess(4)); //<=5
		hcubeManager.push(createSqlProcess(5)); //<=5
		hcubeManager.push(createSqlProcess(8)); //<=10
		hcubeManager.push(createSqlProcess(9)); //<=10
		hcubeManager.push(createSqlProcess(17)); //<=20
		hcubeManager.push(createSqlProcess(18)); //<=20
		hcubeManager.push(createSqlProcess(26)); //<=50
		hcubeManager.push(createSqlProcess(63)); //<=100
		hcubeManager.push(createSqlProcess(73)); //<=100
		hcubeManager.push(createSqlProcess(83)); //<=100
		hcubeManager.push(createSqlProcess(100)); //<=100
		hcubeManager.push(createSqlProcess(99)); //<=100
		hcubeManager.push(createSqlProcess(486)); //<=500
		hcubeManager.push(createSqlProcess(15623)); //<=20000

		final HQuery daySqlQuery = hcubeManager.createQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		HCategory sqlCategory = new HCategory("SQL", new String[0]);

		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getCubes(sqlCategory);
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 18, price * 18, price, price, price);

		HMetric durationMetric = cubes.get(0).getMetric(DURATION);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(0d), 0);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(1d), 1);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(2d), 1);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(5d), 3);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(10d), 2);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(20d), 3);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(50d), 1);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(100d), 5);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(500d), 1);
		Assert.assertEquals(1, durationMetric.getClusteredValues().get(20000d), 1);
	}
}
