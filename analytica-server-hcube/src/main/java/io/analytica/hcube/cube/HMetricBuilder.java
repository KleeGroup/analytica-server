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
import java.util.Map;
import java.util.Map.Entry;

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

	/**
	 * Constructeur.
	 * @param metricName Nom de la metric
	 */
	public HMetricBuilder(final HMetricKey metricKey) {
		Assertion.checkNotNull(metricKey);
		//---------------------------------------------------------------------
		this.metricKey = metricKey;
		if (metricKey.isClustered()) {
			clusteredValues = new HashMap<>();
		} else {
			clusteredValues = null;
		}
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
		if (metricKey.isClustered()) {
			incTreshold(clusterValue2(value), 1);
		}
		return this;
	}

	/**
	 * Ajout d'une metric. 
	 * @param metric Metric
	 * @return MetricBuilder builder
	 */
	public HMetricBuilder withMetric(final HMetric metric) {
		Assertion.checkNotNull(metric);
		Assertion.checkArgument(metricKey.equals(metric.getKey()), "On ne peut merger que des metrics indentiques ({0} != {1})", metricKey, metric.getKey());
		Assertion.checkArgument(metricKey.isClustered() ^ !metric.getKey().isClustered(), "La notion de cluster doit être homogène sur les clés {0}", metricKey);
		//---------------------------------------------------------------------
		count += metric.get(HCounterType.count);
		max = max(max, metric.get(HCounterType.max));
		min = min(min, metric.get(HCounterType.min));
		sum += metric.get(HCounterType.sum);
		sqrSum += metric.get(HCounterType.sqrSum);
		//---------------------------------------------------------------------
		if (metricKey.isClustered()) {
			for (final Entry<Double, Long> entry : metric.getClusteredValues().entrySet()) {
				incTreshold(entry.getKey(), entry.getValue());
			}
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
		return new HMetric(metricKey, count, min, max, sum, sqrSum, clusteredValues);
	}

	private static double max(final double d1, final double d2) {
		//Math.max has low perfs
		return Double.isNaN(d1) ? d2 : Double.isNaN(d2) ? d1 : d1 > d2 ? d1 : d2;
	}

	private static double min(final double d1, final double d2) {
		//Math.max has low perfs
		return Double.isNaN(d1) ? d2 : Double.isNaN(d2) ? d1 : d1 < d2 ? d1 : d2;
	}

	//-----------------------------------------------------------------------------------
	//---------------------------Cluster-------------------------------------------------
	//-----------------------------------------------------------------------------------
	private final Map<Double, Long> clusteredValues;

	private void incTreshold(final double treshold, final long incBy) {
		final Long hcount = clusteredValues.get(treshold);
		clusteredValues.put(treshold, incBy + (hcount == null ? 0 : hcount));
	}

	private double clusterValue2(final double value) {
		//On crée une répartion : 1, 2, 5 - 10, 20, 50 - 100, 200, 500...
		//Optim 
		if (value <= 0)
			return 0;
		if (value <= 1)
			return 1;
		if (value <= 2)
			return 2;
		if (value <= 5)
			return 5;
		if (value <= 10)
			return 10;
		if (value <= 20)
			return 20;
		if (value <= 50)
			return 50;
		if (value <= 100)
			return 100;
		if (value <= 200)
			return 200;
		if (value <= 500)
			return 500;
		if (value <= 1000)
			return 1000;
		if (value <= 2000)
			return 2000;
		if (value <= 5000)
			return 5000;
		//Other cases
		final double index = Math.floor(Math.log10(value));
		final double treshold = Math.pow(10, index);
		if (value <= treshold) {
			return treshold;
		} else if (value <= 2 * treshold) {
			return 2 * treshold;
		} else if (value <= 5 * treshold) {
			return 5 * treshold;
		}
		return 10 * treshold;
	}
}
