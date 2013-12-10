package com.kleegroup.analytica.museum;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import vertigo.kernel.lang.Assertion;
import vertigo.kernel.lang.DateBuilder;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;

/**
 * @author statchum
 */
public final class Museum {
	private static final String QOS = "QOS";
	private static final String HEALTH = "HEALTH";
	private final PageListener pageListener;

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
		for (int d = 0; d < days; d++) {
			final Date visitDate = new DateBuilder(startDate).addDays(-d).build();
			final Calendar calendar = new GregorianCalendar();
			calendar.setTime(visitDate);
			loadVisitors(visitDate, StatsUtil.random(visitsByDay, getCoefPerDay(calendar.get(Calendar.DAY_OF_WEEK))));
		}
		System.out.println();
		System.out.println("data loaded in " + (System.currentTimeMillis() - start) / 1000 + "seconds");
		System.out.println("=============");
	}

	private double getCoefPerDay(final int dayOfWeek) {
		switch (dayOfWeek) {
			case Calendar.MONDAY:
				return 0.6;
			case Calendar.TUESDAY:
				return 0.8;
			case Calendar.WEDNESDAY:
				return 1;
			case Calendar.THURSDAY:
				return 0.9;
			case Calendar.FRIDAY:
				return 0.8;
			case Calendar.SATURDAY:
				return 1.6;
			case Calendar.SUNDAY:
				return 1.4;
		}
		return 1;
	}

	private double getCoefPerHour(final int hourOfDay) {
		if (hourOfDay <= 5) {
			return 0.01;
		} else if (hourOfDay <= 7) {
			return 0.1;
		} else if (hourOfDay <= 12) {
			return 0.5 + 0.5 * ((hourOfDay - 7) / 5d);
		} else if (hourOfDay <= 16) {
			return 0.9 - 0.4 * ((hourOfDay - 12) / 4d);
		} else if (hourOfDay <= 20) {
			return 0.5 + 0.3 * ((hourOfDay - 16) / 4d);
		} else {
			return 0.7 - 0.65 * ((hourOfDay - 20) / 3d);
		}
	}

	private void loadVisitors(final Date startDate, final double visitsByDay) {
		System.out.println("\n===== add " + visitsByDay + " at " + startDate);
		double visitRatioSum = 0;
		final double[] visitRatioPerHour = new double[24];
		for (int h = 0; h < 24; h++) {
			visitRatioPerHour[h] = Math.round(StatsUtil.random(visitsByDay, getCoefPerHour(h)));
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
		final KProcess visiteur = new KProcessBuilder(startVisit, 0, "SESSION")//
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
		final KProcessBuilder healthProcess = new KProcessBuilder(dateHour, 0, QOS);
		final double activity = Math.min(100, StatsUtil.random(100, nbVisitsHour / 50));
		final double perfs = Math.min(100, StatsUtil.random(100, 1.4 - nbVisitsHour / 50));
		final double health = Math.min(100, StatsUtil.random(100, 1.5 - nbVisitsHour / 50));
		healthProcess.setMeasure("Activity", activity);
		healthProcess.setMeasure("ActivityMax", 100);
		healthProcess.setMeasure("Performance", perfs);
		healthProcess.setMeasure("PerformanceMax", 100);
		healthProcess.setMeasure("Health", health);
		healthProcess.setMeasure("HealthMax", 100);
		pageListener.onPage(healthProcess.build());
	}

	private void loadHealthInfos(final Date dateHour, final double nbVisitsHour) {
		for (int m = 0; m < 60; m += 6) {
			final Date dateMinute = new DateBuilder(dateHour).addMinutes(m).toDateTime();
			final KProcessBuilder healthProcess = new KProcessBuilder(dateMinute, 0, HEALTH, "physical");
			healthProcess.setMeasure("CPU", Math.min(100, 5 + (nbVisitsHour > 0 ? StatsUtil.random(nbVisitsHour, 1) : 0)));
			healthProcess.setMeasure("RAM", Math.min(3096, 250 + (nbVisitsHour > 0 ? StatsUtil.random(nbVisitsHour, 10) : 0)));
			healthProcess.setMeasure("IO", 10 + (nbVisitsHour > 0 ? StatsUtil.random(nbVisitsHour, 5) : 0));
			pageListener.onPage(healthProcess.build());
		}
	}
}
