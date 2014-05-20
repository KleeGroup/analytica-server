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

/**
 * Builder permettant de contruire une metric.
 * Une métric est une mesure agrégée.
 * 
 * @author npiedeloup
 */
public final class HMetricBuilder implements Builder<HMetric> {
	private final HMetricKey metricKey;
	private long count = 0;
	private double min = Double.NaN; //Par défaut pas de min
	private double max = Double.NaN; //Par défaut pas de max
	private double sum = 0;
	private double sqrSum = 0;
	private final HDistributionBuilder distributionBuilder;

	/**
	 * Constructeur.
	 * @param metricName Nom de la metric
	 */
	public HMetricBuilder(final HMetricKey metricKey) {
		Assertion.checkNotNull(metricKey);
		//---------------------------------------------------------------------
		this.metricKey = metricKey;
		distributionBuilder = metricKey.hasDistribution() ? new HDistributionBuilder() : null;
	}

	public HMetricKey getMetricKey() {
		return metricKey;
	}
	/**
	 * Add value.
	 * @param value Value to add 
	 * @return MetricBuilder builder
	 */
	public HMetricBuilder withValue(final double value) {
		count++;
		max = max(max, value);
		min = min(min, value);
		sum += value;
		sqrSum += value * value;
		if (distributionBuilder != null) {
			distributionBuilder.withValue(value);
		}
		return this;
	}

	/**
	 * Ajout d'une metric. 
	 * @param metric Metric
	 * @return MetricBuilder builder
	 */
	public HMetricBuilder withMetric( final HMetric metric) {
		Assertion.checkNotNull(metric);
		Assertion.checkArgument(distributionBuilder == null ^ !(metric.getDistribution() == null), "La notion de cluster doit être homogène sur les clés {0}", metricKey);
		//---------------------------------------------------------------------
		count += metric.get(HCounterType.count);
		max = max(max, metric.get(HCounterType.max));
		min = min(min, metric.get(HCounterType.min));
		sum += metric.get(HCounterType.sum);
		sqrSum += metric.get(HCounterType.sqrSum);
		//---------------------------------------------------------------------
		if (distributionBuilder != null) {
			distributionBuilder.withDistribution(metric.getDistribution());
		}
		return this;
	}

	/** 
	 * Construction de la Metric du cube.
	 * @return Metric du cube
	 */
	public HMetric build() {
		Assertion.checkArgument(count > 0, "Aucune valeur ajoutée à cette métric {0}, impossible de la créer.", metricKey);
		//---------------------------------------------------------------------
		return new HMetric( count, min, max, sum, sqrSum, distributionBuilder == null ? null : distributionBuilder.build());
	}

	private static double max(final double d1, final double d2) {
		//Math.max has low perfs
		return Double.isNaN(d1) ? d2 : Double.isNaN(d2) ? d1 : d1 > d2 ? d1 : d2;
	}

	private static double min(final double d1, final double d2) {
		//Math.max has low perfs
		return Double.isNaN(d1) ? d2 : Double.isNaN(d2) ? d1 : d1 < d2 ? d1 : d2;
	}

}
