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
 * @version $Id: MetricBuilder.java,v 1.3 2012/10/16 12:53:40 pchretien Exp $
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
			clusteredValues = new HashMap<Double, Long>();
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
			clusterValue(value);
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
		return Double.isNaN(d1) ? d2 : Double.isNaN(d2) ? d1 : Math.max(d1, d2);
	}

	private static double min(final double d1, final double d2) {
		return Double.isNaN(d1) ? d2 : Double.isNaN(d2) ? d1 : Math.min(d1, d2);
	}

	//-----------------------------------------------------------------------------------
	//---------------------------Cluster-------------------------------------------------
	//-----------------------------------------------------------------------------------
	private final Map<Double, Long> clusteredValues;

	private void incTreshold(final double treshold, final long incBy) {
		final Long count = clusteredValues.get(treshold);
		clusteredValues.put(treshold, incBy + (count == null ? 0 : count));
		//---
	}

	private void clusterValue(final double value) {
		//On crée une répartion : 1, 2, 5 - 10, 20, 50 - 100, 200, 500...
		if (value <= 0) {
			incTreshold(0, 1);
		} else {
			final double index = Math.floor(Math.log10(value));
			final double treshold = Math.pow(10, index);
			if (value <= treshold) {
				incTreshold(treshold, 1);
			} else if (value <= 2 * treshold) {
				incTreshold(2 * treshold, 1);
			} else if (value <= 5 * treshold) {
				incTreshold(5 * treshold, 1);
			} else {
				incTreshold(10 * treshold, 1);
			}
		}
	}
}
