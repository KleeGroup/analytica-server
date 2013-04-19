/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidi�re - BP 159 - 92357 Le Plessis Robinson Cedex - France
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

/**
 * Builder permettant de contruire un processus.
 * Il y a deux modes de cr�ation.
 *  - live (La date de d�but et celle de la cr�ation , la dur�e s'obtient lors de la cr�ation du process
 *  - diff�r� (la date de d�bute et la dur�e sont renseign�e ensembles )
 * 
 * @author pchretien
 * @version $Id: KProcessBuilder.java,v 1.18 2012/11/08 17:06:27 pchretien Exp $
 */
public final class KProcessBuilder implements Builder<KProcess> {
	private final String type;
	private final String[] names;
	private final Date startDate;

	//Tableau des mesures identifi�es par leur nom. 
	private final Map<String, Double> measures;

	//Tableau des m�tadonn�es identifi�es par leur nom. 
	private final Map<String, String> metaDatas;

	private final long start;
	private Double durationMs = null;
	private final List<KProcess> subProcesses;

	/**
	 * Constructeur.
	 * La date de d�but du processus est implicitement la date actuelle
	 * La dur�e du processus sera obtenue lors de l'appel � la m�thode build().
	 * @param type Type du processus
	 * @param name Nom du processus
	 */
	public KProcessBuilder(final String type, final String... names) {
		this(new Date(), type, names);
	}

	/**
	 * Constructeur pour deserialization.
	 * @param type Type du processus
	 * @param names Nom du processus
	 * @param startDate Date de d�but processus
	 * @param duration Dur�e du processus (Millisecondes)
	 */
	public KProcessBuilder(final Date startDate, final double durationMs, final String type, final String... names) {
		this(startDate, type, names);
		//---------------------------------------------------------------------
		this.durationMs = durationMs;
	}

	private KProcessBuilder(final Date startDate, final String type, final String[] names) {
		Assertion.notEmpty(type);
		Assertion.notNull(names);
		Assertion.notNull(startDate);
		Assertion.precondition(KProcess.TYPE_REGEX.matcher(type).matches(), "le type du processus ne respecte pas la regex {0}", KProcess.TYPE_REGEX);
		//---------------------------------------------------------------------
		measures = new HashMap<String, Double>();
		metaDatas = new HashMap<String, String>();
		subProcesses = new ArrayList<KProcess>();
		this.startDate = startDate;
		start = startDate.getTime();
		this.type = type;
		this.names = names;
	}

	/**
	 * Incr�ment d'une mesure. 
	 * Si la mesure est nouvelle, elle est automatiquement cr��e avec la valeur
	 * @param mName Nom de la mesure
	 * @param mValue  Valeur � incr�menter
	 */
	public KProcessBuilder incMeasure(final String mName, double mValue) {
		Assertion.notNull(mName);
		//---------------------------------------------------------------------
		final Double lastmValue = measures.get(mName);
		measures.put(mName, lastmValue == null ? mValue : mValue + lastmValue);
		return this;
	}

	/** 
	 * Mise � jour d'une mesure.
	 * @param mName Nom de la mesure
	 * @param mValue  Valeur � incr�menter
	 */
	public KProcessBuilder setMeasure(final String mName, double mValue) {
		Assertion.notNull(mName);
		//---------------------------------------------------------------------
		measures.put(mName, mValue);
		return this;
	}

	/** 
	 * Mise � jour d'une metadonn�e.
	 * @param mdName Nom de la m�tadonn�e
	 * @param mdValue  Valeur de la m�tadonn�e
	 */
	public KProcessBuilder setMetaData(final String mdName, String mdValue) {
		Assertion.notNull(mdName);
		Assertion.notNull(mdValue);
		//---------------------------------------------------------------------
		metaDatas.put(mdName, mdValue);
		return this;
	}

	/**
	 * Ajout d'un sous processus.
	 * @param process Sous-Processus � ajouter
	 */
	public KProcessBuilder addSubProcess(final KProcess process) {
		Assertion.notNull(process);
		//---------------------------------------------------------------------
		subProcesses.add(process);
		return this;
	}

	/** 
	 * Construction du Processus.
	 * @return Process
	 */
	public KProcess build() {
		//Si on est dans le mode de construction en runtime, on ajoute la dur�e.
		if (durationMs == null) {
			durationMs = Long.valueOf(System.currentTimeMillis() - start).doubleValue();
		}
		//On ajoute la mesure obligatoire : dur�e
		setMeasure(KProcess.DURATION, durationMs);
		return new KProcess(type, names, startDate, measures, metaDatas, subProcesses);
	}
}
