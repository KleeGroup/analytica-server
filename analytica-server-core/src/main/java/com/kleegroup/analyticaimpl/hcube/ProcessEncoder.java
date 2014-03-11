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
package com.kleegroup.analyticaimpl.hcube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HCubeBuilder;
import com.kleegroup.analytica.hcube.cube.HMetricBuilder;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HCubeKey;
import com.kleegroup.analytica.hcube.dimension.HTime;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;

/**
 * Implémentation de la transformation des Process en cubes.
 * 
 * Transformation d'un Process constitué de sous-process.
 * Chaque Process (et donc sous process) est transformé en Cube avec :
 * - une agregation des mesures de ce process
 * - une agregation des mesures des sous process 
 * 
 * 
 * @author npiedeloup
 * @version $Id: StandardProcessEncoderPlugin.java,v 1.16 2012/10/16 17:27:12 pchretien Exp $
 */
final class ProcessEncoder {

	/**
	 * Transforme un KProcess et ses sous process en cubes.
	 * @param process Process à convertir
	 * @return Liste des Cubes associés
	 */
	List<HCube> encode(final KProcess process) {
		final List<HCubeBuilder> resultBuilder = new ArrayList<>();
		doEncode(process, Collections.unmodifiableList(new ArrayList<HCubeBuilder>()), resultBuilder);
		//---
		final List<HCube> result = new ArrayList<HCube>();
		for (final HCubeBuilder cubeBuilder : resultBuilder) {
			result.add(cubeBuilder.build());
		}
		return result;
	}

	/**
	 * On transforme un Process en un cube.
	 * @param process KProcess a transformer
	 * @param result Liste des cubes résultat
	 */
	private static void doEncode(final KProcess process, final List<HCubeBuilder> parentBuilders, final List<HCubeBuilder> allResultBuilders) {
		//On aggrège les mesures dans un nouveau cube 
		final HCubeBuilder localBuilder = encodeMeasures(process);
		//On ajoute les durées du process dans ses parents
		encodeSubDurations(process, parentBuilders);

		allResultBuilders.add(localBuilder);

		//On recrée l'arbre des builders depuis la racine jusqu'à ce builder
		final List<HCubeBuilder> localBuilders = new ArrayList<>(parentBuilders);
		localBuilders.add(localBuilder);

		//On ajoute les sous-process
		for (final KProcess subProcess : process.getSubProcesses()) {
			doEncode(subProcess, Collections.unmodifiableList(localBuilders), allResultBuilders);
		}
	}

	private static HCubeBuilder createCubeBuilder(final KProcess process) {
		final HTime time = new HTime(process.getStartDate(), HTimeDimension.Minute);
		final HCategory category = new HCategory(process.getType(), process.getSubTypes() != null ? process.getSubTypes() : new String[0]);
		final HCubeKey cubeKey = new HCubeKey(time, category);
		return new HCubeBuilder(cubeKey);
	}

	/**
	 * Transforme le Process de premier niveau en un cube.
	 * @param process
	 * @param parentCubeBuilders
	 * @return HCubeBuilder le cubeBuilder du process
	 */
	private static HCubeBuilder encodeMeasures(final KProcess process) {
		final HCubeBuilder cubeBuilder = createCubeBuilder(process);
		//---
		for (final Entry<String, Double> measure : process.getMeasures().entrySet()) {
			// Cas général : on ajoute la mesure sous forme de métric dans le cube 
			final boolean cluster = KProcess.DURATION.equals(measure.getKey());
			cubeBuilder.withMetric(new HMetricBuilder(new HMetricKey(measure.getKey(), cluster)).withValue(measure.getValue()).build());
		}
		return cubeBuilder;
	}

	/**
	 * Transforme le Process de premier niveau en un cube.
	 * @param process
	 * @param parentCubeBuilders
	 * @return HCubeBuilder le cubeBuilder du process
	 */
	private static void encodeSubDurations(final KProcess process, final List<HCubeBuilder> parentBuilders) {
		//On remonte les durée au parent
		final double duration = process.getDuration();
		final Double subDuration = process.getMeasures().get(KProcess.SUB_DURATION);

		for (final HCubeBuilder parentBuilder : parentBuilders) {
			// Cas général : on ajoute la mesure sous forme de métric dans le cube 
			parentBuilder.withMetric(new HMetricBuilder(new HMetricKey(process.getType(), true)).withValue(duration).build());
			if (subDuration != null) {
				parentBuilder.withMetric(new HMetricBuilder(new HMetricKey("sub-" + process.getType(), true)).withValue(subDuration).build());
			}
		}
	}
}
