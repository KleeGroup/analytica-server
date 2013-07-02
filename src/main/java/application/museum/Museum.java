package application.museum;

import java.util.Date;

import kasper.kernel.lang.DateBuilder;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;

/**
 * @author statchum
 */
public final class Museum {
	private final PageListener pageListener;

	public Museum(final PageListener pageListener) {
		Assertion.notNull(pageListener);
		// ---------------------------------------------------------------------
		this.pageListener = pageListener;
	}

	public void load(int days, final int visitsByDay) {
		Assertion.precondition(days >= 0, "days must be >= 0");
		//---------------------------------------------------------------------	
		//Toutes les visites sur 3h, 100visites par heures
		final Date now = new Date();
		System.out.println("=============");
		System.out.println("=====days :" + days);
		System.out.println("=====visitsByDay :" + visitsByDay);
		System.out.println("=====7h-->19h");
		System.out.println("=============");
		long start = System.currentTimeMillis();
		for (int d = 0; d < days; d++) {
			loadVisitors(now, d, visitsByDay);
			System.out.print(".");
		}
		System.out.println();
		System.out.println("data loaded in " + ((System.currentTimeMillis() - start) / 1000) + "seconds");
		System.out.println("=============");
	}

	private void loadVisitors(final Date date, final int dayBefore, final double visitsByDay) {
		final Date startDate = new DateBuilder(date).addDays(-dayBefore).build();
		int visit = 0;
		while (visit++ < visitsByDay) {
			//sur 12 heures donc de 7h à 19h
			int seconds = Double.valueOf(12 * 3600 * Math.random()).intValue();
			Date startVisit = new DateBuilder(startDate).addHours(7/*on commence à 7h du matin*/).addSeconds(seconds).toDateTime();
			addVisitorScenario(startVisit);
		}
	}

	private void addVisitorScenario(final Date startVisit) {
		//System.out.println("scenario [" + startVisit.getDay() + ", " + startVisit.getHours() + "] >>" + startVisit);
		//On ne CODE pas un scenario, on le déclare.
		addPages(startVisit, //
				Pages.HOME,// 
				Pages.ARTIST_SEARCH,// 
				Pages.ARTIST,// 
				Pages.ARTIST,// 
				Pages.ARTIST,// 
				Pages.ARTIST,// 
				Pages.ARTIST_SEARCH,// 
				Pages.ARTIST,// 
				Pages.ARTIST,// 
				Pages.ARTIST,// 
				Pages.ARTIST//
		);
	}

	private void addPages(final Date startVisit, PageBuilder... pageBuilders) {
		Date startDate = startVisit;
		for (PageBuilder pageBuilder : pageBuilders) {
			startDate = addPage(pageBuilder, startDate);
		}
	}

	private Date addPage(PageBuilder pageBuilder, Date startDate) {
		KProcess homePage = pageBuilder.createPage(startDate);
		//On notifie le listener
		pageListener.onPage(homePage);
		return addWaitTime(startDate);
	}

	private static Date addWaitTime(Date startVisit) {
		final long waitTime = 30 * 1000;//30s 
		return new Date(startVisit.getTime() + waitTime);
	}
}
