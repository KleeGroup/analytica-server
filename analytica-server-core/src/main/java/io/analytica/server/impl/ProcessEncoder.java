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
package io.analytica.server.impl;

import io.analytica.api.KProcess;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.cube.HMetricBuilder;
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

/**
 * Impl�mentation de la transformation des Process en cubes.
 * 
 * Transformation d'un Process constitu� de sous-process.
 * Chaque Process (et donc sous process) est transform� en Cube avec :
 * - une agregation des mesures de ce process
 * - une agregation des mesures des sous process 
 * 
 * 
 * @author npiedeloup
 * @version $Id: StandardProcessEncoderPlugin.java,v 1.16 2012/10/16 17:27:12 pchretien Exp $
 */
public final class ProcessEncoder {

	public static class Dual {
		final HCubeBuilder cubeBuilder = new HCubeBuilder();
		final HKey key;

		private Dual(final KProcess process) {
			final HTime time = new HTime(process.getStartDate(), HTimeDimension.Minute);
			final HCategory category = new HCategory(process.getType(), process.getSubTypes() != null ? process.getSubTypes() : new String[0]);
			//	final HLocation location = new HLocation(process.getSystemName(), process.getSystemLocation() != null ? process.getSystemLocation() : new String[0]);
			key = new HKey(time, category/*, location*/);
		}
	}

	/**
	 * Transforme un KProcess et ses sous process en cubes.
	 * @param process Process � convertir
	 * @return Liste des Cubes associ�s
	 */
	public List<Dual> encode(final KProcess process) {
		final List<Dual> resultBuilder = new Dual<>(process);
		doEncode(process, Collections.unmodifiableList(new ArrayList<HCubeBuilder>()), resultBuilder);
		//---
		final List<HCube> result = new ArrayList<>();
		for (final HCubeBuilder cubeBuilder : resultBuilder) {
			result.add(cubeBuilder.build());
		}
		return result;
	}

	/**
	 * On transforme un Process en un cube.
	 * @param process KProcess a transformer
	 * @param result Liste des cubes r�sultat
	 */
	private static void doEncode(final KProcess process, final List<HCubeBuilder> parentBuilders, final List<HCubeBuilder> allResultBuilders) {
		//On aggr�ge les mesures dans un nouveau cube 
		final HKey key = createKey(process);

		final HCubeBuilder localBuilder = encodeMeasures(process);
		//On ajoute les dur�es du process dans ses parents
		encodeSubDurations(process, parentBuilders);

		allResultBuilders.add(localBuilder);

		//On recr�e l'arbre des builders depuis la racine jusqu'� ce builder
		final List<HCubeBuilder> localBuilders = new ArrayList<>(parentBuilders);
		localBuilders.add(localBuilder);

		//On ajoute les sous-process
		for (final KProcess subProcess : process.getSubProcesses()) {
			doEncode(subProcess, Collections.unmodifiableList(localBuilders), allResultBuilders);
		}
	}

	/**
	 * Transforme le Process de premier niveau en un cube.
	 * @param process
	 * @param parentCubeBuilders
	 * @return HCubeBuilder le cubeBuilder du process
	 */
	private static HCubeBuilder encodeMeasures(final KProcess process) {
		final HCubeBuilder cubeBuilder = new HCubeBuilder();
		//---
		for (final Entry<String, Double> measure : process.getMeasures().entrySet()) {
			// Cas g�n�ral : on ajoute la mesure sous forme de m�tric dans le cube 
			final boolean cluster = KProcess.DURATION.equals(measure.getKey());
			HMetricKey metricKey = new HMetricKey(measure.getKey(), cluster);
			cubeBuilder.withMetric(metricKey, new HMetricBuilder(metricKey).withValue(measure.getValue()).build());
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
		//On remonte les dur�e au parent
		final double duration = process.getDuration();
		final Double subDuration = process.getMeasures().get(KProcess.SUB_DURATION);
		HMetricKey metricKey = new HMetricKey(process.getType(), true);
		HMetricKey subProcessKey = new HMetricKey("sub-" + process.getType(), true);
		for (final HCubeBuilder parentBuilder : parentBuilders) {
			// Cas g�n�ral : on ajoute la mesure sous forme de m�tric dans le cube 
			parentBuilder.withMetric(metricKey, new HMetricBuilder(metricKey).withValue(duration).build());
			if (subDuration != null) {
				parentBuilder.withMetric(subProcessKey, new HMetricBuilder(subProcessKey).withValue(subDuration).build());
			}
		}
	}
}
