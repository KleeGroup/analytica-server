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
package com.kleegroup.analytica.hcube.cube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.HCubeKey;

/**
 * Builder permettant de contruire un cube.
 *
 * @author npiedeloup, pchretien
 * @version $Id: CubeBuilder.java,v 1.6 2012/11/08 17:06:41 pchretien Exp $
 */
public final class HCubeBuilder implements Builder<HCube> {
	private final HCubeKey cubeKey;
	private final Map<HMetricKey, HMetricBuilder> metrics = new HashMap<HMetricKey, HMetricBuilder>();

	/**
	 * Constructeur.
	 * @param cubeKey Identifiant du cube
	 */
	public HCubeBuilder(final HCubeKey cubeKey) {
		Assertion.notNull(cubeKey);
		//---------------------------------------------------------------------
		this.cubeKey = cubeKey;
	}

	/**
	 * Ajout d'une metric. 
	 * @param metric Metric
	 */
	public HCubeBuilder withMetric(final HMetric metric) {
		Assertion.notNull(metric);
		//---------------------------------------------------------------------
		HMetricBuilder metricBuilder = metrics.get(metric.getKey());
		if (metricBuilder == null) {
			metricBuilder = new HMetricBuilder(metric.getKey());
			metrics.put(metric.getKey(), metricBuilder);
		}
		//On ajoute metric
		metricBuilder.withMetric(metric);
		return this;
	}

	/**
	 * Ajout d'un cube. 
	 * @param cube Cube
	 */
	public HCubeBuilder withCube(final HCube cube) {
		Assertion.notNull(cube);
		//Assertion util mais 50% des perfs !!
		Assertion.precondition(cubeKey.contains(cube.getKey()), "On ne peut merger que des cubes sur la même clée (builder:{0} != cube:{1}) ou d'une dimension inférieur au builder", cubeKey, cube.getKey());
		//---------------------------------------------------------------------
		for (final HMetric metric : cube.getMetrics()) {
			withMetric(metric);
		}
		return this;
	}

	/** 
	 * Construction du Cube.
	 * @return cube
	 */
	public HCube build() {
		//---------------------------------------------------------------------
		final List<HMetric> list = new ArrayList<HMetric>(metrics.size());
		for (final HMetricBuilder metricBuilder : metrics.values()) {
			list.add(metricBuilder.build());
		}
		return new HCube(cubeKey, list);
	}
}
