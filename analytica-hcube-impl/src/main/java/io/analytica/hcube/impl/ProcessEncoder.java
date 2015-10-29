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
package io.analytica.hcube.impl;

import io.analytica.api.KProcess;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.cube.HMetricBuilder;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HLocation;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

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
public final class ProcessEncoder {

	public static class Dual {
		final private HCubeBuilder cubeBuilder;
		final private HKey key;

		private Dual(final KProcess process, final HCubeBuilder cubeBuilder) {
			final HTime time = new HTime(process.getStartDate(), HTimeDimension.Minute);
			final int nbOfCategoryTerms = process.getCategory() != null ? process.getCategoryAsArray().length : 0;
			final String[] categories = new String[nbOfCategoryTerms + 1];
			categories[0] = process.getType();
			if (nbOfCategoryTerms > 0) {
				System.arraycopy(process.getCategoryAsArray(), 0, categories, 1, nbOfCategoryTerms);
			}

			final HCategory category = new HCategory(categories);
			// TODO ETAPE 1 Utilisation du premier element du tableau
			final HLocation location = new HLocation(process.getLocationAsArray() != null ? process.getLocationAsArray() : new String[0]);
			key = new HKey(location, time, category/*, location*/);
			this.cubeBuilder = cubeBuilder;
		}

		public HCubeBuilder getCubeBuilder() {
			return cubeBuilder;
		}

		public HKey getKey() {
			return key;
		}

	}

	/**
	 * Transforme un KProcess et ses sous process en cubes.
	 * @param process Process à convertir
	 * @return Liste des Cubes associés
	 */
	public List<Dual> encode(final KProcess process) {
		final List<Dual> result = new ArrayList<>();
		doEncode(process, Collections.unmodifiableList(new ArrayList<HCubeBuilder>()), result);
		//---

		return result;
	}

	/**
	 * On transforme un Process en un cube.
	 * @param process KProcess a transformer
	 * @param result Liste des cubes résultat
	 */
	private static void doEncode(final KProcess process, final List<HCubeBuilder> parentBuilders, final List<Dual> allResultBuilders) {
		//On aggrège les mesures dans un nouveau cube
		final HCubeBuilder localBuilder = encodeMeasures(process);
		//On ajoute les durées du process dans ses parents
		encodeSubDurations(process, parentBuilders);

		allResultBuilders.add(new Dual(process, localBuilder));

		//On recrée l'arbre des builders depuis la racine jusqu'à ce builder
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
			// Cas général : on ajoute la mesure sous forme de métric dans le cube
			cubeBuilder.withMetric(new HMetricBuilder(measure.getKey()).withValue(measure.getValue()).build());
		}
		return cubeBuilder;
	}

	/**
	 * Transforme le Process de premier niveau en un cube.
	 * @param process
	 * @param parentCubeBuilders
	 */
	private static void encodeSubDurations(final KProcess process, final List<HCubeBuilder> parentBuilders) {
		//On remonte les durée au parent
		final double duration = process.getDuration();
		final Double subDuration = process.getMeasures().get(KProcess.SUB_DURATION);
		for (final HCubeBuilder parentBuilder : parentBuilders) {
			// Cas général : on ajoute la mesure sous forme de métric dans le cube
			parentBuilder.withMetric(new HMetricBuilder(process.getType()).withValue(duration).build());
			if (subDuration != null) {
				parentBuilder.withMetric(new HMetricBuilder("sub_" + process.getType()).withValue(subDuration).build());
			}
		}
	}
}
