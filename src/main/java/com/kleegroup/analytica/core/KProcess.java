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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A process is an event with
 * - a category defined by 
 * 		--a type 
 * 		--an array of subTypes	
 * - a start date
 * - a list of sub processes
 * - a duration (cf.measures)
 * - a list of measures  with a DURATION  measure 
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
	 * Mesure de type durée.  
	 */
	public static final String SUB_DURATION = "sub-duration";
	/**
	 * REGEX décrivant les règles du type de process. (exemples : SQL, MAIL, REQUEST)
	 */
	public static final Pattern TYPE_REGEX = Pattern.compile("[A-Z][A-Z0-9_]*");

	private final String type;
	private final String[] subTypes;
	private final Date startDate;

	private final Map<String, Double> measures;
	private final Map<String, String> metaDatas;
	private final List<KProcess> subProcesses;

	/*
	 * Le constructeur est package car il faut passer par le builder.
	 */
	KProcess(final String type, final String[] subTypes, final Date startDate, final Map<String, Double> measures, final Map<String, String> metaDatas, final List<KProcess> subProcesses) {
		if (type == null) {
			throw new NullPointerException("type of process is required");
		}
		if (subTypes == null) {
			throw new NullPointerException("subTypes of process are required");
		}
		if (!TYPE_REGEX.matcher(type).matches()) {
			throw new NullPointerException("process type must match regex :" + TYPE_REGEX);
		}
		if (!measures.containsKey(DURATION)) {
			throw new NullPointerException("measures must contain DURATION");
		}
		//---------------------------------------------------------------------
		this.type = type;
		this.subTypes = subTypes;
		this.startDate = startDate;
		this.measures = Collections.unmodifiableMap(new HashMap<String, Double>(measures));
		this.metaDatas = Collections.unmodifiableMap(new HashMap<String, String>(metaDatas));
		this.subProcesses = subProcesses;
	}

	/**
	 * @return Type du processus
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return Sous-types du processus
	 */
	public String[] getSubTypes() {
		return subTypes;
	}

	/**@return Process duration */
	public double getDuration() {
		return measures.get(DURATION);
	}

	public Date getStartDate() {
		return startDate;
	}

	public Map<String, Double> getMeasures() {
		return measures;
	}

	public Map<String, String> getMetaDatas() {
		return metaDatas;
	}

	public List<KProcess> getSubProcesses() {
		return subProcesses;
	}

	@Override
	public String toString() {
		return "process:{category:{ type:" + type + ", subTypes:" + Arrays.asList(subTypes) + "}; startDate:" + startDate + "}";
	}
}
