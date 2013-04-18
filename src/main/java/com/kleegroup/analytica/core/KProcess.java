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
package com.kleegroup.analytica.core;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import kasper.kernel.util.Assertion;

/**
 * Un process est un événement ayant 
 * - un type 
 * - un nom
 * - une date de début 
 * - une liste de sous process
 * - une durée (cf.mesures)
 * - une liste de mesures dont obligatoirement une mesure de type 'duration'
 * - une liste de métadonnées
 * 
 * @author pchretien
 * @version $Id: KProcess.java,v 1.8 2012/10/16 17:18:26 pchretien Exp $
 */
public final class KProcess {
	/**
	 * Mesure de type durée.  
	 */
	public static final String DURATION = "duration";
	/**
	 * REGEX décrivant les règles du type de process. (exemples : SQL, MAIL, REQUEST)
	 */
	public static final Pattern TYPE_REGEX = Pattern.compile("[A-Z][A-Z0-9_]*");

	private final String type;
	private final String name;
	private final Date startDate;

	private final Map<String, Double> measures;
	private final Map<String, String> metaDatas;
	private final List<KProcess> subProcesses;

	/*
	 * Le constructeur est package car il faut passer par le builder.
	 */
	KProcess(final String type, final String name, final Date startDate, final Map<String, Double> measures, final Map<String, String> metaDatas, final List<KProcess> subProcesses) {
		Assertion.notEmpty(type);
		Assertion.notEmpty(name);
		Assertion.precondition(TYPE_REGEX.matcher(type).matches(), "le type du processus ne respecte pas la regex {0}", TYPE_REGEX);
		Assertion.precondition(measures.containsKey(DURATION), "durée est obligatoire");
		//---------------------------------------------------------------------
		this.type = type;
		this.name = name;
		this.startDate = startDate;
		this.measures = Collections.unmodifiableMap(new HashMap<String, Double>(measures));
		this.metaDatas = Collections.unmodifiableMap(new HashMap<String, String>(metaDatas));
		this.subProcesses = subProcesses;
	}

	/**
	 * @return Type duprocessus
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return Nom du processus
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Date de début du processus
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return Liste des mesures 
	 */
	public Map<String, Double> getMeasures() {
		return measures;
	}

	/**
	 * @return Liste des meta-données 
	 */
	public Map<String, String> getMetaDatas() {
		return metaDatas;
	}

	/**
	 * @return Liste des Sous-Processus
	 */
	public List<KProcess> getSubProcesses() {
		return subProcesses;
	}

	public String toString() {
		return "process:{startDate:" + startDate + "; name:" + name + "}";
	}
}
