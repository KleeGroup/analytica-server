package application.museum;

import java.util.Date;

import kasper.kernel.lang.DateBuilder;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;

/**
 * @author statchum
 */
public final class Museum {
	private static final int NB_VISIT_DAILY_MAX = 10;

	private final PageListener pageListener;

	public Museum(final PageListener pageListener) {
		Assertion.notNull(pageListener);
		// ---------------------------------------------------------------------
		this.pageListener = pageListener;
	}

	public void load(int days) {
		Assertion.precondition(days >= 0, "days must be >= 0");
		//---------------------------------------------------------------------	
		//Toutes les visites sur 3h, 100visites par heures
		final Date now = new Date();
		for (int d = 0; d < days; d++) {
			final Date startDate = new DateBuilder(now).addDays(-d).toDateTime();
			loadVisitors(startDate, 50);
		}
	}

	private void loadVisitors(final Date startDate, final double visitorByHour) {
		final Date date = startDate;
		for (int h = 7; h < 19; h++) {
			final long nbVisit = StatsUtil.random(Math.round(NB_VISIT_DAILY_MAX * 0.3d), Pages.getCoef(h) * 2); // de 30% à 60% en fonction de l'heure
			for (int visit = 0; visit < nbVisit; visit++) {
				final Date dateVisit = new Date(date.getTime() + h * 60 * 60 * 1000 + visit * 60 * 60 * 1000L / nbVisit);
				addVisitorScenario(dateVisit);
			}
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
