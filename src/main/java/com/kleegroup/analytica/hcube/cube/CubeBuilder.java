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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.CubePosition;

/**
 * Builder permettant de contruire un cube.
 *
 * @author npiedeloup, pchretien
 * @version $Id: CubeBuilder.java,v 1.6 2012/11/08 17:06:41 pchretien Exp $
 */
public final class CubeBuilder implements Builder<Cube> {
	private final CubePosition cubePosition;
	private final Map<String, MetricBuilder> metrics = new HashMap<String, MetricBuilder>();
	private final Set<MetaData> metaDatas = new HashSet<MetaData>(500);

	/**
	 * Constructeur.
	 * @param cubeKey Identifiant du cube
	 */
	public CubeBuilder(CubePosition cubePosition) {
		Assertion.notNull(cubePosition);
		//---------------------------------------------------------------------
		this.cubePosition = cubePosition;
	}

	/**
	 * Ajout d'une metric. 
	 * @param metric Metric
	 */
	public CubeBuilder withMetric(final Metric metric) {
		Assertion.notNull(metric);
		//---------------------------------------------------------------------
		MetricBuilder metricBuilder = metrics.get(metric.getName());
		if (metricBuilder == null) {
			metricBuilder = new MetricBuilder(metric.getName());
			metrics.put(metric.getName(), metricBuilder);
		}
		//On ajoute metric
		metricBuilder.withMetric(metric);
		return this;
	}

	/** 
	 * Ajout d'une meta-donnée.
	 * @param metaData meta-donnée
	 */
	public CubeBuilder withMetaData(final MetaData metaData) {
		Assertion.notNull(metaData);
		//---------------------------------------------------------------------
		metaDatas.add(metaData);
		return this;
	}

	/**
	 * Ajout d'un cube. 
	 * @param cube Cube
	 */
	public CubeBuilder withCube(final Cube cube) {
		Assertion.notNull(cube);
		//Assertion util mais 50% des perfs !!
		Assertion.precondition(cubePosition.contains(cube.getPosition()), "On ne peut merger que des cubes sur la même clée (builder:{0} != cube:{1}) ou d'une dimension inférieur au builder", cubePosition, cube.getPosition());
		//---------------------------------------------------------------------
		for (final Metric metric : cube.getMetrics()) {
			withMetric(metric);
		}
		for (final MetaData metaData : cube.getMetaDatas()) {
			withMetaData(metaData);
		}
		return this;
	}

	/** 
	 * Construction du Cube.
	 * @return cube
	 */
	public Cube build() {
		final List<Metric> list = new ArrayList<Metric>(metrics.size());
		for (final MetricBuilder metricBuilder : metrics.values()) {
			list.add(metricBuilder.build());
		}
		return new Cube(cubePosition, list, metaDatas);
	}
}
