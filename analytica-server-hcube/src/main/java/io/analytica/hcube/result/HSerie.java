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
package io.analytica.hcube.result;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.cube.HMetricBuilder;
import io.analytica.hcube.cube.HVirtualCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HTime;
import io.vertigo.kernel.lang.Assertion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Résultat d'une série.
 * Une série est un parallélépipède continu pour une catégorie donnée, qui peut se voir comme autant un cube virtuel.
 * 
 * @author pchretien, npiedeloup
 */
public final class HSerie implements HVirtualCube {
	private final List<HCategory> categories;
	private final Map<HTime, HCube> cubes;
	private Map<String, HMetric> metrics;

	/**
	 * Constructeur.
	 * @param category Catégorie de la série
	 * @param cubes Liste ordonnée des élements du parallélépipède
	 */
	public HSerie(final List<HCategory> categories, final Map<HTime, HCube> cubes) {
		Assertion.checkNotNull(categories);
		Assertion.checkNotNull(cubes);
		//---------------------------------------------------------------------
		this.categories = categories;
		this.cubes = cubes;
		metrics = buildMetrics(cubes.values());
	}

	/**
	 * @return Category de la série
	 */
	public List<HCategory> getCategories() {
		return categories;
	}

	/**
	 * @return Liste ordonnée des élements du parallélépipède
	 */
	public Map<HTime, HCube> getCubes() {
		return Collections.unmodifiableMap(cubes);
	}

	//-------------------------------------------------------------------------
	private static Map<String, HMetric> buildMetrics(Collection<HCube> cubes) {
		Map<String, HMetric> metrics = new HashMap<>();
		final Map<String, HMetricBuilder> metricBuilders = new HashMap<>();
		for (final HCube cube : cubes) {
			for (final HMetric metric : cube.getMetrics()) {
				HMetricBuilder metricBuilder = metricBuilders.get(metric.getName());
				if (metricBuilder == null) {
					metricBuilder = new HMetricBuilder(metric.getName());
					metricBuilders.put(metric.getName(), metricBuilder);
				}
				metricBuilder.withMetric(metric);
			}
		}
		for (final Entry<String, HMetricBuilder> entry : metricBuilders.entrySet()) {
			metrics.put(entry.getKey(), entry.getValue().build());
		}
		return metrics;
	}

	/** {@inheritDoc} */
	public HMetric getMetric(final String metricName) {
		Assertion.checkNotNull(metricName);
		//---------------------------------------------------------------------
		return metrics.get(metricName);
	}

	/** {@inheritDoc} */
	public Collection<HMetric> getMetrics() {
		return metrics.values();
	}

	public List<HPoint> getPoints(final String metricName) {
		final List<HPoint> points = new ArrayList<>();
		for (final Entry<HTime, HCube> entry : cubes.entrySet()) {
			points.add(new HPoint() {
				/** {@inheritDoc} */
				public HMetric getMetric() {
					return entry.getValue().getMetric(metricName);
				}

				/** {@inheritDoc} */
				public Date getDate() {
					return new Date(entry.getKey().inMillis());
				}
			});
		}
		return Collections.unmodifiableList(points);
	}

	@Override
	public String toString() {
		return "{categories:" + categories + ", cubes" + cubes + "}";
	}
}
