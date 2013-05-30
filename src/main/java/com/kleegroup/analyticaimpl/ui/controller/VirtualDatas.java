/**
 * 
 */
package com.kleegroup.analyticaimpl.ui.controller;

import java.util.Date;
import java.util.Random;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;
import com.kleegroup.analytica.server.ServerManager;

/**
 * @author statchum
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public class VirtualDatas {
	private final ServerManager serverManager;

	private static final String PAGE_PROCESS = "PAGE";
	private static final String SQL_PROCESS = "SQL";
	private static final String SEARCH_PROCESS = "SEARCH";

	private static final int FIRST_WEEK = 0;

	private static final int NB_WEEK = 1;

	private static final double NB_VISIT_DAILY_MAX = 10;

	public VirtualDatas(final ServerManager serverManager) {
		this.serverManager = serverManager;
	}

	public void load() {
		//Toutes les visites sur 3h, 100visites par heures
		loadVisitors(new Date(System.currentTimeMillis()), 50);
	}

	//	public void loadVisitors(final Date startDate, final double hours, final double visitorByHour) {
	// visitByHour represente finalement le nombre max de visites
	public void loadVisitors(final Date startDate, final double visitorByHour) {

		for (int j = 0; j < 5; j++) {
			final Date dateJour = new Date(startDate.getTime() + j * 24 * 60 * 60 * 1000L);
			for (int h = 0; h < 12; h++) {
				final double coef = 1.5 + 0.25 * Math.sin(j * Math.PI / 2 / (6 * 30));
				final long nbVisit = random(NB_VISIT_DAILY_MAX * Math.cos((h - 3) / 2d / 3.14d), coef); // 50 visites en moyenne par heure en pic
				for (int visit = 0; visit < nbVisit; visit++) {
					final Date dateVisite = new Date(dateJour.getTime() + h * 60 * 60 * 1000L + random(visit * (50 * 60 * 1000L / nbVisit) + 5 * 60 * 1000L, 1));
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

	private void addVisitorScenario(final Date startVisite, final double coef) {
		final long waitTime = 30 * 1000;

		addHomePage(startVisite, coef);

		addSearchPage(new Date(startVisite.getTime() + waitTime), coef);

		for (int i = 0; i < 3; i++) {
			addViewPage(new Date(startVisite.getTime() + waitTime + waitTime * i), coef);
		}
	}

	private void addHomePage(final Date dateVisite, final double processDuration) {
		//		final double processDuration = Math.random() * 50 + 150d;
		//	final double processDuration = 150d + 100 * Math.sin(dateVisite.getMinutes() * Math.PI / 60);

		final KProcess sqlProcess = new KProcessBuilder(dateVisite, 80, SQL_PROCESS, "select*from news").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "/home").addSubProcess(sqlProcess).build();
		serverManager.push(pageProcess);
	}

	private void addSearchPage(final Date dateVisite, final double processDuration) {
		//final double processDuration = Math.random() * 50 + 150d;
		//	final double processDuration = 150d + 100 * Math.sin(dateVisite.getMinutes() * Math.PI / 60);

		final KProcess searchProcess = new KProcessBuilder(dateVisite, 80, SEARCH_PROCESS, "find oeuvres").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "/search").addSubProcess(searchProcess).build();
		serverManager.push(pageProcess);
		System.out.println("Recherche " + dateVisite);

	}

	private void addViewPage(final Date dateVisite, final double processDuration) {
		//final double processDuration = Math.random() * 50 + 150d;
		//final double processDuration = 150d + 100 * Math.sin(dateVisite.getMinutes() * Math.PI / 60);

		final KProcess searchProcess = new KProcessBuilder(dateVisite, 80, SQL_PROCESS, "select 1 from oeuvres").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "/oeuvre").addSubProcess(searchProcess).build();
		serverManager.push(pageProcess);
		System.out.println("Consultation " + dateVisite);

	}

	private void addUpdatePage(final Date dateVisite, final double processDuration) {
		//final double processDuration = Math.random() * 50 + 150d;

		final KProcess updateProcess = new KProcessBuilder(dateVisite, 80, SQL_PROCESS, "update 1 from oeuvres").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "/oeuvre").addSubProcess(updateProcess).build();
		serverManager.push(pageProcess);
		System.out.println("Modification " + dateVisite);

	}

	/**
	 * Calcul la prochaine valeur aléatoire gaussienne entre X +/- 20% + X *
	 * (coef-1).
	 * 
	 * @param value
	 *            valeur moyenne
	 * @param coef
	 *            Coefficient
	 * @return prochaine valeur aléatoire suivant une gaussienne
	 */
	private long random(final double value, final double coef) {
		return Math.round(nextGaussian(value, Math.round(value * 1.20)) + (coef - 1) * value);
	}

	private static final Random RANDOM = new Random();

	private static long nextGaussian(final double moyenne, final double maxValue) {
		Assertion.precondition(moyenne >= 1, "La moyenne doit être supérieure ou égale à 1");
		Assertion.precondition(maxValue > moyenne, "La valeur max doit être supérieure à la moyenne");
		long result = Math.round(RANDOM.nextGaussian() * maxValue / 3d + moyenne);
		if (result < 0 || result > maxValue) {
			result = nextGaussian(moyenne, maxValue);
		}
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

