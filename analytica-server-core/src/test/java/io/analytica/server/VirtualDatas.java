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
/**
 * 
 */
package io.analytica.server;

import io.analytica.api.KProcess;
import io.analytica.api.KProcessBuilder;
import io.vertigo.kernel.lang.Assertion;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author npiedeloup
 */
public class VirtualDatas {
	private final ServerManager serverManager;

	private static final String SYSTEM_NAME = "Server-Test";
	private static final String[] SYSTEM_LOCATION = { "test", "UnknownHost" };
	static {
		try {
			SYSTEM_LOCATION[1] = InetAddress.getLocalHost().getHostAddress();
		} catch (final UnknownHostException e) {
			//nothing, we keep UnknownHost
		}
	}
	private static final String PAGE_PROCESS = "PAGE";
	private static final String SQL_PROCESS = "SQL";
	private static final String SEARCH_PROCESS = "SEARCH";

	private static final double NB_VISIT_DAILY_MAX = 10;

	public VirtualDatas(final ServerManager serverManager) {
		this.serverManager = serverManager;
	}

	public void load() throws InterruptedException {
		//Toutes les visites sur 3h, 100visites par heures

		//Start date = 8h
		//Date startDate = new Date();
		final Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		System.out.println("StartDate : " + startDate);
		loadVisitors(startDate.getTime(), 200);
	}

	//	public void loadVisitors(final Date startDate, final double hours, final double visitorByHour) {
	// visitByHour represente finalement le nombre max de visites
	public void loadVisitors(final Date startDate, final double visitorByHour) throws InterruptedException {

		//Pics à 10 h et à 15h
		//final String d = startDate.toLocaleString();

		//		for (int j = 0; j < 5; j++) {
		//			final Date dateJour = new Date(startDate.getTime() + j * 24 * 60 * 60 * 1000L);
		//			for (int h = 0; h < 12; h++) {
		//				final double coef = 1.5 + 0.25 * Math.sin(j * Math.PI / 2 / (6 * 30));
		//				final long nbVisit = random(NB_VISIT_DAILY_MAX * Math.cos((h - 3) / 2d / 3.14d), coef); // 50 visites en moyenne par heure en pic
		//				for (int visit = 0; visit < nbVisit; visit++) {
		//					final Date dateVisite = new Date(dateJour.getTime() + h * 60 * 60 * 1000L + random(visit * (50 * 60 * 1000L / nbVisit) + 5 * 60 * 1000L, 1));
		//					addVisitorScenario(dateVisite, coef);
		//				}
		//			}
		//		}
		long nbVisit;
		for (int j = 4; j >= 0; j--) {
			//			final Date dateJour = new Date(startDate.getTime() + j * 24 * 60 * 60 * 1000L);
			final Date date = new Date(startDate.getTime() + j * 24 * 60 * 60 * 1000L);
			for (int h = 7; h < 19; h++) {

				//final double coef = 1.5 + 0.25 * Math.sin((h - 8) * Math.PI / 2 / (6 * 30));
				final double coef = 0.25 + 0.25 * Math.sin((h - 7 + 4.5) * Math.PI / 3); //varie de 0 à 0.5
				//if (h == 10 || h == 15) {
				//	nbVisit = Math.round(NB_VISIT_DAILY_MAX * 0.6d);
				//} else {
				//	nbVisit = Math.round(NB_VISIT_DAILY_MAX * 0.2d);
				nbVisit = random(Math.round(NB_VISIT_DAILY_MAX * 0.3d), coef * 2); // de 30% à 60% en fonction de l'heure
				//}
				//System.out.println(h + "\t" + coef + "\t" + nbVisit);
				for (int visit = 0; visit < nbVisit; visit++) {
					final Date dateVisite = new Date(date.getTime() + h * 60 * 60 * 1000 + visit * 60 * 60 * 1000L / nbVisit);
					//System.out.println(dateVisite + "\t" + h + "\t" + coef + "\t" + nbVisit);
					addVisitorScenario(dateVisite, coef);
				}

			}
		}

	}

	//final long nbVisit = (long) (Math.random() * hours * visitorByHour);
	//		for (int visit = 0; visit < nbVisit; visit++) {
	//			final Date dateVisite = new Date(startDate.getTime() - visit * 60 * 1000);
	//			System.out.println("Date début de la visite ::" + dateVisite);
	//			addVisitorScenario(dateVisite, coef);
	//		}
	//		final int nbVisit = 0;
	//		final Date startDateVisite = new Date(System.currentTimeMillis());
	//		for (int visit = 0; visit < nbVisit; visit++) {
	//			final int h = 0;
	//			final Date dateVisite = new Date(startDateVisite.getTime() - h * visit * 1000);// e.g 30 sec
	//			addVisitorScenario(dateVisite);
	//		}

	//	public void loadContributors(final int offSetSeconds, final int nbVisit) throws ParseException {
	//		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	//		final Date startDate = sdf.parse("29/05/2013 08:00:00");
	//
	//		Date dateVisite = new Date(System.currentTimeMillis());
	//		for (int visit = 0; visit < nbVisit; visit++) {
	//			dateVisite = new Date(dateVisite.getTime() - offSetSeconds * visit * 1000);// e.g 30 sec
	//			addContributorScenario(dateVisite, offSetSeconds);
	//		}
	//
	//	}

	//	private void addContributorScenario(final Date dateVisite, final int offSetSeconds) {
	//		addLoginProcesses(offSetSeconds, 150, dateVisite);
	//		//		addLoginProcesses(offSetSeconds, , dateVisite);
	//		//		addPageAccueil(offSetSeconds, , dateVisite);
	//		//		addSearchProcess(offSetSeconds, , dateVisite);
	//
	//	}

	private void addVisitorScenario(final Date startVisite, final double coef) throws InterruptedException {
		final long waitTime = 30 * 1000;

		addHomePage(startVisite, random(150, coef));
		Thread.sleep(10);
		addSearchPage(new Date(startVisite.getTime() + waitTime), random(750, coef));
		Thread.sleep(10);
		for (int i = 0; i < 3; i++) {
			addViewPage(new Date(startVisite.getTime() + waitTime + waitTime * i), random(200, coef));
			Thread.sleep(10);
		}
	}

	/*	private void addContriutorScenario(final Date startVisite, final double coef) throws InterruptedException {
			final long waitTime = 30 * 1000;
			addHomePage(startVisite, coef);
			Thread.sleep(10);
			addSearchPage(new Date(startVisite.getTime() + waitTime), coef);
			Thread.sleep(10);
			for (int i = 0; i < 10; i++) {
				addSearchPage(new Date(startVisite.getTime() + waitTime), coef);
				Thread.sleep(10);
				addUpdatePage(new Date(startVisite.getTime() + waitTime + waitTime * i), coef);
				Thread.sleep(10);
			}

		}
	*/
	private void addHomePage(final Date dateVisite, final double processDuration) {
		//		final double processDuration = Math.random() * 50 + 150d;
		//	final double processDuration = 150d + 100 * Math.sin(dateVisite.getMinutes() * Math.PI / 60);

		final KProcess sqlProcess = new KProcessBuilder(dateVisite, 80, SYSTEM_NAME, SYSTEM_LOCATION, SQL_PROCESS, "select*from news").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, SYSTEM_NAME, SYSTEM_LOCATION, PAGE_PROCESS, "home", "homePage").addSubProcess(sqlProcess).build();
		serverManager.push(pageProcess.getSystemName(), pageProcess.getSystemLocation(), pageProcess);
	}

	private void addSearchPage(final Date dateVisite, final double processDuration) {
		//final double processDuration = Math.random() * 50 + 150d;
		//	final double processDuration = 150d + 100 * Math.sin(dateVisite.getMinutes() * Math.PI / 60);

		final KProcess searchProcess = new KProcessBuilder(dateVisite, 80, SYSTEM_NAME, SYSTEM_LOCATION, SEARCH_PROCESS, "find oeuvres").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, SYSTEM_NAME, SYSTEM_LOCATION, PAGE_PROCESS, "search").addSubProcess(searchProcess).build();
		serverManager.push(pageProcess.getSystemName(), pageProcess.getSystemLocation(), pageProcess);
		//System.out.println("Recherche " + dateVisite);

	}

	private void addViewPage(final Date dateVisite, final double processDuration) {
		//final double processDuration = Math.random() * 50 + 150d;
		//final double processDuration = 150d + 100 * Math.sin(dateVisite.getMinutes() * Math.PI / 60);

		final KProcess searchProcess = new KProcessBuilder(dateVisite, 80, SYSTEM_NAME, SYSTEM_LOCATION, SQL_PROCESS, "select 1 from oeuvres").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, SYSTEM_NAME, SYSTEM_LOCATION, PAGE_PROCESS, "oeuvre").addSubProcess(searchProcess).build();
		serverManager.push(pageProcess.getSystemName(), pageProcess.getSystemLocation(), pageProcess);
		//System.out.println("Consultation " + dateVisite);

	}

	/*	private void addUpdatePage(final Date dateVisite, final double processDuration) {
			//final double processDuration = Math.random() * 50 + 150d;

		final KProcess updateProcess = new KProcessBuilder(dateVisite, 80, SYSTEM_NAME, SYSTEM_LOCATION, SQL_PROCESS, "update 1 from oeuvres").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, SYSTEM_NAME, SYSTEM_LOCATION, PAGE_PROCESS, "/oeuvre").addSubProcess(updateProcess).build();
		serverManager.push(pageProcess);
		//System.out.println("Modification " + dateVisite);
		}*/

	/**
	 * Calcul la prochaine valeur aléatoire gaussienne entre X +/- 20% + X*(coef-1).
	 * 
	 * @param value
	 *            valeur moyenne
	 * @param coef
	 *            Coefficient
	 * @return prochaine valeur aléatoire suivant une gaussienne
	 */
	private long random(final double value, final double coef) {
		final long result = Math.round(nextGaussian(value, Math.round(value * 1.20)) + coef * value);
		//System.out.println("random(" + value + ", " + coef + ") = " + result);
		return result;
	}

	private static final Random RANDOM = new Random();

	private static long nextGaussian(final double moyenne, final double maxValue) {
		Assertion.checkArgument(moyenne >= 1, "La moyenne doit être supérieure ou égale à 1");
		Assertion.checkArgument(maxValue > moyenne, "La valeur max doit être supérieure à la moyenne");
		long result = Math.round(RANDOM.nextGaussian() * maxValue / 5d + moyenne);
		if (result < 0 || result > maxValue) {
			result = nextGaussian(moyenne, maxValue);
		}
		//System.out.println("nextGaussian(" + moyenne + ", " + maxValue + ") = " + result);

		return result;
	}

}

//
//	private void addLoginProcesses(final int offSetSeconds, final double processDuration, final Date dateVisite) {
//		final KProcess sqlProcess1 = new KProcessBuilder(dateVisite, 10, SQL_PROCESS, "select*from user").build();
//		final KProcess loginProcess = new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "/Accueil/login.jsf").addSubProcess(sqlProcess1).build();
//		serverManager.push(loginProcess);
//		final HQuery query = serverManager.createQueryBuilder() //
//				.on(HTimeDimension.Minute)//
//				.from(new Date(System.currentTimeMillis() - 60 * 120 * 1000))// 10 min ==> 10 cubes
//				.to(new Date()) //
//				.with("PAGE").build();
//		final HResult result = serverManager.execute(query);
//
//		System.out.println(serverManager.execute(query));
//
//	}
//
//	private void addContributorProcess(final int offSetSeconds, final int processDuration) {
//		final Date dateVisite = new Date(System.currentTimeMillis() - 60 * offSetSeconds * 1000);
//	}

