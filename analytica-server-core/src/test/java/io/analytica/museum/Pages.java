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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

final class Pages {
	public final static PageBuilder HOME = new HomePage();
	public final static PageBuilder ARTIST_SEARCH = new SearchArtistPage();
	public final static PageBuilder OEUVRE_SEARCH = new SearchOeuvrePage();
	public final static PageBuilder ARTIST = new ArtistPage();
	public final static PageBuilder OEUVRE = new OeuvrePage();
	public final static PageBuilder EXPOSITION = new ExpositionPage();
	public final static PageBuilder IMAGE_ARTIST = new ImageArtistPage();
	public final static PageBuilder IMAGE_OEUVRE = new ImageOeuvrePage();

	private static final String ERROR_MEASURE = "ERROR";
	private static final String PAGE_PROCESS = "PAGE";
	private static final String SERVICE_PROCESS = "SERVICE";
	private static final String SQL_PROCESS = "SQL";
	private static final String SEARCH_PROCESS = "SEARCH";

	private static class HomePage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final int[] randomDurations = StatsUtil.randoms(getCoef(dateVisite), 100, 5, 40, 5, 40, 5, 40, 5, 40);

			return new KProcessBuilder(dateVisite, StatsUtil.sum(randomDurations, 0, 1, 2, 3, 4, 5, 6, 7, 8), PAGE_PROCESS, "home")//
					.setMeasure(ERROR_MEASURE, StatsUtil.randomValue(1, 0.01, 100, 0))// 1% d'erreur
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 1, 2), SERVICE_PROCESS, "CommunicationServices", "loadNews")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 2), SQL_PROCESS, "select * from news").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 3, 4), SERVICE_PROCESS, "ExpositionServices", "loadPushExpositions")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 4), SQL_PROCESS, "select * from expositions where mise_en_avant = 1").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 5, 6), SERVICE_PROCESS, "OeuvreServices", "loadPushOeuvres")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 6), SQL_PROCESS, "select * from oeuvres where mise_en_avant = 1").endSubProcess()//
					.endSubProcess().beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 7, 8), SERVICE_PROCESS, "OeuvreServices", "loadFavoriesOeuvres")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 8), SQL_PROCESS, "select * from oeuvres join oeu_usr where usr_id = #usr_id#").endSubProcess()//
					.endSubProcess().build();
		}
	}

	private static class SearchArtistPage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final int[] randomDurations = StatsUtil.randoms(getCoef(dateVisite), 100, 50, 150, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10);

			return new KProcessBuilder(dateVisite, StatsUtil.sum(randomDurations, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20), PAGE_PROCESS, "search", "artists")//
					.setMeasure(ERROR_MEASURE, StatsUtil.randomValue(1, 0.01, 100, 0))// 1% d'erreur
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 1, 2), SERVICE_PROCESS, "ArtistServices", "search")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 2), SEARCH_PROCESS, "find artists").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 3, 4), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(100, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 4), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 5, 6), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(101, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 6), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 7, 8), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(102, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 8), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 9, 10), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(103, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 10), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 11, 12), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(104, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 12), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 13, 14), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(105, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 14), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 15, 16), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(106, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 16), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 17, 18), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(107, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 18), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 19, 20), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(108, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 20), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.build();
		}
	}

	private static class SearchOeuvrePage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final int[] randomDurations = StatsUtil.randoms(getCoef(dateVisite), 100, 50, 250, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10, 5, 10);

			return new KProcessBuilder(dateVisite, StatsUtil.sum(randomDurations, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20), PAGE_PROCESS, "search", "oeuvres")//
					.setMeasure(ERROR_MEASURE, StatsUtil.randomValue(1, 0.01, 100, 0))// 1% d'erreur
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 1, 2), SERVICE_PROCESS, "OeuvreServices", "search")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 2), SEARCH_PROCESS, "find oeuvres").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 3, 4), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(200, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 4), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 5, 6), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(201, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 6), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 7, 8), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(202, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 8), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 9, 10), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(203, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 10), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 11, 12), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(204, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 12), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 13, 14), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(205, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 14), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 15, 16), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(206, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 16), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 17, 18), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(207, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 18), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 19, 20), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(208, 1)))//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 20), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.endSubProcess()//
					.build();
		}
	}

	private static class ImageOeuvrePage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final int[] randomDurations = StatsUtil.randoms(getCoef(dateVisite), 5, 15);

			return new KProcessBuilder(dateVisite, StatsUtil.sum(randomDurations, 0, 1), PAGE_PROCESS, "images", "oeuvres", String.valueOf(StatsUtil.random(200, 1)))//
					.setMeasure(ERROR_MEASURE, StatsUtil.randomValue(1, 0.01, 100, 0))// 1% d'erreur
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 1), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.build();
		}
	}

	private static class ImageArtistPage implements PageBuilder {
		public KProcess createPage(final Date dateVisite) {
			final int[] randomDurations = StatsUtil.randoms(getCoef(dateVisite), 5, 10);

			return new KProcessBuilder(dateVisite, StatsUtil.sum(randomDurations, 0, 1), PAGE_PROCESS, "images", "artists", String.valueOf(StatsUtil.random(100, 1)))//
					.setMeasure(ERROR_MEASURE, StatsUtil.randomValue(1, 0.01, 100, 0))// 1% d'erreur
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 1), SQL_PROCESS, "select data from blob where id = #id#").endSubProcess()//
					.build();
		}
	}

	private static class ArtistPage implements PageBuilder {
		//On joue sur plusieurs listes de façon à ne pas avoir une équirépartition des données.
		private static final String[] artistsA = "vinci;monet;picasso;renoir;rubens".split(";");
		private static final String[] artistsB = "bazille;bonnard;munch;signac;hopper;cézanne;bacon;johnes;rothko;warhol".split(";");

		public KProcess createPage(final Date dateVisite) {
			final int[] randomDurations = StatsUtil.randoms(getCoef(dateVisite), 100, 5, 20, 5, 20);
			final String artist = getArtist();
			return new KProcessBuilder(dateVisite, StatsUtil.sum(randomDurations, 0, 1, 2, 3, 4), PAGE_PROCESS, "artists", artist)//
					.setMeasure(ERROR_MEASURE, StatsUtil.randomValue(1, 0.01, 100, 0))// 1% d'erreur
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 1, 2), SERVICE_PROCESS, "ArtistServices", "loadArtist")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 2), SQL_PROCESS, "select * from artists where art_id = #art_id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 3, 4), SERVICE_PROCESS, "OeuvreServices", "loadOeuvreByArtId")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 4), SQL_PROCESS, "select * from oeuvres join oeu_art where art_id = #art_id#").endSubProcess()//
					.endSubProcess().build();
		}

		private static String getArtist() {
			final String[] artists = Math.random() > 0.3 ? artistsA : artistsB;
			final int r = Double.valueOf(Math.random() * artists.length).intValue();
			return artists[r];
		}
	}

	private static class OeuvrePage implements PageBuilder {
		//On joue sur plusieurs listes de façon à ne pas avoir une équirépartition des données.
		private static final String[] oeuvresA = "Le Baptême du Christ;L'Annonciation;Ginevra de'Benci;La Madone à l'œillet;Madonna Benois;Saint Jérôme;L'Adoration des mages;La Vierge aux rochers;La Dame à l'hermine;Madonna Litta;Portrait de musicien;La Belle Ferronnière;La Cène;La Vierge aux rochers;Sala delle Asse;La Vierge, l'Enfant Jésus avec sainte Anne et saint Jean Baptiste;La Madone aux fuseaux;La Joconde ou Mona Lisa;Jeune fille décoiffée;La Vierge, l'Enfant Jésus et sainte Anne;Bacchus;Saint Jean Baptiste".split(";");
		private static final String[] oeuvresB = "Autoportrait 1901;La Célestine;Les Demoiselles d'Avignon;Dora Maar au chat;Garçon à la pipe;Guernica;Massacre en Corée;Les Noces de Pierrette;Maya à la poupée;Nu au plateau de sculpteur;Le Rêve;Le Vieux Guitariste aveugle".split(";");
		private static final String[] oeuvresC = "Achille Emperaire;Nature morte à la bouilloire;La Pendule noire;Pastorale ou l'Idylle;La Maison du pendu;Autoportrait;Madame Cézanne dans un fauteuil rouge;Pont de Maincy;Cour de ferme à Auvers;Pommes et biscuits;Plateau de la montagne Sainte Victoire;L'Estaque, vue du golfe de Marseille;Vase de fleurs et pommes;Les Collines de Meyreuil;Gardanne le soir, Vue de la colline des frères;Gardanne, Vue de Saint André;Les rideaux;Payannet et la Sainte-Victoire. Environs de Gardanne;L'aqueduc;Marronniers et ferme du Jas de Bouffon;Pont sur la Marne à Créteil;La table de cuisine (Nature morte au panier);Mardi-gras;Madame Cézanne sur une chaise jaune;Les Joueurs de cartes;Les baigneurs;Baigneurs;Femme à la cafetière;Le Garçon au gilet rouge;Les Grandes Baigneuses;Oignons et bouteille;Joachim Gasquet;Paysan à la blouse bleue;Pommes et oranges;Nature morte aux oignons;Fumeur accoudé;Oignons et bouteille;Le fumeur;Le rocher rouge;Le château noir;Montagne Sainte Victoire;Rocher de Bibemus;Vieille Femme au rosaire;La Montagne Sainte-Victoire et le Château Noir"
				.split(";");

		public KProcess createPage(final Date dateVisite) {
			final int[] randomDurations = StatsUtil.randoms(getCoef(dateVisite), 100, 5, 50, 5, 20);
			final String oeuvre = getOeuvre();
			return new KProcessBuilder(dateVisite, StatsUtil.sum(randomDurations, 0, 1, 2, 3, 4), PAGE_PROCESS, "oeuvres", oeuvre)//
					.setMeasure(ERROR_MEASURE, StatsUtil.randomValue(1, 0.01, 100, 0))// 1% d'erreur
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 1, 2), SERVICE_PROCESS, "OeuvreServices", "loadOeuvre")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 2), SQL_PROCESS, "select * from oeuvres where oeu_id = #oeu_id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 3, 4), SERVICE_PROCESS, "ExpositionServices", "loadExpositionByOeuId")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 4), SQL_PROCESS, "select * from expositions join exp_oeu where oeu_id = #oeu_id#").endSubProcess()//
					.endSubProcess().build();
		}

		private static String getOeuvre() {
			final String[] oeuvres = Math.random() < 0.5 ? oeuvresA : Math.random() < 0.7 ? oeuvresB : oeuvresC;
			final int r = Double.valueOf(Math.random() * oeuvres.length).intValue();
			return oeuvres[r];
		}
	}

	private static class ExpositionPage implements PageBuilder {
		private static final String[] museums = "Musée du Louvre,Paris;Musée d'Orsay,Paris;The Metropolitan Museum of Art,New York;Pushkin Museum,Moscow;Courtauld Institute Galleries,London".split(";");
		private static final String[] annees = "1954;1966;1970;1982;1991;2002;2005;2007;2008;2009;2010;2011;2011;2012;2012;2012;2013;2013;2013;2013".split(";");

		public KProcess createPage(final Date dateVisite) {
			final int[] randomDurations = StatsUtil.randoms(getCoef(dateVisite), 100, 5, 20, 5, 50, 5, 40);
			final String[] expositionInfos = getExposition().split(",");// [Musée,Ville,Année]

			return new KProcessBuilder(dateVisite, StatsUtil.sum(randomDurations, 0, 1, 2, 3, 4, 5, 6), PAGE_PROCESS, "exposition", expositionInfos[0], expositionInfos[1], expositionInfos[2])//
					.setMeasure(ERROR_MEASURE, StatsUtil.randomValue(1, 0.01, 100, 0))// 1% d'erreur
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 1, 2), SERVICE_PROCESS, "ExpositionServices", "loadExposition")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 2), SQL_PROCESS, "select * from expositions where exp_id = #exp_id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 3, 4), SERVICE_PROCESS, "OeuvreServices", "loadOeuvreByExpId")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 4), SQL_PROCESS, "select * from oeuvres join exp_oeu where exp_id = #exp_id#").endSubProcess()//
					.endSubProcess()//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 5, 6), SERVICE_PROCESS, "ArtistServices", "loadArtistByExpId")//
					.beginSubProcess(dateVisite, StatsUtil.sum(randomDurations, 6), SQL_PROCESS, "select * from artists join exp_art where exp_id = #exp_id#").endSubProcess()//
					.endSubProcess()//
					.build();
		}

		private static String getExposition() {
			final int m = Double.valueOf(Math.random() * museums.length).intValue();
			final String museum = museums[m];
			final int a = Double.valueOf(Math.random() * annees.length).intValue();
			final String annee = annees[a];
			return museum + "," + annee;
		}
	}

	static double getCoef(final Date date) {
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		final int h = calendar.get(Calendar.HOUR_OF_DAY);
		if (h <= 5 || h >= 21) {
			return 0.6;
		}
		return 1 + 0.5 * (0.35 * Math.sin((h - 7) * Math.PI / 4d) + 0.15 * Math.sin((h - 7 - 2 / 3d) * Math.PI / 8d)); //varie de 0.6 à 1.5 (entre 6h et 20h)
	}
}
