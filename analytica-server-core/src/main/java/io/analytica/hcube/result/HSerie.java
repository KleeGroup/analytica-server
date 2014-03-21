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
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.cube.HVirtualCube;
import io.analytica.hcube.dimension.HCategory;
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
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public final class HSerie implements HVirtualCube {
	private final HCategory category;
	private final List<HCube> cubes;
	private Map<HMetricKey, HMetric> metrics; //lazy

	/**
	 * Constructeur.
	 * @param category Catégorie de la série
	 * @param cubes Liste ordonnée des élements du parallélépipède
	 */
	public HSerie(final HCategory category, final List<HCube> cubes) {
		Assertion.checkNotNull(category);
		Assertion.checkNotNull(cubes);
		//---------------------------------------------------------------------
		this.category = category;
		this.cubes = cubes;
	}

	/**
	 * @return Category de la série
	 */
	public HCategory getCategory() {
		return category;
	}

	/**
	 * @return Liste ordonnée des élements du parallélépipède
	 */
	public List<HCube> getCubes() {
		Assertion.checkNotNull(category);
		//-------------------------------------------------------------------------
		return Collections.unmodifiableList(cubes);
	}

	//-------------------------------------------------------------------------
	private Map<HMetricKey, HMetric> getLazyMetrics() {
		if (metrics == null) {
			final Map<HMetricKey, HMetricBuilder> metricBuilders = new HashMap<>();
			for (final HCube cube : cubes) {
				for (final HMetric metric : cube.getMetrics()) {
					HMetricBuilder metricBuilder = metricBuilders.get(metric.getKey());
					if (metricBuilder == null) {
						metricBuilder = new HMetricBuilder(metric.getKey());
						metricBuilders.put(metric.getKey(), metricBuilder);
					}
					metricBuilder.withMetric(metric);
				}
			}
			metrics = new HashMap<>();
			for (final Entry<HMetricKey, HMetricBuilder> entry : metricBuilders.entrySet()) {
				metrics.put(entry.getKey(), entry.getValue().build());
			}
		}
		return metrics;
	}

	/** {@inheritDoc} */
	public HMetric getMetric(final HMetricKey metricKey) {
		Assertion.checkNotNull(metricKey);
		//---------------------------------------------------------------------
		return getLazyMetrics().get(metricKey);
	}

	/** {@inheritDoc} */
	public Collection<HMetric> getMetrics() {
		return getLazyMetrics().values();
	}

	public List<HPoint> getPoints(final HMetricKey metricKey) {
		final List<HPoint> points = new ArrayList<>();
		for (final HCube cube : cubes) {
			points.add(new HPoint() {
				/** {@inheritDoc} */
				public HMetric getMetric() {
					return cube.getMetric(metricKey);
				}

				/** {@inheritDoc} */
				public Date getDate() {
					return cube.getKey().getTime().getValue();
				}
			});
		}
		return Collections.unmodifiableList(points);
	}
}
