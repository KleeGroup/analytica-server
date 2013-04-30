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
package com.kleegroup.analytica.server.h2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import kasper.AbstractTestCaseJU4;
import kasper.kernel.util.Assertion;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.core.KProcessBuilder;
import com.kleegroup.analytica.server.ServerManager;

/**
 * Cas de Test JUNIT permettant de générer de nombreux KProcess fictifs.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: AnalyticaDataBuilderTest.java,v 1.11 2012/10/30 18:28:53
 *          pchretien Exp $
 */
public final class AnalyticaDataBuilderTest extends AbstractTestCaseJU4 {
	/** Logger. */
	private final Logger log = Logger.getLogger(getClass());
	private static final int FIRST_WEEK = 0;// 52 + 15 - 3 * 31 / 7 + 1;
	private static final int NB_WEEK = 52 * 2;// 2 ans 52*2
	private static final int NB_VISIT_DAILY_MAX = 50;
	@Inject
	private ServerManager serverManager;

	/**
	 * Test reproduisant des données semi-realistes. 829s pour store des Process
	 * dans Berkley seul. 30000 visites => 150000 pages => 750000 Process.
	 * 
	 * @throws ParseException
	 *             Exception sur la date
	 */
	@Test
	public void testTrueLifeDatas() throws ParseException {
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		final Date dateT0 = sdf.parse("03/01/2011 08:00:00");

		for (int s = FIRST_WEEK; s < FIRST_WEEK + NB_WEEK; s++) {
			int weekVisits = 0;
			final Date dateSemaine = new Date(dateT0.getTime() + s * 7 * 24 * 60 * 60 * 1000L);
			for (int j = 0; j < 5; j++) {
				int dayVisits = 0;
				final Date dateJour = new Date(dateSemaine.getTime() + j * 24 * 60 * 60 * 1000L);
				for (int h = 0; h < 12; h++) {
					final double coef = 1.25 + 0.25 * Math.sin((s * 7d + j) * Math.PI / 2 / (6 * 30));
					final long nbVisit = random(NB_VISIT_DAILY_MAX * Math.cos((h - 3) / 2d / 3.14d), coef); // 50 visites en moyenne par heure en pic
					for (int visit = 0; visit < nbVisit; visit++) {
						final Date dateHeure = new Date(dateJour.getTime() + h * 60 * 60 * 1000L + random(visit * (50 * 60 * 1000L / nbVisit) + 5 * 60 * 1000L, 1));
						addOneVisite(dateHeure, coef);
						dayVisits++;
						weekVisits++;
					}
				}
				log.info("Day :" + dateSemaine + " " + dayVisits + " visits");
			}
			log.info("Week :" + dateSemaine + " " + weekVisits + " visits");
		}
	}

	@Test
	public void testTrueLifeHealthInfos() throws ParseException {
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		final Date dateT0 = sdf.parse("03/01/2011 00:00:00");
		final Map<String, Integer> subSystemHealtState = new HashMap<String, Integer>();
		for (int s = FIRST_WEEK; s < FIRST_WEEK + NB_WEEK; s++) {
			final Date dateSemaine = new Date(dateT0.getTime() + s * 7 * 24 * 60 * 60 * 1000L);
			addConfInfo(dateSemaine, subSystemHealtState);
			for (int j = 0; j < 7; j++) {
				final Date dateJour = new Date(dateSemaine.getTime() + j * 24 * 60 * 60 * 1000L);
				for (int h = 0; h < 24; h++) {
					final double coef = 1.25 + 0.25 * Math.sin((s * 7d + j) * Math.PI / 2 / (6 * 30));
					// final long nbVisit = random(NB_VISIT_DAILY_MAX *
					// Math.cos((h - 3) / 2d / 3.14d), coef); //50 visites en
					// moyenne par heure en pic
					for (int m = 0; m < 60; m += 6) {
						final Date dateMinute = new Date(dateJour.getTime() + h * 60 * 60 * 1000L + m * 60 * 1000L);
						addHealthInfo(dateMinute, coef, subSystemHealtState);
						// weekVisits++;
					}
				}
			}
			log.info("Week :" + dateSemaine + " conf + health");
		}
	}

	private void addHealthInfo(final Date dateMinute, final double coef, final Map<String, Integer> subSystemHealthState) {
		KProcessBuilder healthProcess = new KProcessBuilder(dateMinute, 0, "HEALTH", "/physical");
		final double tendance = random(6000d, 1 + (1 - coef) / 2); // 6000 +/-
		// 1500
		healthProcess.setMeasure("CPU", tendance * 15 / 6000 + random(3d, coef));
		healthProcess.setMeasure("RAM", tendance * 60 / 6000 + random(10d, coef));
		healthProcess.setMeasure("IO", tendance + random(1000d, coef));
		serverManager.push(healthProcess.build());

		healthProcess = new KProcessBuilder(dateMinute, 0, "HEALTH", "/technical/web");// séparer car les sondes seront a des
		// emplacements différents
		healthProcess.setMeasure("SESSION HTTP", tendance * 50 / 6000 + random(10d, coef));
		serverManager.push(healthProcess.build());

		healthProcess = new KProcessBuilder(dateMinute, 0, "HEALTH", "/technical/cache");
		healthProcess.setMeasure("CACHE", tendance * 85 / 6000 + random(10d, coef));
		serverManager.push(healthProcess.build());

		for (final Map.Entry<String, Integer> entry : subSystemHealthState.entrySet()) {
			if (entry.getKey().endsWith("_INIT")) {
				continue;
			}
			final int chanceToBeOk = Math.abs(entry.getValue());
			final int initChanceToBeOk = subSystemHealthState.get(entry.getKey() + "_INIT");
			final boolean isOk = entry.getValue() >= initChanceToBeOk;
			healthProcess = new KProcessBuilder(dateMinute, 0, "HEALTH", "/subsystem/" + entry.getKey());
			healthProcess.setMeasure("HEALTH", isOk ? 100 : 0);
			serverManager.push(healthProcess.build());
			if (!isOk) { // si system KO, on a 'value' chance de revenir en
				// état, sinon les chances augmentent de 5%
				if (entry.getValue() > 0) {
					subSystemHealthState.put(entry.getKey(), randomBoolean(chanceToBeOk) ? initChanceToBeOk : chanceToBeOk + 5);
				} else { // si <0 : problème de conf il faut un reboot
					if (randomBoolean(chanceToBeOk)) {
						addConfInfo(dateMinute, subSystemHealthState);
					} else {
						subSystemHealthState.put(entry.getKey(), -(chanceToBeOk + 5));
					}

				}
			} else if (!randomBoolean(chanceToBeOk)) {// si system OK, on a 99%
				// chance de rester en
				// état, sinon on est KO
				// et 10% de revenir en
				// état
				subSystemHealthState.put(entry.getKey(), 10);
			}
		}
	}

	private void addConfInfo(final Date dateSemaine, final Map<String, Integer> subSystemHealthState) {
		log.info("Week :" + dateSemaine + " Add Conf");
		subSystemHealthState.put("Application", 99); // chance d'etre OK
		subSystemHealthState.put("BDD", 99); // chance d'etre OK
		subSystemHealthState.put("Mail", 90); // chance d'etre OK
		subSystemHealthState.put("LDAP", 99); // chance d'etre OK
		subSystemHealthState.put("Application_INIT", 99); // chance d'etre OK
		subSystemHealthState.put("BDD_INIT", 99); // chance d'etre OK
		subSystemHealthState.put("Mail_INIT", 90); // chance d'etre OK
		subSystemHealthState.put("LDAP_INIT", 99); // chance d'etre OK

		KProcessBuilder confEvent = new KProcessBuilder(dateSemaine, 0, "CONF", "/Managers/RessourceManager");
		confEvent.setMeasure("START", 1);
		confEvent.setMetaData("CONF:RES", "256 Ressources");
		confEvent.setMetaData("CONF:LANG", "1 langue");
		serverManager.push(confEvent.build());

		confEvent = new KProcessBuilder(dateSemaine, 0, "CONF", "/Managers/SecurityManager");
		confEvent.setMeasure("START", 1);
		confEvent.setMetaData("CONF:SEC", "18 roles");
		confEvent.setMetaData("CONF:ROLES", "R_ADMIN;R_AUTHENTIFIED;R_UTILISATEUR_ADMIN;R_UTILISATEUR_READ;R_UTILISATEUR_WRITE;R_UTILISATEUR_EXEC;R_DOSSIER_ADMIN;R_DOSSIER_READ;R_DOSSIER_WRITE;R_DOSSIER_EXEC");
		serverManager.push(confEvent.build());

		confEvent = new KProcessBuilder(dateSemaine, 0, "CONF", "/Managers/SystemManager");
		confEvent.setMeasure("START", 1);
		confEvent.setMetaData("CONF:JDK", "Système Java: 1.6.0_20, OS: Linux/2.6.18-238.9.1.el5, host: 127.0.0.1, hostName: 127.0.0.1, Log level (root) : INFO");
		if (randomBoolean(10)) {// 10% de chance d'une erreur de conf
			confEvent.setMetaData("CONF:MEM:MAX", "Mémoire Maximum : 64 Mo");
			subSystemHealthState.put("Application", 75); // chance d'etre OK
			subSystemHealthState.put("Application_INIT", 75); // chance d'etre
			// OK
		} else {
			confEvent.setMetaData("CONF:MEM:MAX", "Mémoire Maximum : 512 Mo");
		}

		serverManager.push(confEvent.build());

		confEvent = new KProcessBuilder(dateSemaine, 0, "CONF", "/Managers/CodecManager");
		confEvent.setMeasure("START", 1);
		confEvent.setMetaData("CONF:CRYPTO", "Cryptographie : [algorithme=DESede , clé=168] ");
		confEvent.setMetaData("CONF:ZIP", "Compression zip: [taille min= 100octets , niveau de compression=1]");
		serverManager.push(confEvent.build());

		confEvent = new KProcessBuilder(dateSemaine, 0, "CONF", "/Managers/CacheManager");
		confEvent.setMeasure("START", 1);
		confEvent.setMetaData("CONF:EH_CACHE", "Paramétrage : EHCache [ name = DataCache, isOverflowToDisk = true, maxElementsInMemory = 1,000, maxElementsOnDisk = 0, timeToIdleSeconds = 1,800, timeToLiveSeconds = 3,600, eternal = false]");
		confEvent.setMetaData("CONF:DT", "DT_UTILISATEUR;DT_ETAT_DOSSIER;DT_TYPE_DOSSIER;DT_ROLE");
		serverManager.push(confEvent.build());

		confEvent = new KProcessBuilder(dateSemaine, 0, "CONF", "/Managers/DataBaseManager");
		confEvent.setMeasure("START", 1);
		if (randomBoolean(5)) {// 5% de chance d'une erreur de conf
			confEvent.setMetaData("CONF:BDD", "Bdd = Oracle / Oracle Database 11g Enterprise Edition Release 11.2.0.2.0 - 64bit Production\\With the Partitioning option, Driver JDBC = Oracle JDBC driver / 11.1.0.7.0-Production, URL JDBC = jdbc:oracle:thin:@blackhole.dev.klee.lan.net:1521:O11W1252");
			subSystemHealthState.put("BDD", -40); // chance d'etre OK ; négatif
			// => erreur de conf
		} else {
			confEvent.setMetaData("CONF:BDD", "Bdd = Oracle / Oracle Database 11g Enterprise Edition Release 11.2.0.2.0 - 64bit Production\\With the Partitioning option, Driver JDBC = Oracle JDBC driver / 11.1.0.7.0-Production, URL JDBC = jdbc:oracle:thin:@selma.dev.klee.lan.net:1521:O11W1252");
		}
		serverManager.push(confEvent.build());

		confEvent = new KProcessBuilder(dateSemaine, 0, "CONF", "/Managers/LDAPManager");
		confEvent.setMeasure("START", 1);
		if (randomBoolean(10)) {// 10% de chance d'une erreur de conf
			confEvent.setMetaData("CONF:HOST", "Host:mrveille;Port:1999;SSL:non;Racine:ou=INT,ou=USERS,o=ADM;Attributs binaires:null");
			subSystemHealthState.put("LDAP", -10); // chance d'etre OK ; négatif
			// => erreur de conf
		} else {
			confEvent.setMetaData("CONF:HOST", "Host:mrveille;Port:389;SSL:non;Racine:ou=INT,ou=USERS,o=ADM;Attributs binaires:null");
		}
		serverManager.push(confEvent.build());
	}

	@Test
	public void testProcessToCube() {
		int nbProcess;
		do {
			nbProcess = serverManager.store50NextProcessesAsCube();
			log.info("Stored " + nbProcess + " processes as Cubes.");
		} while (nbProcess > 0);
	}

	@Test
	public void testAll() throws ParseException {
		testTrueLifeDatas();
		testProcessToCube();
		testTrueLifeHealthInfos();
		testProcessToCube();
	}

	private void addOneVisite(final Date dateT0, final double coef) {
		Date nextDate = addLogin(dateT0, coef);
		do {
			nextDate = addHome(nextDate, coef);
			nextDate = wait(nextDate, 60 * coef);
			do {
				do {
					nextDate = addSearchUser(nextDate, coef);
					nextDate = wait(nextDate, 20 * coef);
				} while (randomBoolean(20));
				nextDate = wait(nextDate, 20 * coef);

				do {
					nextDate = addViewUser(nextDate, coef);
					nextDate = wait(nextDate, 10 * coef);
				} while (randomBoolean(30));
				nextDate = wait(nextDate, 20 * coef);
			} while (randomBoolean(20));

			if (randomBoolean(40)) {
				nextDate = addEditUser(nextDate, coef);
				nextDate = wait(nextDate, 60 * coef);
			}
			nextDate = wait(nextDate, 60 * coef);
		} while (randomBoolean(75));
	}

	private Date wait(final Date dateT0, final double waitTimeSecond) {
		return new Date(dateT0.getTime() + random(waitTimeSecond * 1000, 1));
	}

	private Date addLogin(final Date dateT0, final double coef) {
		final KProcessBuilder pageProcessBuilder = createProcess(dateT0, 1000, coef, "PAGE", "/login/login.jsf");
		final KProcessBuilder serviceProcessBuilder = createSubProcess(400, coef, "FACADE", "/LoginService/checkCredentials()", pageProcessBuilder);
		serviceProcessBuilder.addSubProcess(createSubProcess(50, coef, "SQL", "/SV_LOAD_USER_BY_LOGIN_PASSWORD", serviceProcessBuilder).build());
		serviceProcessBuilder.addSubProcess(createSubProcess(100, coef, "SQL", "/SV_LOAD_USER_INFORMATIONS", serviceProcessBuilder).build());
		pageProcessBuilder.addSubProcess(serviceProcessBuilder.build());

		final KProcessBuilder service2ProcessBuilder = createSubProcess(200, coef, "FACADE", "/AccueilService/loadHomeDatas()", pageProcessBuilder);
		service2ProcessBuilder.addSubProcess(createSubProcess(50, coef, "SQL", "/SV_LOAD_HOME_DATA_1", service2ProcessBuilder).build());
		service2ProcessBuilder.addSubProcess(createSubProcess(50, coef, "SQL", "/SV_LOAD_HOME_DATA_2", service2ProcessBuilder).build());
		pageProcessBuilder.addSubProcess(service2ProcessBuilder.build());
		pageProcessBuilder.setMeasure("EXCEPTION", randomBoolean(95) ? 0 : 100);
		pageProcessBuilder.setMeasure("PAGE_SIZE", random(10000, 1));
		final KProcess pageProcess = pageProcessBuilder.build();
		serverManager.push(pageProcess);
		// System.out.println(dateT0.getTime() + "\t=>\t1000\t+" + coef + "\t" +
		// pageProcess.getDuration());
		log.debug(pageProcess.getNames());
		return new Date(dateT0.getTime() + pageProcess.getMeasures().get(KProcess.DURATION).longValue());
	}

	private Date addHome(final Date dateT0, final double coef) {
		final KProcessBuilder pageProcessBuilder = createProcess(dateT0, 200, coef, "PAGE", "/accueil/accueil.jsf");
		final KProcessBuilder serviceProcessBuilder = createSubProcess(150, coef, "FACADE", "/AccueilService/loadHomeDatas()", pageProcessBuilder);
		serviceProcessBuilder.addSubProcess(createSubProcess(50, coef, "SQL", "/SV_LOAD_HOME_DATA_1", serviceProcessBuilder).build());
		serviceProcessBuilder.addSubProcess(createSubProcess(50, coef, "SQL", "/SV_LOAD_HOME_DATA_2", serviceProcessBuilder).build());
		pageProcessBuilder.setMeasure("EXCEPTION", randomBoolean(98) ? 0 : 100);
		pageProcessBuilder.setMeasure("PAGE_SIZE", random(10000, 1));
		pageProcessBuilder.addSubProcess(serviceProcessBuilder.build());
		final KProcess pageProcess = pageProcessBuilder.build();
		serverManager.push(pageProcess);
		log.debug(pageProcess.getNames());
		return new Date(dateT0.getTime() + pageProcess.getMeasures().get(KProcess.DURATION).longValue());
	}

	private Date addSearchUser(final Date dateT0, final double coef) {
		final KProcessBuilder pageProcessBuilder = createProcess(dateT0, 1000, coef, "PAGE", "/user/searchUser.jsf");
		final KProcessBuilder serviceProcessBuilder = createSubProcess(950, coef, "FACADE", "/UserService/loadUserByCriteria()", pageProcessBuilder);
		serviceProcessBuilder.addSubProcess(createSubProcess(700, coef, "SQL", "/SV_LOAD_USER_BY_CRITERIA", serviceProcessBuilder).build());
		for (int i = 0; i < 20; i++) {
			serviceProcessBuilder.addSubProcess(createSubProcess(10, coef, "SQL", "/SV_LOAD_USER_INFO", serviceProcessBuilder).build());
		}
		pageProcessBuilder.setMeasure("EXCEPTION", randomBoolean(95) ? 0 : 100);
		pageProcessBuilder.setMeasure("PAGE_SIZE", random(20000, 1));
		pageProcessBuilder.addSubProcess(serviceProcessBuilder.build());
		final KProcess pageProcess = pageProcessBuilder.build();
		serverManager.push(pageProcess);
		log.debug(pageProcess.getNames());
		return new Date(dateT0.getTime() + pageProcess.getMeasures().get(KProcess.DURATION).longValue());
	}

	private Date addViewUser(final Date dateT0, final double coef) {
		final KProcessBuilder pageProcessBuilder = createProcess(dateT0, 500, coef, "PAGE", "/user/viewUser.jsf");
		addLoadUserProcess(pageProcessBuilder, coef);
		final KProcess pageProcess = pageProcessBuilder.build();
		serverManager.push(pageProcess);
		log.debug(pageProcess.getNames());
		return new Date(dateT0.getTime() + pageProcess.getMeasures().get(KProcess.DURATION).longValue());
	}

	private Date addEditUser(final Date dateT0, final double coef) {
		final KProcessBuilder pageProcessBuilder = createProcess(dateT0, 1000, coef, "PAGE", "/user/editUser.jsf");
		final KProcessBuilder service1ProcessBuilder = createSubProcess(200, coef, "FACADE", "/UserService/saveUser()", pageProcessBuilder);
		service1ProcessBuilder.addSubProcess(createSubProcess(150, coef, "SQL", "/SV_UPDATE_USER", service1ProcessBuilder).build());
		service1ProcessBuilder.addSubProcess(createSubProcess(50, coef, "SQL", "/SV_UPDATE_USER_INFO", service1ProcessBuilder).build());
		service1ProcessBuilder.setMeasure("EXCEPTION", randomBoolean(98) ? 0 : 100);
		pageProcessBuilder.addSubProcess(service1ProcessBuilder.build());

		addLoadUserProcess(pageProcessBuilder, coef);
		final KProcess pageProcess = pageProcessBuilder.build();
		serverManager.push(pageProcess);
		log.debug(pageProcess.getNames());
		return new Date(dateT0.getTime() + pageProcess.getMeasures().get(KProcess.DURATION).longValue());
	}

	private void addLoadUserProcess(final KProcessBuilder pageProcessBuilder, final double coef) {
		final KProcessBuilder serviceProcessBuilder = createSubProcess(400, coef, "FACADE", "/UserService/loadUser()", pageProcessBuilder);
		serviceProcessBuilder.addSubProcess(createSubProcess(50, coef, "SQL", "/SV_LOAD_USER", serviceProcessBuilder).build());

		final KProcessBuilder service2ProcessBuilder = createSubProcess(20, coef, "FACADE", "/ReferentielService/loadCodePostaux()", serviceProcessBuilder);
		if (randomBoolean(90)) {
			service2ProcessBuilder.setMeasure("REF_CACHE_HIT", 100);
		} else {
			service2ProcessBuilder.setMeasure("REF_CACHE_HIT", 0);
			service2ProcessBuilder.addSubProcess(createSubProcess(18, coef, "SQL", "/SV_LOAD_CODE_POSTAL", service2ProcessBuilder).build());
		}
		serviceProcessBuilder.addSubProcess(service2ProcessBuilder.build());
		final KProcessBuilder service3ProcessBuilder = createSubProcess(5, coef, "FACADE", "/ReferentielService/loadEtats()", serviceProcessBuilder);
		serviceProcessBuilder.addSubProcess(service3ProcessBuilder.build());

		serviceProcessBuilder.addSubProcess(createSubProcess(20, coef, "SQL", "/SV_LOAD_USER_INFO", serviceProcessBuilder).build());

		pageProcessBuilder.setMeasure("EXCEPTION", randomBoolean(98) ? 0 : 100);
		pageProcessBuilder.setMeasure("PAGE_SIZE", random(5000, 1));
		pageProcessBuilder.addSubProcess(serviceProcessBuilder.build());

	}

	private KProcessBuilder createProcess(final Date dataT0, final int averageTime, final double coef, final String module, final String fullName) {
		return new KProcessBuilder(dataT0, random(averageTime, coef), module, fullName);
	}

	private KProcessBuilder createSubProcess(final int averageTime, final double coef, final String module, final String fullName, final KProcessBuilder parentProcess) {
		final KProcess parent = parentProcess.build();
		final long parentStartTime = parent.getStartDate().getTime();
		long startTime = parentStartTime;
		for (final KProcess process : parent.getSubProcesses()) {
			startTime += process.getMeasures().get(KProcess.DURATION).longValue() + 10;// 10ms entre process
		}
		final long thisDuration = Math.max(Math.min(random(averageTime, coef), parentStartTime + parent.getMeasures().get(KProcess.DURATION).longValue() - startTime), 0);
		final KProcessBuilder processBuilder = new KProcessBuilder(new Date(startTime), thisDuration, module, fullName);
		return processBuilder;
	}

	private boolean randomBoolean(final int proba) {
		return Boolean.valueOf(Math.random() < proba / 100d);
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
