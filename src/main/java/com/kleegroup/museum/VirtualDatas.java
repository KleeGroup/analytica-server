package com.kleegroup.museum;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;
import com.kleegroup.analytica.server.ServerManager;

/**
 * 
 * @author statchum
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public final class VirtualDatas {
	private static final String PAGE_PROCESS = "PAGE";
	private static final String SQL_PROCESS = "SQL";
	private static final String SEARCH_PROCESS = "SEARCH";
	//	private static final String OEUVRES_PROCESS = "OEUVRE";
	private final String[] artists = "davinci;monet;bazille;bonnard;signac;hopper;picasso;munch;renoir;cézanne;rubens;bacon;johnes;rothko;warhol".split(";");

	private static final int NB_VISIT_DAILY_MAX = 10;

	private final ServerManager serverManager;

	public VirtualDatas(final ServerManager serverManager) {
		Assertion.notNull(serverManager);
		// ---------------------------------------------------------------------
		this.serverManager = serverManager;
	}

	public void load() {
		//Toutes les visites sur 3h, 100visites par heures

		//Start date = 8h
		//Date startDate = new Date();
		final Calendar startDate = GregorianCalendar.getInstance();
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		System.out.println("StartDate : " + startDate);
		loadVisitors(startDate.getTime(), 50);
	}

	public void loadVisitors(final Date startDate, final double visitorByHour) {
		final Date date = startDate;
		for (int h = 7; h < 19; h++) {
			final double coef = 0.25 + 0.25 * Math.sin((h - 7 + 4.5) * Math.PI / 3); //varie de 0 à 0.5
			final long nbVisit = StatsUtil.random(Math.round(NB_VISIT_DAILY_MAX * 0.3d), coef * 2); // de 30% à 60% en fonction de l'heure
			for (int visit = 0; visit < nbVisit; visit++) {
				final Date dateVisit = new Date(date.getTime() + h * 60 * 60 * 1000 + visit * 60 * 60 * 1000L / nbVisit);
				addVisitorScenario(dateVisit, coef);
				//addContriutorScenario(dateVisite, coef);
			}
		}
	}

	//	}

	private void addVisitorScenario(final Date startVisite, final double coef) {
		final long waitTime = 30 * 1000;//30s 

		addHomePage(startVisite, StatsUtil.random(150, coef));
		addSearchPage(new Date(startVisite.getTime() + waitTime), StatsUtil.random(750, coef));
		int artistViewed = 0;
		for (int i = 0; i < 3; i++) {
			for (final String artist : artists) {
				addArtistPage(artist, new Date(startVisite.getTime() + waitTime * artistViewed++), StatsUtil.random(150, coef));
			}
		}
	}

	/*	private void addContriutorScenario(final Date startVisite, final double coef) {
			final long waitTime = 30 * 1000;

			addHomePage(startVisite, coef);
			addSearchPage(new Date(startVisite.getTime() + waitTime), coef);
			for (int i = 0; i < 10; i++) {
				addSearchPage(new Date(startVisite.getTime() + waitTime), coef);
				addUpdatePage(new Date(startVisite.getTime() + waitTime + waitTime * i), coef);
			}

		}*/

	private void addHomePage(final Date dateVisite, final double processDuration) {
		final KProcess sqlProcess = new KProcessBuilder(dateVisite, 80, SQL_PROCESS, "select*from news").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "home", "homePage").addSubProcess(sqlProcess).build();
		serverManager.push(pageProcess);
	}

	private void addSearchPage(final Date dateVisite, final double processDuration) {
		final KProcess searchProcess = new KProcessBuilder(dateVisite, 80, SEARCH_PROCESS, "find oeuvres").build();
		final KProcess pageProcess = new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "search").addSubProcess(searchProcess).build();
		serverManager.push(pageProcess);
	}

	private void addArtistPage(final String artistName, final Date dateVisite, final double processDuration) {
		final KProcess searchProcess = new KProcessBuilder(dateVisite, 80, SQL_PROCESS, "select 1 from oeuvres").build();
		final KProcess artistpageProcess = new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, artistName).addSubProcess(searchProcess).build();
		serverManager.push(artistpageProcess);
	}

}
