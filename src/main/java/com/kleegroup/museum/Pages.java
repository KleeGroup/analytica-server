package com.kleegroup.museum;

import java.util.Date;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;

final class Pages {
	public final static PageBuilder HOME = new HomePage();
	public final static PageBuilder SEARCH = new SearchPage();
	public final static PageBuilder ARTIST = new ArtistPage();

	private static final String PAGE_PROCESS = "PAGE";
	private static final String SQL_PROCESS = "SQL";
	private static final String SEARCH_PROCESS = "SEARCH";

	private static class HomePage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final double processDuration = StatsUtil.random(150, getCoef(dateVisite.getHours()));
			final KProcess sqlProcess = new KProcessBuilder(dateVisite, 80, SQL_PROCESS, "select*from news").build();
			return new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "home", "homePage").addSubProcess(sqlProcess).build();
		}
	}

	private static class SearchPage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final double processDuration = StatsUtil.random(750, getCoef(dateVisite.getHours()));
			final KProcess searchProcess = new KProcessBuilder(dateVisite, 80, SEARCH_PROCESS, "find oeuvres").build();
			return new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "search").addSubProcess(searchProcess).build();
		}
	}

	private static class ArtistPage implements PageBuilder {
		private static final String[] artists = "davinci;monet;bazille;bonnard;signac;hopper;picasso;munch;renoir;cézanne;rubens;bacon;johnes;rothko;warhol".split(";");

		public KProcess createPage(final Date dateVisite) {
			final double processDuration = StatsUtil.random(150, getCoef(dateVisite.getHours()));
			final String artist = getArtist();
			final KProcess searchProcess = new KProcessBuilder(dateVisite, 80, SQL_PROCESS, "select 1 from oeuvres").build();
			return new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, artist).addSubProcess(searchProcess).build();
		}

		private static String getArtist() {
			int r = Double.valueOf(Math.random() * artists.length).intValue();
			return artists[r];
		}
	}

	static double getCoef(int h) {
		return 0.25 + 0.25 * Math.sin((h - 7 + 4.5) * Math.PI / 3); //varie de 0 à 0.5
	}
}
