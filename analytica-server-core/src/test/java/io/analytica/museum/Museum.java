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
package io.analytica.museum;

import io.analytica.api.KProcess;
import io.analytica.api.KProcessBuilder;
import io.analytica.hcube.HCubeManager;
import io.vertigo.kernel.Home;
import io.vertigo.kernel.lang.Assertion;
import io.vertigo.kernel.lang.DateBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author statchum
 */
public final class Museum {
	public static final String APP_NAME = "MUSEUM";
	private static final String QOS = "QOS";
	private static final String HEALTH = "HEALTH";
	private final PageListener pageListener;

	private long pages = 0;

	public Museum(final PageListener pageListener) {
		Assertion.checkNotNull(pageListener);
		// ---------------------------------------------------------------------
		this.pageListener = pageListener;
	}

	public void load(final int days, final int visitsByDay) {
		Assertion.checkArgument(days >= 0, "days must be >= 0");
		//---------------------------------------------------------------------	
		//Toutes les visites sur 3h, 100visites par heures
		final Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		final Date startDate = new DateBuilder(today.getTime()).addDays(2).build();
		System.out.println("=============");
		System.out.println("=====days :" + days);
		System.out.println("=====visitsByDay :" + visitsByDay);
		System.out.println("=============");

		final long start = System.currentTimeMillis();
		for (int day = 0; day < days; day++) {
			final Date visitDate = new DateBuilder(startDate).addDays(-day).build();
			final Calendar calendar = new GregorianCalendar();
			calendar.setTime(visitDate);
			loadVisitors(visitDate, StatsUtil.random(visitsByDay, Activity.getCoefPerDay(calendar.get(Calendar.DAY_OF_WEEK))));
			checkMemory(day);

		}
		System.out.println();
		System.out.println("data loaded in " + (System.currentTimeMillis() - start) / 1000 + "seconds");
		System.out.println("=============");
	}

	private void checkMemory(final long day) {
		final HCubeManager cubeManager = Home.getComponentSpace().resolve(HCubeManager.class);
		if ((Runtime.getRuntime().totalMemory() / Runtime.getRuntime().maxMemory()) > 0.9) {
			cubeManager.size(APP_NAME);
			System.gc();
			//---
			if ((Runtime.getRuntime().totalMemory() / Runtime.getRuntime().maxMemory()) > 0.9) {
				System.out.println();
				System.out.println(">>>> total mem =" + Runtime.getRuntime().totalMemory());
				System.out.println(">>>> max   mem =" + Runtime.getRuntime().maxMemory());
				System.out.println(">>>> mem total > 90% - days =" + day);
				System.out.println(">>>> cubes count =" + cubeManager.size(APP_NAME));
				System.out.println(">>>> pages       =" + pages);

				System.out.println(">>>> cube footprint =" + (Runtime.getRuntime().maxMemory() / cubeManager.size(APP_NAME)) + " octets");
				try {
					Thread.sleep(1000 * 20); //20s
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		}
	}

	private void loadVisitors(final Date startDate, final double visitsByDay) {
		System.out.println("\n===== add " + visitsByDay + " at " + startDate);
		double visitRatioSum = 0;
		final double[] visitRatioPerHour = new double[24];
		for (int h = 0; h < 24; h++) {
			visitRatioPerHour[h] = Math.round(StatsUtil.random(visitsByDay, Activity.getCoefPerHour(h)));
			visitRatioSum += visitRatioPerHour[h];
		}
		final double visitPerHourRatio = visitsByDay / visitRatioSum;

		for (int h = 0; h < 24; h++) {
			final int nbHourVisit = (int) Math.round(visitRatioPerHour[h] * visitPerHourRatio);
			System.out.print(h + "h:" + nbHourVisit + ", ");
			final Date dateHour = new DateBuilder(startDate).addHours(h).toDateTime();
			for (int i = 0; i < nbHourVisit; i++) {
				final int defaultMinute = (int) Math.round((i + 1) * 60d / (nbHourVisit + 1));
				final int randomMinute = (int) (defaultMinute + (4 * 5 - StatsUtil.random(4 * 5, 1))); //default +/- 5 minutes (on joue sur le battement de 20%)
				final Date startVisit = new DateBuilder(dateHour).addMinutes(randomMinute).toDateTime();
				addVisitorScenario(startVisit);
			}
			loadHealthInfos(dateHour, nbHourVisit);
			loadQOS(dateHour, nbHourVisit);

		}
	}

	private void addVisitorScenario(final Date startVisit) {
		//System.out.println("scenario [" + startVisit.getDay() + ", " + startVisit.getHours() + "] >>" + startVisit);
		//On ne CODE pas un scenario, on le déclare.
		final KProcess visiteur = new KProcessBuilder(APP_NAME, startVisit, 0, "SESSION")//
				.setMeasure("SESSION HTTP", 1) //1 session
				.build();
		//On notifie le listener
		pageListener.onPage(visiteur);

		addPages(startVisit, //
				Pages.HOME,// 
				Pages.ARTIST_SEARCH,// 
				Pages.ARTIST,// 
				Pages.IMAGE_ARTIST,// 
				Pages.ARTIST,// 
				Pages.IMAGE_ARTIST,// 
				Pages.ARTIST,//
				Pages.IMAGE_ARTIST,// 
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,// 
				Pages.ARTIST,//
				Pages.IMAGE_ARTIST,// 
				Pages.EXPOSITION,//
				Pages.EXPOSITION,//
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,// 
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,// 
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,// 
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,// 
				Pages.ARTIST_SEARCH,// 
				Pages.ARTIST,// 
				Pages.IMAGE_ARTIST,// 
				Pages.ARTIST,//
				Pages.IMAGE_ARTIST,//  
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,//  
				Pages.ARTIST,//
				Pages.IMAGE_ARTIST,//  
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,// 
				Pages.OEUVRE_SEARCH,// 
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,//  
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,//  
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,//  
				Pages.OEUVRE,//
				Pages.IMAGE_OEUVRE,// 
				Pages.EXPOSITION,// 
				Pages.ARTIST,//
				Pages.IMAGE_ARTIST// 
		);
	}

	private void addPages(final Date startVisit, final PageBuilder... pageBuilders) {
		Date startDate = startVisit;
		int pagesVue = 0;
		final int pagesAVoir = (int) StatsUtil.random(pageBuilders.length, 1); //en moyen on voie le max mais 20% voient moins
		for (final PageBuilder pageBuilder : pageBuilders) {
			startDate = addPage(pageBuilder, startDate);
			if (++pagesVue > pagesAVoir) {
				break;
			}
		}
	}

	private Date addPage(final PageBuilder pageBuilder, final Date startDate) {
		final KProcess page = pageBuilder.createPage(startDate);
		//On notifie le listener
		pageListener.onPage(page);
		pages++;
		return addWaitTime(startDate, pageBuilder);
	}

	private static Date addWaitTime(final Date startVisit, final PageBuilder pageBuilder) {
		final long waitTime;
		if (pageBuilder == Pages.IMAGE_ARTIST || pageBuilder == Pages.IMAGE_OEUVRE) {
			waitTime = 100;
		} else {
			waitTime = 30 * 1000;//30s
		}
		return new Date(startVisit.getTime() + StatsUtil.random(waitTime, 1));
	}

	private void loadQOS(final Date dateHour, final double nbVisitsHour) {
		final double activity = Math.min(100, StatsUtil.random(100, nbVisitsHour / 50));
		final double perfs = Math.min(100, StatsUtil.random(100, 1.4 - nbVisitsHour / 50));
		final double health = Math.min(100, StatsUtil.random(100, 1.5 - nbVisitsHour / 50));

		final KProcess qosProcess = new KProcessBuilder(APP_NAME, dateHour, 0, QOS)//
				.setMeasure("Activity", activity)//
				.setMeasure("ActivityMax", 100)//
				.setMeasure("Performance", perfs)//
				.setMeasure("PerformanceMax", 100)//
				.setMeasure("Health", health)//
				.setMeasure("HealthMax", 100)//
				.build();
		pageListener.onPage(qosProcess);
	}

	private void loadHealthInfos(final Date dateHour, final double nbVisitsHour) {
		for (int min = 0; min < 60; min += 6) {
			final Date dateMinute = new DateBuilder(dateHour).addMinutes(min).toDateTime();
			final KProcess healthProcess = new KProcessBuilder(APP_NAME, dateMinute, 0, HEALTH, "physical")//
					.setMeasure("CPU", Math.min(100, 5 + (nbVisitsHour > 0 ? StatsUtil.random(nbVisitsHour, 1) : 0)))//
					.setMeasure("RAM", Math.min(3096, 250 + (nbVisitsHour > 0 ? StatsUtil.random(nbVisitsHour, 10) : 0)))//
					.setMeasure("IO", 10 + (nbVisitsHour > 0 ? StatsUtil.random(nbVisitsHour, 5) : 0))//
					.build();
			pageListener.onPage(healthProcess);
		}
	}
}
