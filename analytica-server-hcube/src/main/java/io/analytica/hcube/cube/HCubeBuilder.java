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
package io.analytica.hcube.cube;

import io.vertigo.kernel.lang.Assertion;
import io.vertigo.kernel.lang.Builder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Builder permettant de contruire un cube.
 *
 * @author npiedeloup, pchretien
 */
public final class HCubeBuilder implements Builder<HCube> {
	private final Map<HMetricKey, HMetricBuilder> metricBuilders = new HashMap<>();

	/**
	 * Ajout d'une metric. 
	 * @param metric Metric
	 */
	public HCubeBuilder withMetric(final HMetricKey metricKey, HMetric metric) {
		Assertion.checkNotNull(metricKey);
		Assertion.checkNotNull(metric);
		//---------------------------------------------------------------------
		HMetricBuilder metricBuilder = metricBuilders.get(metricKey);
		if (metricBuilder == null) {
			metricBuilder = new HMetricBuilder(metricKey);
			metricBuilders.put(metricKey, metricBuilder);
		}
		//On ajoute metric
		metricBuilder.withMetric(metric);
		return this;
	}

	//	/**
	//	 * Ajout de ttes les Metrics. 
	//	 * @param Metrics  
	//	 */
	//	public HCubeBuilder withMetrics(final Map<HMetricKey, HMetric> metrics) {
	//		Assertion.checkNotNull(metrics);
	//		//---------------------------------------------------------------------
	//		for (final Entry<HMetricKey, HMetric> entry : metrics.entrySet()) {
	//			withMetric(entry.getKey(), entry.getValue());
	//		}
	//		return this;
	//	}

	/** 
	 * Construction du Cube.
	 * @return cube
	 */
	public HCube build() {
		final Map<HMetricKey, HMetric> metrics = new LinkedHashMap<>(metricBuilders.size());
		for (final Entry<HMetricKey, HMetricBuilder> entry : metricBuilders.entrySet()) {
			HMetric metric = entry.getValue().build();
			metrics.put(entry.getKey(), metric);
		}
		return new HCube(metrics);
	}
}
