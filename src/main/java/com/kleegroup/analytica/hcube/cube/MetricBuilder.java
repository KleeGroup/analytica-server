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

import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

/**
 * Builder permettant de contruire une metric.
 * Une métric est une mesure agrégée.
 * 
 * @author npiedeloup
 * @version $Id: MetricBuilder.java,v 1.3 2012/10/16 12:53:40 pchretien Exp $
 */
public final class MetricBuilder implements Builder<Metric> {
	private final String metricName;
	private long count = 0;
	private double min = Double.NaN;
	private double max = Double.NaN;
	private double sum = 0;
	private double sqrSum = 0;

	/**
	 * Constructeur.
	 * @param metricName Nom de la metric
	 */
	public MetricBuilder(final String metricName) {
		Assertion.notEmpty(metricName);
		//---------------------------------------------------------------------
		this.metricName = metricName;
	}

	/**
	 * Add value.
	 * @param value Value to add 
	 * @return MetricBuilder builder
	 */
	public MetricBuilder withValue(final double value) {
		count++;
		max = max(max, value);
		min = min(min, value);
		sum += value;
		sqrSum += value * value;
		return this;
	}

	/**
	 * Ajout d'une metric. 
	 * @param metric Metric
	 * @return MetricBuilder builder
	 */
	public MetricBuilder withMetric(final Metric metric) {
		Assertion.notNull(metric);
		Assertion.precondition(metricName.equals(metric.getName()), "On ne peut merger que des metrics indentiques ({0} != {1})", metricName, metric.getName());
		//---------------------------------------------------------------------
		count += metric.get(Metric.F_COUNT);
		max = max(max, metric.get(Metric.F_MAX));
		min = min(min, metric.get(Metric.F_MIN));
		sum += metric.get(Metric.F_SUM);
		sqrSum += metric.get(Metric.F_SQR_SUM);
		return this;
	}

	/** 
	 * Construction de la Metric du cube.
	 * @return Metric du cube
	 */
	public Metric build() {
		Assertion.precondition(count > 0, "Aucune valeur ajoutée à cette métric {0}, impossible de la créer.", metricName);
		//---------------------------------------------------------------------
		return new Metric(metricName, count, min, max, sum, sqrSum);
	}

	private static double max(double d1, double d2) {
		return Double.isNaN(d1) ? d2 : Double.isNaN(d2) ? d1 : Math.max(d1, d2);
	}

	private static double min(double d1, double d2) {
		return Double.isNaN(d1) ? d2 : Double.isNaN(d2) ? d1 : Math.min(d1, d2);
	}
}
