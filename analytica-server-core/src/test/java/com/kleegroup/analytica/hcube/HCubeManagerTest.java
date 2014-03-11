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

import io.vertigo.kernel.lang.DateBuilder;

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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kleegroup.analytica.AbstractTestCaseJU4Rule;
import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;
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

/**
 * Cas de Test JUNIT de l'API Analytics.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManagerMemoryTest.java,v 1.2 2012/03/22 18:33:04 pchretien Exp $
 */
public final class HCubeManagerTest extends AbstractTestCaseJU4Rule {
	private static final HMetricKey MONTANT = new HMetricKey("MONTANT", false);
	private static final HMetricKey POIDS = new HMetricKey("POIDS", false);
	private static final HMetricKey DURATION = new HMetricKey(KProcess.DURATION, true);

	private static final String PROCESS_REQUEST = "REQUEST";
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

		final Set<HCategory> rootCategories = hcubeManager.getCategoryDictionary().getAllRootCategories();
		final HCategory processSQLCategory = new HCategory(PROCESS_SQL);
		//--- On vérifie la catégorie racine.
		Assert.assertEquals(1, rootCategories.size());
		Assert.assertEquals(processSQLCategory, rootCategories.iterator().next());
		//--- On vérifie les sous-catégories.
		final Set<HCategory> categories = hcubeManager.getCategoryDictionary().getAllSubCategories(processSQLCategory);
		Assert.assertEquals(2, categories.size());
	}

	@Test
	public void testSimpleProcess() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		final KProcess selectProcess2 = new KProcessBuilder(date, 100, PROCESS_SQL, "insert article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess2);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.withChildren(PROCESS_SQL) //pas d'aggregation
				.build();

		//final HCategory sqlCategory = new HCategory(PROCESS_SQL);
		final HResult result = hcubeManager.execute(daySqlQuery);

		final Set<HCategory> sqlCategories = result.getAllCategories();
		Assert.assertEquals(2, sqlCategories.size());

		for (final HCategory category : sqlCategories) {
			final List<HCube> cubes = result.getSerie(category).getCubes();
			Assert.assertEquals(1, cubes.size());
			final HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
			assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
		}
		//
	}

	@Test
	public void testSimpleProcessMultiSubCategrories() {
		final KProcess selectProcess3 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article", "id=12")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess3);
		final KProcess selectProcess4 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article", "id=13")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess4);
		final KProcess selectProcess5 = new KProcessBuilder(date, 100, PROCESS_SQL, "insert article", "id=12")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess5);

		final KProcess selectProcess6 = new KProcessBuilder(date, 100, PROCESS_SQL, "insert article", "id=13")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess6);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.withChildren(PROCESS_SQL) //pas d'aggregation
				.build();

		System.out.println(daySqlQuery + "Requete");
		final String[] sub = { "select article" };
		final HCategory sqlCategory = new HCategory(PROCESS_SQL, sub);
		final HResult result = hcubeManager.execute(daySqlQuery);

		final Set<HCategory> sqlCategories = result.getAllCategories();
		System.out.println(sqlCategory + "-----------");
		System.out.println(sqlCategories);
		Assert.assertEquals(2, sqlCategories.size());
		for (final HCategory category : sqlCategories) {
			final List<HCube> cubes = result.getSerie(category).getCubes();
			Assert.assertEquals(1, cubes.size());
			final HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
			assertMetricEquals(montantMetric, 2, price * 2, price, price, price);
		}
	}

	@Test
	public void testSimpleProcessWithAllTimeDimensions() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		final HCategory sqlCategory = new HCategory("SQL");

		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);

		final HQuery monthSqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Month)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		cubes = hcubeManager.execute(monthSqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);

		final HQuery yearSqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Month)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		cubes = hcubeManager.execute(yearSqlQuery).getSerie(sqlCategory).getCubes();
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
			selectDate = new DateBuilder(selectDate).addHours(1).toDateTime();
			kProcessBuilder//
					.beginSubProcess(selectDate, 100, PROCESS_SQL, "select article")//
					.incMeasure(POIDS.id(), 25)//
					.incMeasure(MONTANT.id(), price)//
					.endSubProcess();
		}
		final KProcess process = kProcessBuilder.build();

		hcubeManager.push(process);
		//---------------------------------------------------------------------
		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		final HCategory sqlCategory = new HCategory("SQL");
		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, nbSelect, price * nbSelect, price, price, price);
		//Durée
		HMetric durationMetric = cubes.get(0).getMetric(DURATION);
		assertMetricEquals(durationMetric, nbSelect, nbSelect * 100, 100, 100, 100);
		//---------------------------------------------------------------------
		final HQuery hourQuery = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from(date)//
				.to(new DateBuilder(date).addDays(1).build())//
				.with("SQL")//
				.build();
		cubes = hcubeManager.execute(hourQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(14, cubes.size());
		//cube 0==>10h00, 1==>11h etc
		montantMetric = cubes.get(5).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
		//---------------------------------------------------------------------
		final HQuery dayServiceslQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SERVICES")//
				.build();

		final HCategory servicesCategory = new HCategory("SERVICES", new String[0]);

		cubes = hcubeManager.execute(dayServiceslQuery).getSerie(servicesCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//Vérification de la durée du process principal
		durationMetric = cubes.get(0).getMetric(DURATION);
		assertMetricEquals(durationMetric, 1, 2000, 2000, 2000, 2000);
		//Vérification de la durée des sous-process
		final HMetric sqlMetric = cubes.get(0).getMetric(new HMetricKey(PROCESS_SQL, true));
		assertMetricEquals(sqlMetric, nbSelect, nbSelect * 100, 100, 100, 100);
	}

	/**
	 * Test composite à 3 niveaux
	 * - d'un processus maitre : //requete/get articles
	 * - consititué de n sous-processus : //services/get article
	 * - consititué de n sous-processus : //sql/select article
	 * les sous processus possèdent deux mesures
	 *  - Poids des articles (25 kg) par sous processus
	 *  - Prix des articles 10€
	 */
	@Test
	public void testDeepCompositeProcess() throws ParseException {
		//for (int i = 0; i < 60 * 24; i++) {

		final int nbService = 5;
		final int nbSelect = 3;
		final int dureeSelect = 100;
		final int dureeService = 150 + nbSelect * dureeSelect;
		final int dureeRequete = 75 + nbService * dureeService;

		final KProcessBuilder kProcessBuilder = new KProcessBuilder(date, dureeRequete, PROCESS_REQUEST, "articles.html");
		Date selectDate = date;
		for (int i = 0; i < nbService; i++) {
			selectDate = new DateBuilder(selectDate).addHours(1).toDateTime();
			final KProcessBuilder serviceProcessBuilder = kProcessBuilder.beginSubProcess(selectDate, dureeService, PROCESS_SERVICES, "get articles");//
			for (int j = 0; j < nbSelect; j++) {
				selectDate = new DateBuilder(selectDate).addMinutes(1).toDateTime();
				serviceProcessBuilder//
						.beginSubProcess(selectDate, dureeSelect, PROCESS_SQL, "select article")//
						.incMeasure(POIDS.id(), 25)//
						.incMeasure(MONTANT.id(), price)//
						.endSubProcess();
			}
			serviceProcessBuilder.endSubProcess();
		}

		final KProcess process = kProcessBuilder.build();

		hcubeManager.push(process);
		//---------------------------------------------------------------------
		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		final HCategory sqlCategory = new HCategory("SQL");
		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, nbSelect * nbService, price * nbSelect * nbService, price, price, price);
		//Durée
		HMetric durationMetric = cubes.get(0).getMetric(DURATION);
		assertMetricEquals(durationMetric, nbSelect * nbService, nbService * nbSelect * 100, 100, 100, 100);
		//---------------------------------------------------------------------
		final HQuery hourQuery = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from(date)//
				.to(new DateBuilder(date).addDays(1).build())//
				.with("SQL")//
				.build();
		cubes = hcubeManager.execute(hourQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(14, cubes.size());
		//cube 0==>10h00, 1==>11h etc
		montantMetric = cubes.get(5).getMetric(MONTANT);
		assertMetricEquals(montantMetric, nbSelect, price * nbSelect, price, price, price);
		//---------------------------------------------------------------------
		final HQuery dayServiceslQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SERVICES")//
				.build();

		final HCategory servicesCategory = new HCategory("SERVICES", new String[0]);

		cubes = hcubeManager.execute(dayServiceslQuery).getSerie(servicesCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//Vérification de la durée du process principal
		durationMetric = cubes.get(0).getMetric(DURATION);
		assertMetricEquals(durationMetric, nbService, nbService * dureeService, dureeService, dureeService, dureeService);
		//Vérification de la durée des sous-process
		final HMetric sqlMetric = cubes.get(0).getMetric(new HMetricKey(PROCESS_SQL, true));
		assertMetricEquals(sqlMetric, nbService * nbSelect, nbService * nbSelect * 100, 100, 100, 100);
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

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		final HCategory sqlCategory = new HCategory("SQL");
		final List<HCube> cubes = hcubeManager.execute(daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		final HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
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

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		final HCategory sqlCategory = new HCategory("SQL");
		List<HCube> cubes = hcubeManager.execute(daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 2, price * 4, 2 * price, price, 3 * price);
		//Check SQL/select article#1
		final HQuery daySelectQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL", "select article#3")//
				.build();

		final HCategory selectArticle3Category = new HCategory("SQL", new String[] { "select article#3" });

		cubes = hcubeManager.execute(daySelectQuery).getSerie(selectArticle3Category).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 3, price * 3, price * 3, price * 3);
	}

	@Test
	public void testMultiThread() throws InterruptedException {
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

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		final HCategory sqlCategory = new HCategory("SQL");

		final List<HCube> cubes = hcubeManager.execute(daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		final HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
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

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		final HResult hresult = hcubeManager.execute(daySqlQuery);

		final HCategory processSQLCategory = new HCategory(PROCESS_SQL);
		assertMetricEquals(hresult.getSerie(processSQLCategory).getMetric(POIDS), 2, 120, 60, 50, 70);
		assertMetricEquals(hresult.getSerie(processSQLCategory).getMetric(MONTANT), 2, price * 4, price * 2, price, price * 3);
	}

	private KProcess createSqlProcess(final int duration) {
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

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		final HCategory sqlCategory = new HCategory("SQL");

		final List<HCube> cubes = hcubeManager.execute(daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		final HMetric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 18, price * 18, price, price, price);

		final HMetric durationMetric = cubes.get(0).getMetric(DURATION);
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

	@Test
	//On charge 10 jours à 50 visites par jourDURATION
	public void testMuseum() {
		final int days = 10;
		final int visitsByDay = 200;
		new Museum(new PageListener() {

			@Override
			public void onPage(final KProcess process) {
				hcubeManager.push(process);
			}
		}).load(days, visitsByDay);

	}
}
