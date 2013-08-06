package com.kleegroup.analytica.museum;

import java.util.Date;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;

final class Pages {
	public final static PageBuilder HOME = new HomePage();
	public final static PageBuilder ARTIST_SEARCH = new SearchPage();
	public final static PageBuilder ARTIST = new ArtistPage();

	private static final String PAGE_PROCESS = "PAGE";
	private static final String SQL_PROCESS = "SQL";
	private static final String SEARCH_PROCESS = "SEARCH";

	private static class HomePage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final double processDuration = StatsUtil.random(150, getCoef(dateVisite.getHours()));

			return new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "home")//
					.beginSubProcess(dateVisite, 80, SQL_PROCESS, "select*from news").endSubProcess()//
					.build();
		}
	}

	private static class SearchPage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final double processDuration = StatsUtil.random(750, getCoef(dateVisite.getHours()));

			return new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, "search", "artists")//
					.beginSubProcess(dateVisite, 80, SEARCH_PROCESS, "find artists").endSubProcess()//
					.build();
		}
	}

	private static class ArtistPage implements PageBuilder {
		//On joue sur plusieurs listes de façon à ne pas avoir une équirépartition des données.
		private static final String[] artistsA = "vinci;monet;picasso;renoir;rubens".split(";");
		private static final String[] artistsB = "bazille;bonnard;munch;signac;hopper;cézanne;bacon;johnes;rothko;warhol".split(";");

		public KProcess createPage(final Date dateVisite) {
			final double processDuration = StatsUtil.random(150, getCoef(dateVisite.getHours()));
			final String artist = getArtist();
			return new KProcessBuilder(dateVisite, processDuration, PAGE_PROCESS, artist)//
					.beginSubProcess(dateVisite, 80, SQL_PROCESS, "select 1 from oeuvres").endSubProcess()//
					.build();
		}

		private static String getArtist() {
			String[] artists = Math.random() > 0.3 ? artistsA : artistsB;
			int r = Double.valueOf(Math.random() * artists.length).intValue();
			return artists[r];
		}
	}

	static double getCoef(int h) {
		return 0.25 + 0.25 * Math.sin((h - 7 + 4.5) * Math.PI / 3); //varie de 0 à 0.5
	}
}
