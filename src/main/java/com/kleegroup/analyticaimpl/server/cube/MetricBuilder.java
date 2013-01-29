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
package com.kleegroup.analyticaimpl.server.cube;

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
	private long count;
	private double min;
	private double max;
	private double sum;
	private double sqrSum;

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
	 * Ajout d'une valeur.
	 * @param metric Metric à compléter 
	 */
	public MetricBuilder withValue(final double value) {
		count++;
		max = Math.max(max, value);
		min = Math.max(min, value);
		sum += value;
		sqrSum += value * value;
		return this;
	}

	/**
	 * Ajout d'une metric. 
	 * @param metric Metric
	 */
	public MetricBuilder withMetric(final Metric metric) {
		Assertion.notNull(metric);
		Assertion.precondition(metricName.equals(metric.getName()), "On ne peut merger que des metrics indentiques ({0} != {1})", metricName, metric.getName());
		//---------------------------------------------------------------------
		count += metric.get(Metric.F_COUNT);
		max = Math.max(max, metric.get(Metric.F_MAX));
		min = Math.min(min, metric.get(Metric.F_MIN));
		sum += metric.get(Metric.F_SUM);
		sqrSum += metric.get(Metric.F_SQR_SUM);
		return this;
	}

	/** 
	 * Construction du Cube.
	 * @return cube
	 */
	public Metric build() {
		return new Metric(metricName, count, min, max, sum, sqrSum);
	}
}
