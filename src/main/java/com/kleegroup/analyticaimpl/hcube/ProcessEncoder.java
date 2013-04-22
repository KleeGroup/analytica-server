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
import java.util.List;
import java.util.Map.Entry;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.hcube.cube.Cube;
import com.kleegroup.analytica.hcube.cube.CubeBuilder;
import com.kleegroup.analytica.hcube.cube.MetricBuilder;
import com.kleegroup.analytica.hcube.cube.MetricKey;
import com.kleegroup.analytica.hcube.dimension.CubePosition;
import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.dimension.TimePosition;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;

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
	List<Cube> encode(final KProcess process) {
		final List<Cube> result = new ArrayList<Cube>();
		doEncode(process, result);
		return result;
	}

	/**
	 * On transforme un Process en un cube.
	 * @param process KProcess a transformer
	 * @param result Liste des cubes résultat
	 * @return Cube du process de premier niveau
	 */
	private static void doEncode(final KProcess process, final List<Cube> result) {
		//On ajoute les mesures
		result.add(encodeMeasures(process));
		//On ajoute les sous-process
		for (final KProcess subProcess : process.getSubProcesses()) {
			doEncode(subProcess, result);
		}
	}

	private static CubeBuilder createCubeBuilder(KProcess process) {
		final TimePosition timePosition = new TimePosition(process.getStartDate(), TimeDimension.Minute);
		final WhatPosition whatPosition = new WhatPosition(process.getType(), process.getNames());
		final CubePosition cubePosition = new CubePosition(timePosition, whatPosition);
		return new CubeBuilder(cubePosition);
	}

	/**
	 * Transforme le Process de premier niveau en un cube.
	 * @param process
	 * @param parentCubeBuilders
	 * @return
	 */
	private static Cube encodeMeasures(final KProcess process) {
		CubeBuilder cubeBuilder = createCubeBuilder(process);
		for (final Entry<String, Double> measure : process.getMeasures().entrySet()) {
			// Cas général : on ajoute la mesure sous forme de métric dans le cube 
			boolean cluster = KProcess.DURATION.equals(measure.getKey());
			cubeBuilder.withMetric(new MetricBuilder(new MetricKey(measure.getKey(), cluster)).withValue(measure.getValue()).build());
		}
		//On ajoute les durées sous-process
		for (final KProcess subProcess : process.getSubProcesses()) {
			// Cas général : on ajoute la mesure sous forme de métric dans le cube 
			cubeBuilder.withMetric(new MetricBuilder(new MetricKey(subProcess.getType(), true)).withValue(subProcess.getDuration()).build());
		}
		return cubeBuilder.build();
	}
}
