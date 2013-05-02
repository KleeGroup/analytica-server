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
package com.kleegroup.analyticaimpl.server.plugins.cubestore.h2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Outil de pour simplifier l'ananlyse des temps de traitement.
 * 
 * On marque un point de départ avec TIC, et un point d'arret avec TAC.
 * Fournit des rapports de traitement, lors des appels à reportXXXX.
 * 
 * @version $Id: TicTac.java,v 1.2 2012/11/26 16:11:07 npiedeloup Exp $
 * @author npiedeloup $
 */
final class TicTac {
	private static final String LOG_SEP = "##########################################################################################";
	private final Logger LOG = Logger.getLogger("TicTac");

	private final String timerName;

	private final List<String> listOrderedNames = new ArrayList<String>();
	private final Map<String, Long> interMsMap = new HashMap<String, Long>();
	private final Map<String, Long> maxMap = new HashMap<String, Long>();
	private final Map<String, Long> minMap = new HashMap<String, Long>();
	private final Map<String, Long> nbTicMap = new HashMap<String, Long>();
	private final Map<String, Long> dernierMsMap = new HashMap<String, Long>();
	private final Map<String, Long> totalMap = new HashMap<String, Long>();
	private long totalEnglobantMs; // = 0;
	private long nbTicEnglobant; // = 0;
	private long dernierEnglobantMs; // = 0;
	private String ticTacEnglobantName; // = null;
	private boolean ligneIsDraw; // = false;
	private long reportCallCount = 0;

	/**
	 * Construit un timer.
	 * @param timerName Nom du timer (module, type de timer, ...).
	 */
	public TicTac(final String timerName) {
		this.timerName = timerName;
	}

	/**
	 * Démarre le timer.
	 * @param indicateurName nom de l'indicateur (requete XXX, chargement YYY, traitement ZZZ, ...)
	 */
	public void tic(final String indicateurName) {
		final long nbTic = nbTicMap.containsKey(indicateurName) ? nbTicMap.get(indicateurName).longValue() : 0;
		nbTicMap.put(indicateurName, new Long(nbTic + 1));
		if (ticTacEnglobantName == null) {
			ticTacEnglobantName = indicateurName;
			nbTicEnglobant++;
		}

		if (!listOrderedNames.contains(indicateurName)) {
			listOrderedNames.add(indicateurName);
		}
		interMsMap.put(indicateurName, new Long(System.currentTimeMillis()));
	}

	/**
	 * Termine le timer.
	 * @param indicateurName nom de l'indicateur (requete XXX, chargement YYY, traitement ZZZ, ...)
	 */
	public void tac(final String indicateurName) {
		final long nowMs = System.currentTimeMillis();
		final Long max = maxMap.get(indicateurName);
		final Long min = minMap.get(indicateurName);
		final long interMs = interMsMap.containsKey(indicateurName) ? interMsMap.get(indicateurName) : 0;
		final long totalMs = totalMap.containsKey(indicateurName) ? totalMap.get(indicateurName) : 0;
		if (max == null || max < nowMs - interMs) {
			maxMap.put(indicateurName, nowMs - interMs);
		}
		if (min == null || min > nowMs - interMs) {
			minMap.put(indicateurName, nowMs - interMs);
		}
		//LOG.info("### TICTAC ### - " + startName + " +" + (nowMs - interMs) + "ms - " + s);
		totalMap.put(indicateurName, totalMs + nowMs - interMs);
		dernierMsMap.put(indicateurName, nowMs - interMs);
		interMsMap.put(indicateurName, nowMs);
		if (ticTacEnglobantName != null && ticTacEnglobantName.equals(indicateurName)) {
			dernierEnglobantMs = nowMs - interMs;
			totalEnglobantMs += dernierEnglobantMs;
			ticTacEnglobantName = null;
		}

	}

	/**
	 * Affiche un rapport, tous les "everyX" appels.
	 * @param everyX Nombre d'appel de cette méthode entre deux rapports.
	 */
	public void reportAllEveryX(final int everyX) {
		reportCallCount++;
		if (reportCallCount % everyX == 0) {
			reportAll();
		}
	}

	/**
	 * Affiche dans le log le rapport complet.
	 */
	public void reportAll() {
		LOG.info(LOG_SEP);
		ligneIsDraw = true;
		for (final Object element : listOrderedNames) {
			report((String) element);
		}
		if (!ligneIsDraw) {
			LOG.info(LOG_SEP);
		}
		ligneIsDraw = false;
	}

	/**
	 * Affiche dans le log, le rapport pour le timer timerName.
	 * @param indicateurName Nom de l'indicateur
	 */
	public void report(final String indicateurName) {
		final long max = maxMap.containsKey(indicateurName) ? maxMap.get(indicateurName) : 0;
		final long min = minMap.containsKey(indicateurName) ? minMap.get(indicateurName) : Long.MAX_VALUE;
		//long interMs = interMsMap.containsKey(s) ? ((Long) interMsMap.get(s)) : 0;
		final long dernierMs = dernierMsMap.containsKey(indicateurName) ? dernierMsMap.get(indicateurName) : 0;
		final long nbTic = nbTicMap.containsKey(indicateurName) ? nbTicMap.get(indicateurName) : 1;
		long totalMs = totalMap.containsKey(indicateurName) ? totalMap.get(indicateurName) : 1;
		if (totalMs == 0) {
			totalMs = 1;
		}
		if (totalEnglobantMs == 0) {
			totalEnglobantMs = 1;
		}
		if (dernierEnglobantMs == 0) {
			dernierEnglobantMs = 1;
		}
		if (nbTicEnglobant == 0) {
			nbTicEnglobant = 1;
		}

		if (totalMs * 100 / totalEnglobantMs < 5) {
			LOG.info(String.format("### TICTAC ### - %s - %s - %sms en (%s appels) < 5%%", timerName, indicateurName, totalMs, nbTic));
			ligneIsDraw = false;
		} else {
			if (!ligneIsDraw) {
				LOG.info(LOG_SEP);
			}
			ligneIsDraw = true;
			LOG.info(String.format("### TICTAC ### - %s - %s - %sms en (%s appels) - %s%% du temps global", timerName, indicateurName, totalMs, nbTic, totalMs * 100 / totalEnglobantMs));
			//LOG.info("### TICTAC ### - " + startName + " - Temps total = " + (totalMs) + "ms - Nb appels = " + nbTic + ", soit "+ (totalMs) * 100 / (totalEnglobantMs) + "% du temp global) ");
			LOG.info(String.format("### TICTAC ### - %s - dernier = %sms (soit %s%%) - moyenne = %sms (soit %s%%) ", timerName, dernierMs, dernierMs * nbTic * 100 / (dernierEnglobantMs * nbTicEnglobant), totalMs / nbTic, totalMs * nbTicEnglobant * 100 / (nbTic * totalEnglobantMs)));
			LOG.info(String.format("### TICTAC ### - %s - Le plus long = %sms (soit %s%% de la moyenne) - Le plus court = %sms (soit %s%% de la moyenne) ", timerName, max, max * nbTic * 100 / totalMs, min, min * nbTic * 100 / totalMs));
			LOG.info(LOG_SEP);
		}

	}

}
