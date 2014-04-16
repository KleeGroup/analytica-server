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

import io.analytica.AbstractTestCaseJU4Rule;
import io.analytica.api.KProcess;
import io.analytica.api.KProcessBuilder;
import io.analytica.hcube.cube.HCounterType;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HTimeDimension;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HQueryBuilder;
import io.analytica.hcube.result.HResult;
import io.analytica.museum.Museum;
import io.analytica.museum.PageListener;
import io.analytica.server.impl.ProcessEncoder;
import io.vertigo.kernel.lang.DateBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

/**
 * Cas de Test JUNIT de l'API Analytics.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManagerMemoryTest.java,v 1.2 2012/03/22 18:33:04 pchretien Exp $
 */
public final class HCubeManagerTest extends AbstractTestCaseJU4Rule {
	private static final String APP_NAME = "myApp";
	private static final String MONTANT = "MONTANT";
	private static final String POIDS = "POIDS";
	private static final HMetricKey MONTANT_KEY = new HMetricKey(MONTANT, false);
	private static final HMetricKey POIDS_KEY = new HMetricKey(POIDS, false);
	private static final HMetricKey DURATION_KEY = new HMetricKey(KProcess.DURATION, true);

	private static final String SYSTEM_NAME = "Server-Test";
	private static final String[] SYSTEM_LOCATION = { "test", "UnknownHost" };
	static {
		try {
			SYSTEM_LOCATION[1] = InetAddress.getLocalHost().getHostAddress();
		} catch (final UnknownHostException e) {
			//nothing, we keep UnknownHost
		}
	}
	private static final String PROCESS_REQUEST = "REQUEST";
	private static final String PROCESS_SERVICES = "SERVICES";
	private static final String PROCESS_SQL = "SQL";

	private ProcessEncoder processEncoder;

	@Inject
	private HCubeManager hcubeManager;

	private Date date;
	private final int price = 8;

	@Before
	public void init() throws ParseException {
		//On se place au 10-10-2010  a 10h10
		date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).parse("10/10/2010 10:10");
		processEncoder = new ProcessEncoder();
	}

	//-------------------------------------------------------------------------

	private static void assertMetricEquals(final HMetric metric, final double count, final double sum, final double mean, final double min, final double max) {
		Assert.assertEquals(count, metric.get(HCounterType.count), 0);
		Assert.assertEquals(count, metric.getCount(), 0); //test accesseur rapide
		Assert.assertEquals(sum, metric.get(HCounterType.sum), 0);//test accesseur rapide 0);
		Assert.assertEquals(mean, metric.get(HCounterType.mean), 0);
		Assert.assertEquals(mean, metric.getMean(), 0);//test accesseur rapide
		Assert.assertEquals(min, metric.get(HCounterType.min), 0);
		Assert.assertEquals(max, metric.get(HCounterType.max), 0);
	}

	//-------------------------------------------------------------------------
	@Test
	public void testDictionnary() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select * from article")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess1);

		//On crée un processs identique (même catégorie)
		final KProcess selectProcess2 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select * from article")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess2);

		final KProcess selectProcess3 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select * from user")//
				.build();
		pushProcess(selectProcess3);

		final Set<HCategory> rootCategories = hcubeManager.getCategoryDictionary().getAllRootCategories(APP_NAME);
		final HCategory processSQLCategory = new HCategory(PROCESS_SQL);
		//--- On vérifie la catégorie racine.
		Assert.assertEquals(1, rootCategories.size());
		Assert.assertEquals(processSQLCategory, rootCategories.iterator().next());
		//--- On vérifie les sous-catégories.
		final Set<HCategory> categories = hcubeManager.getCategoryDictionary().getAllSubCategories(APP_NAME, processSQLCategory);
		Assert.assertEquals(2, categories.size());
	}

	@Test
	public void testSimpleProcess() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess1);

		final KProcess selectProcess2 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "insert article")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess2);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.withChildren(PROCESS_SQL) //pas d'aggregation
				.build();

		//final HCategory sqlCategory = new HCategory(PROCESS_SQL);
		final HResult result = hcubeManager.execute(APP_NAME, daySqlQuery);

		final Set<HCategory> sqlCategories = result.getAllCategories();
		Assert.assertEquals(2, sqlCategories.size());

		for (final HCategory category : sqlCategories) {
			final List<HCube> cubes = result.getSerie(category).getCubes();
			Assert.assertEquals(1, cubes.size());
			final HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
			assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
		}
		//
	}

	@Test
	public void testSimpleProcessMultiSubCategrories() {
		final KProcess selectProcess3 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article", "id=12")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess3);
		final KProcess selectProcess4 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article", "id=13")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess4);
		final KProcess selectProcess5 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "insert article", "id=12")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess5);

		final KProcess selectProcess6 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "insert article", "id=13")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess6);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.withChildren(PROCESS_SQL) //pas d'aggregation
				.build();

		System.out.println(daySqlQuery + "Requete");
		final String[] sub = { "select article" };
		final HCategory sqlCategory = new HCategory(PROCESS_SQL, sub);
		final HResult result = hcubeManager.execute(APP_NAME, daySqlQuery);

		final Set<HCategory> sqlCategories = result.getAllCategories();
		System.out.println(sqlCategory + "-----------");
		System.out.println(sqlCategories);
		Assert.assertEquals(2, sqlCategories.size());
		for (final HCategory category : sqlCategories) {
			final List<HCube> cubes = result.getSerie(category).getCubes();
			Assert.assertEquals(1, cubes.size());
			final HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
			assertMetricEquals(montantMetric, 2, price * 2, price, price, price);
		}
	}

	@Test
	public void testSimpleProcessWithAllTimeDimensions() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess1);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		final HCategory sqlCategory = new HCategory("SQL");

		List<HCube> cubes = hcubeManager.execute(APP_NAME, daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);

		final HQuery monthSqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Month)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		cubes = hcubeManager.execute(APP_NAME, monthSqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);

		final HQuery yearSqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Month)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		cubes = hcubeManager.execute(APP_NAME, yearSqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
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
	public void testCompositeProcess() {
		//for (int i = 0; i < 60 * 24; i++) {

		final int nbSelect = 12;

		final KProcessBuilder kProcessBuilder = new KProcessBuilder(date, 2000, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SERVICES, "get articles");
		Date selectDate = date;
		for (int i = 0; i < nbSelect; i++) {
			selectDate = new DateBuilder(selectDate).addHours(1).toDateTime();
			kProcessBuilder//
					.beginSubProcess(selectDate, 100, PROCESS_SQL, "select article")//
					.incMeasure(POIDS, 25)//
					.incMeasure(MONTANT, price)//
					.endSubProcess();
		}
		final KProcess process = kProcessBuilder.build();

		pushProcess(process);
		//---------------------------------------------------------------------
		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		final HCategory sqlCategory = new HCategory("SQL");
		List<HCube> cubes = hcubeManager.execute(APP_NAME, daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, nbSelect, price * nbSelect, price, price, price);
		//Durée
		HMetric durationMetric = cubes.get(0).getMetric(DURATION_KEY);
		assertMetricEquals(durationMetric, nbSelect, nbSelect * 100, 100, 100, 100);
		//---------------------------------------------------------------------
		final HQuery hourQuery = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from(date)//
				.to(new DateBuilder(date).addDays(1).build())//
				.with("SQL")//
				.build();
		cubes = hcubeManager.execute(APP_NAME, hourQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(14, cubes.size());
		//cube 0==>10h00, 1==>11h etc
		montantMetric = cubes.get(5).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
		//---------------------------------------------------------------------
		final HQuery dayServiceslQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SERVICES")//
				.build();

		final HCategory servicesCategory = new HCategory("SERVICES", new String[0]);

		cubes = hcubeManager.execute(APP_NAME, dayServiceslQuery).getSerie(servicesCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//Vérification de la durée du process principal
		durationMetric = cubes.get(0).getMetric(DURATION_KEY);
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
	public void testDeepCompositeProcess() {
		//for (int i = 0; i < 60 * 24; i++) {

		final int nbService = 5;
		final int nbSelect = 3;
		final int dureeSelect = 100;
		final int dureeService = 150 + nbSelect * dureeSelect;
		final int dureeRequete = 75 + nbService * dureeService;

		final KProcessBuilder kProcessBuilder = new KProcessBuilder(date, dureeRequete, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_REQUEST, "articles.html");
		Date selectDate = date;
		for (int i = 0; i < nbService; i++) {
			selectDate = new DateBuilder(selectDate).addHours(1).toDateTime();
			final KProcessBuilder serviceProcessBuilder = kProcessBuilder.beginSubProcess(selectDate, dureeService, PROCESS_SERVICES, "get articles");//
			for (int j = 0; j < nbSelect; j++) {
				selectDate = new DateBuilder(selectDate).addMinutes(1).toDateTime();
				serviceProcessBuilder//
						.beginSubProcess(selectDate, dureeSelect, PROCESS_SQL, "select article")//
						.incMeasure(POIDS, 25)//
						.incMeasure(MONTANT, price)//
						.endSubProcess();
			}
			serviceProcessBuilder.endSubProcess();
		}

		final KProcess process = kProcessBuilder.build();

		pushProcess(process);
		//---------------------------------------------------------------------
		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		final HCategory sqlCategory = new HCategory("SQL");
		List<HCube> cubes = hcubeManager.execute(APP_NAME, daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, nbSelect * nbService, price * nbSelect * nbService, price, price, price);
		//Durée
		HMetric durationMetric = cubes.get(0).getMetric(DURATION_KEY);
		assertMetricEquals(durationMetric, nbSelect * nbService, nbService * nbSelect * 100, 100, 100, 100);
		//---------------------------------------------------------------------
		final HQuery hourQuery = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from(date)//
				.to(new DateBuilder(date).addDays(1).build())//
				.with("SQL")//
				.build();
		cubes = hcubeManager.execute(APP_NAME, hourQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(14, cubes.size());
		//cube 0==>10h00, 1==>11h etc
		montantMetric = cubes.get(5).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, nbSelect, price * nbSelect, price, price, price);
		//---------------------------------------------------------------------
		final HQuery dayServiceslQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SERVICES")//
				.build();

		final HCategory servicesCategory = new HCategory("SERVICES", new String[0]);

		cubes = hcubeManager.execute(APP_NAME, dayServiceslQuery).getSerie(servicesCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//Vérification de la durée du process principal
		durationMetric = cubes.get(0).getMetric(DURATION_KEY);
		assertMetricEquals(durationMetric, nbService, nbService * dureeService, dureeService, dureeService, dureeService);
		//Vérification de la durée des sous-process
		final HMetric sqlMetric = cubes.get(0).getMetric(new HMetricKey(PROCESS_SQL, true));
		assertMetricEquals(sqlMetric, nbService * nbSelect, nbService * nbSelect * 100, 100, 100, 100);
	}

	@Test
	public void testSimpleProcessWithMultiIncMeasure() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT, price)//
				.incMeasure(MONTANT, price)//
				.incMeasure(MONTANT, price)//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess1);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		final HCategory sqlCategory = new HCategory("SQL");
		final List<HCube> cubes = hcubeManager.execute(APP_NAME, daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		final HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, 1, price * 4, 4 * price, 4 * price, 4 * price);
	}

	@Test
	public void testMultiProcesses() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article#1")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess1);

		final KProcess selectProcess2 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article#2")//
				//.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess2);

		final KProcess selectProcess3 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article#3")//
				.incMeasure(MONTANT, price * 3)//
				.build();
		pushProcess(selectProcess3);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		final HCategory sqlCategory = new HCategory("SQL");
		List<HCube> cubes = hcubeManager.execute(APP_NAME, daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, 2, price * 4, 2 * price, price, 3 * price);
		//Check SQL/select article#1
		final HQuery daySelectQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL", "select article#3")//
				.build();

		final HCategory selectArticle3Category = new HCategory("SQL", new String[] { "select article#3" });

		cubes = hcubeManager.execute(APP_NAME, daySelectQuery).getSerie(selectArticle3Category).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, 1, price * 3, price * 3, price * 3, price * 3);
	}

	@Test
	public void testMultiThread() throws InterruptedException {
		final ExecutorService workersPool = Executors.newFixedThreadPool(20);
		for (int i = 0; i < 50; i++) {
			workersPool.execute(new Runnable() {
				@Override
				public void run() {
					final KProcess selectProcess1 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article")//
							.incMeasure(MONTANT, price)//
							.build();
					pushProcess(selectProcess1);
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

		final List<HCube> cubes = hcubeManager.execute(APP_NAME, daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		final HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, 50, price * 50, price, price, price);
	}

	@Test
	public void testHCube() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article#1")//
				.incMeasure(MONTANT, price)//
				.build();
		pushProcess(selectProcess1);

		final KProcess selectProcess2 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article#2")//
				.incMeasure(MONTANT, price * 3)//
				.incMeasure(POIDS, 50)//
				.build();
		pushProcess(selectProcess2);

		final KProcess selectProcess3 = new KProcessBuilder(date, 100, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article#3")//
				.incMeasure(POIDS, 70)//
				.build();
		pushProcess(selectProcess3);

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		final HResult hresult = hcubeManager.execute(APP_NAME, daySqlQuery);

		final HCategory processSQLCategory = new HCategory(PROCESS_SQL);
		assertMetricEquals(hresult.getSerie(processSQLCategory).getMetric(POIDS_KEY), 2, 120, 60, 50, 70);
		assertMetricEquals(hresult.getSerie(processSQLCategory).getMetric(MONTANT_KEY), 2, price * 4, price * 2, price, price * 3);
	}

	private KProcess createSqlProcess(final int duration) {
		return new KProcessBuilder(date, duration, SYSTEM_NAME, SYSTEM_LOCATION, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT, price)//
				.build();
	}

	@Test
	public void testDistributionProcess() {
		pushProcess(createSqlProcess(0)); //<=0
		pushProcess(createSqlProcess(1)); //<=1
		pushProcess(createSqlProcess(2)); //<=2
		pushProcess(createSqlProcess(3)); //<=5
		pushProcess(createSqlProcess(4)); //<=5
		pushProcess(createSqlProcess(5)); //<=5
		pushProcess(createSqlProcess(8)); //<=10
		pushProcess(createSqlProcess(9)); //<=10
		pushProcess(createSqlProcess(17)); //<=20
		pushProcess(createSqlProcess(18)); //<=20
		pushProcess(createSqlProcess(26)); //<=50
		pushProcess(createSqlProcess(63)); //<=100
		pushProcess(createSqlProcess(73)); //<=100
		pushProcess(createSqlProcess(83)); //<=100
		pushProcess(createSqlProcess(100)); //<=100
		pushProcess(createSqlProcess(99)); //<=100
		pushProcess(createSqlProcess(486)); //<=500
		pushProcess(createSqlProcess(15623)); //<=20000

		final HQuery daySqlQuery = new HQueryBuilder()//
				.on(HTimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();
		final HCategory sqlCategory = new HCategory("SQL");

		final List<HCube> cubes = hcubeManager.execute(APP_NAME, daySqlQuery).getSerie(sqlCategory).getCubes();
		Assert.assertEquals(1, cubes.size());
		//
		final HMetric montantMetric = cubes.get(0).getMetric(MONTANT_KEY);
		assertMetricEquals(montantMetric, 18, price * 18, price, price, price);

		final HMetric durationMetric = cubes.get(0).getMetric(DURATION_KEY);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(0d), 0);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(1d), 1);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(2d), 1);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(5d), 3);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(10d), 2);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(20d), 3);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(50d), 1);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(100d), 5);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(500d), 1);
		Assert.assertEquals(1, durationMetric.getDistribution().getData().get(20000d), 1);
	}

	@Test
	//On charge 10 jours à 50 visites par jourDURATION
	public void testMuseum() {
		final int days = 10;
		final int visitsByDay = 200;
		new Museum(new PageListener() {
			@Override
			public void onPage(final KProcess process) {
				pushProcess(process);
			}
		}).load(days, visitsByDay);

		//---------------------------------------------------------------------
		final HCategory pageCategory = new HCategory("PAGE");
		final Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		final HQuery hourQuery = new HQueryBuilder()//
				.on(HTimeDimension.Hour)//
				.from(today.getTime())//
				.to(new DateBuilder(today.getTime()).addDays(1).build())//
				.with("PAGE")//
				.build();
		final List<HCube> cubes = hcubeManager.execute(APP_NAME, hourQuery).getSerie(pageCategory).getCubes();
		Assert.assertEquals(24, cubes.size());
		//cube 0==>10h00, 1==>11h etc
		//final HMetric montantMetric = cubes.get(5).getMetric(DURATION);
		//assertMetricEquals(montantMetric, 1, 1, 1, 1, 1);
		//---------------------------------------------------------------------
	}

	private void pushProcess(final KProcess process) {
		final List<HCube> cubes = processEncoder.encode(process);
		for (final HCube cube : cubes) {
			hcubeManager.push(APP_NAME, cube);
		}
	}
}
