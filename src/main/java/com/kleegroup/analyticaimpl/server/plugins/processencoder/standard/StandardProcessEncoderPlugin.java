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
package com.kleegroup.analyticaimpl.server.plugins.processencoder.standard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.hcube.cube.Cube;
import com.kleegroup.analytica.hcube.cube.CubeBuilder;
import com.kleegroup.analytica.hcube.cube.MetaData;
import com.kleegroup.analytica.hcube.cube.Metric;
import com.kleegroup.analytica.hcube.cube.MetricBuilder;
import com.kleegroup.analytica.hcube.dimension.CubePosition;
import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.dimension.TimePosition;
import com.kleegroup.analytica.hcube.dimension.WhatDimension;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;
import com.kleegroup.analyticaimpl.server.ProcessEncoderPlugin;

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
public final class StandardProcessEncoderPlugin implements ProcessEncoderPlugin {

	/** {@inheritDoc} */
	public List<Cube> encode(final KProcess process) {
		final List<Cube> result = new ArrayList<Cube>();
		final List<CubeBuilder> parentCubeBuilders = new ArrayList<CubeBuilder>();

		final Cube cube = doEncode(process, parentCubeBuilders, result);
		result.add(cube);
		return result;
	}

	/**
	 * On transforme un Process en un cube.
	 * @param process KProcess a transformer
	 * @param parentCubeBuilders Pile des CubeBuilder des parents
	 * @param result Liste des cubes résultat
	 * @return Cube du process de premier niveau
	 */
	private static Cube doEncode(final KProcess process, final List<CubeBuilder> parentCubeBuilders, final List<Cube> result) {
		final CubeBuilder cubeBuilder = encodeSingle(process, parentCubeBuilders);
		parentCubeBuilders.add(cubeBuilder);
		for (final KProcess subProcess : process.getSubProcesses()) {
			final Cube subCube = doEncode(subProcess, parentCubeBuilders, result);
			result.add(subCube);
		}
		parentCubeBuilders.remove(cubeBuilder);
		return cubeBuilder.build();
	}

	/**
	 * Transforme le Process de premier niveau en un cub
	 * @param process
	 * @param parentCubeBuilders
	 * @return
	 */
	private static CubeBuilder encodeSingle(final KProcess process, final List<CubeBuilder> parentCubeBuilders) {
		final TimePosition timePosition = new TimePosition(process.getStartDate(), TimeDimension.Minute);
		final StringBuilder sb = new StringBuilder();
		sb.append(WhatDimension.SEPARATOR).append(process.getType());
		if (!process.getName().startsWith(WhatDimension.SEPARATOR)) {
			sb.append(WhatDimension.SEPARATOR);
		}
		sb.append(process.getName());
		final WhatPosition whatPosition = new WhatPosition(sb.toString(), WhatDimension.FullName);
		final String whatTypePositionValue = new WhatPosition(sb.toString(), WhatDimension.Type).getValue().substring(1); //On retire le premier /
		sb.setLength(0);

		final CubePosition key = new CubePosition(timePosition, whatPosition);
		final CubeBuilder cubeBuilder = new CubeBuilder(key);

		for (final Entry<String, Double> measure : process.getMeasures().entrySet()) {
			addMetric(measure.getKey(), measure.getValue(), cubeBuilder, whatTypePositionValue, parentCubeBuilders, sb);
		}

		for (final Entry<String, String> metaData : process.getMetaDatas().entrySet()) {
			cubeBuilder.withMetaData(new MetaData(metaData.getKey(), metaData.getValue()));
		}

		return cubeBuilder;
	}

	private static void addMetric(final String measureName, final double measureValue, final CubeBuilder cubeBuilder, final String whatModulePositionValue, final List<CubeBuilder> parentCubeBuilders, final StringBuilder sb) {
		Assertion.precondition(sb.length() == 0, "Le buffer doit être vide");
		//---------------------------------------------------------------------
		// 1- Cas général : on ajoute la mesure sous forme de métric dans le cube 
		cubeBuilder.withMetric(new MetricBuilder(measureName).withValue(measureValue).build());
		//---------------------------------------------------------------------
		//---------------------------------------------------------------------
		// 2- Cas particulier : Certaines métriques sont escaladées au niveau des processus parents.  
		if (isClimbingMetric(measureName)) {
			sb.append(whatModulePositionValue).append("_").append(measureName);
			final Metric metric = new MetricBuilder(sb.toString()).withValue(measureValue).build();
			sb.setLength(0);
			for (final CubeBuilder parentCubeBuilder : parentCubeBuilders) {
				parentCubeBuilder.withMetric(metric);
			}
		}
		// 3- Cas particulier : Certaines mesures sont déerivées sous la forme d'autres mesures (exemple : calcul des distributions)
		if (isClusteredMetric(measureName)) {
			doClustering(measureName, measureValue, cubeBuilder, whatModulePositionValue, parentCubeBuilders, sb);
		}
	}

	private static void doClustering(final String measureType, final double value, final CubeBuilder cubeBuilder, final String whatModulePositionValue, final List<CubeBuilder> parentCubeBuilders, final StringBuilder sb) {
		Assertion.precondition(sb.length() == 0, "Le buffer doit être vide");
		final long[] minMax = getClusteredMetricMinMax(measureType);
		//On crée une répartion : 10,20,50 ...
		final String strValue = String.valueOf(Math.round(value));
		long clusterValue = value == 0 ? 0 : -1;
		long indice = (long) Math.pow(10, strValue.length() - 1);
		while (clusterValue == -1) {
			if (value <= 1 * indice) {
				clusterValue = 1 * indice;
			} else if (value <= 2 * indice) {
				clusterValue = 2 * indice;
			} else if (value <= 5 * indice) {
				clusterValue = 5 * indice;
			} else if (value <= 10 * indice) {
				clusterValue = 10 * indice;
			}
			indice *= 10;
		}
		clusterValue = Math.max(clusterValue, minMax[0]); //check min 
		clusterValue = Math.min(clusterValue, minMax[1]); //check max

		sb.append(measureType).append("_C").append(clusterValue);
		final String clusterMeasureName = sb.toString();
		sb.setLength(0);
		addMetric(clusterMeasureName, 1, cubeBuilder, whatModulePositionValue, parentCubeBuilders, sb);

	}

	private static boolean isClimbingMetric(final String measureName) {
		return true;
	}

	private static boolean isClusteredMetric(final String measureName) {
		return !measureName.contains("_C") && measureName.contains(KProcess.DURATION);
	}

	private static long[] getClusteredMetricMinMax(final String measureName) {
		if (measureName.contains(KProcess.DURATION)) {
			return new long[] { 100, 10000 };
		}
		return new long[] { 1, 100000 };
	}
}
