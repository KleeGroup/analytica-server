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
import com.kleegroup.analytica.hcube.cube.Cube;
import com.kleegroup.analytica.hcube.cube.DataType;
import com.kleegroup.analytica.hcube.cube.Metric;
import com.kleegroup.analytica.hcube.cube.MetricKey;
import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.query.Query;
import com.kleegroup.analytica.hcube.query.QueryBuilder;

/**
 * Cas de Test JUNIT de l'API Analytics.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManagerMemoryTest.java,v 1.2 2012/03/22 18:33:04 pchretien Exp $
 */
public final class HCubeManagerTest extends AbstractTestCaseJU4 {
	private static final MetricKey MONTANT = new MetricKey("MONTANT");
	private static final MetricKey POIDS = new MetricKey("POIDS");

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

	private static void assertMetricEquals(Metric metric, double count, double sum, double mean, double min, double max) {
		Assert.assertEquals(count, metric.get(DataType.count), 0);
		Assert.assertEquals(sum, metric.get(DataType.sum), 0);
		Assert.assertEquals(mean, metric.get(DataType.mean), 0);
		Assert.assertEquals(min, metric.get(DataType.min), 0);
		Assert.assertEquals(max, metric.get(DataType.max), 0);
	}

	//-------------------------------------------------------------------------
	@Test
	public void testSimpleProcess() {
		final KProcess selectProcess1 = new KProcessBuilder(date, 100, PROCESS_SQL, "select article")//
				.incMeasure(MONTANT.id(), price)//
				.build();
		hcubeManager.push(selectProcess1);

		final Query daySqlQuery = new QueryBuilder()//
				.on(TimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		List<Cube> cubes = hcubeManager.findAll(daySqlQuery);
		Assert.assertEquals(1, cubes.size());
		//
		Metric montantMetric = cubes.get(0).getMetric(MONTANT);
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
		//
		final Query daySqlQuery = new QueryBuilder()//
				.on(TimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		List<Cube> cubes = hcubeManager.findAll(daySqlQuery);
		Assert.assertEquals(1, cubes.size());
		//
		Metric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, nbSelect, price * nbSelect, price, price, price);

		final Query hourQuery = new QueryBuilder()//
				.on(TimeDimension.Hour)//
				.from(date)//
				.to(new DateBuilder(date).addDays(1).build())//
				.with("SQL")//
				.build();
		cubes = hcubeManager.findAll(hourQuery);
		Assert.assertEquals(14, cubes.size());
		//cube 0==>10h00, 1==>11h etc
		System.out.println(">>>" + cubes);
		montantMetric = cubes.get(5).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 1, price * 1, price, price, price);
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

		final Query daySqlQuery = new QueryBuilder()//
				.on(TimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		List<Cube> cubes = hcubeManager.findAll(daySqlQuery);
		Assert.assertEquals(1, cubes.size());
		//
		Metric montantMetric = cubes.get(0).getMetric(MONTANT);
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

		final Query daySqlQuery = new QueryBuilder()//
				.on(TimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		List<Cube> cubes = hcubeManager.findAll(daySqlQuery);
		Assert.assertEquals(1, cubes.size());
		//
		Metric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 2, price * 4, 2 * price, price, 3 * price);
		//Check SQL/select article#1
		final Query daySelectQuery = new QueryBuilder()//
				.on(TimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL", "select article#3")//
				.build();

		cubes = hcubeManager.findAll(daySelectQuery);
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

		final Query daySqlQuery = new QueryBuilder()//
				.on(TimeDimension.Day)//
				.from(date)//
				.to(date)//
				.with("SQL")//
				.build();

		List<Cube> cubes = hcubeManager.findAll(daySqlQuery);
		Assert.assertEquals(1, cubes.size());
		//
		Metric montantMetric = cubes.get(0).getMetric(MONTANT);
		assertMetricEquals(montantMetric, 50, price * 50, price, price, price);
	}
	//
	//		log.trace("elapsed = " + (System.currentTimeMillis() - start));
	//		//	printDatas(MONTANT);
	//		//System.out.println(analyticaUIManager.toString(serverManager.getProcesses()));
	//	}
	//
	//	private static KProcessBuilder createProcess(final String module, final String fullName, Date date) {
	//		return new KProcessBuilder(module, fullName, date, 10);
	//	}
	//
	//	private static KProcessBuilder createProcess(final String module, final String fullName, final long time) {
	//		return new KProcessBuilder(module, fullName, new Date(), time);
	//	}
}
