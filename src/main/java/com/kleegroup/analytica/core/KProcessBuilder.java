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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

/**
 * Builder permettant de contruire un processus.
 * Il y a deux modes de création.
 *  - live (La date de début et celle de la création , la durée s'obtient lors de la création du process
 *  - différé (la date de débute et la durée sont renseignée ensembles )
 * 
 * @author pchretien
 * @version $Id: KProcessBuilder.java,v 1.18 2012/11/08 17:06:27 pchretien Exp $
 */
public final class KProcessBuilder implements Builder<KProcess> {
	private final String type;
	private final String[] names;
	private final Date startDate;

	//Tableau des mesures identifiées par leur nom. 
	private final Map<String, Double> measures;

	//Tableau des métadonnées identifiées par leur nom. 
	private final Map<String, String> metaDatas;

	private final long start;
	private Double durationMs = null;
	private final List<KProcess> subProcesses;

	/**
	 * Constructeur.
	 * La date de début du processus est implicitement la date actuelle
	 * La durée du processus sera obtenue lors de l'appel à la méthode build().
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
	 * @param startDate Date de début processus
	 * @param duration Durée du processus (Millisecondes)
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
	 * Incrément d'une mesure. 
	 * Si la mesure est nouvelle, elle est automatiquement créée avec la valeur
	 * @param mName Nom de la mesure
	 * @param mValue  Valeur à incrémenter
	 */
	public KProcessBuilder incMeasure(final String mName, double mValue) {
		Assertion.notNull(mName);
		//---------------------------------------------------------------------
		final Double lastmValue = measures.get(mName);
		measures.put(mName, lastmValue == null ? mValue : mValue + lastmValue);
		return this;
	}

	/** 
	 * Mise à jour d'une mesure.
	 * @param mName Nom de la mesure
	 * @param mValue  Valeur à incrémenter
	 */
	public KProcessBuilder setMeasure(final String mName, double mValue) {
		Assertion.notNull(mName);
		//---------------------------------------------------------------------
		measures.put(mName, mValue);
		return this;
	}

	/** 
	 * Mise à jour d'une metadonnée.
	 * @param mdName Nom de la métadonnée
	 * @param mdValue  Valeur de la métadonnée
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
	 * @param process Sous-Processus à ajouter
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
		//Si on est dans le mode de construction en runtime, on ajoute la durée.
		if (durationMs == null) {
			durationMs = Long.valueOf(System.currentTimeMillis() - start).doubleValue();
		}
		//On ajoute la mesure obligatoire : durée
		setMeasure(KProcess.DURATION, durationMs);
		return new KProcess(type, names, startDate, measures, metaDatas, subProcesses);
	}
}
