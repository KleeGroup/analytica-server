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
package com.kleegroup.analyticaimpl.server.plugins.cubestore.h2.bean;

import kasper.kernel.lang.Builder;

import com.kleegroup.analytica.hcube.cube.Metric;
import com.kleegroup.analytica.hcube.cube.MetricKey;

/**
 * Metric d'un cube.
 * Une metric possède un type et une valeur.
 *  
 * @author npiedeloup, pchretien
 * @version $Id: MetricBuilder.java,v 1.2 2012/03/22 09:16:40 npiedeloup Exp $
 */
public final class MetricBuilder implements Builder<Metric> {
	private long cubId;
	private String name;

	private long count;
	private double min;
	private double max;
	private double sum;
	private double sqrSum;

	/**
	 * @return Build de metric
	 */
	public Metric build() {
		return new Metric(new MetricKey(name), count, min, max, sum, sqrSum);
	}

	public final long getCubId() {
		return cubId;
	}

	public final void setCubId(final long cubId) {
		this.cubId = cubId;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final void setCount(final long count) {
		this.count = count;
	}

	public final void setMin(final double min) {
		this.min = min;
	}

	public final void setMax(final double max) {
		this.max = max;
	}

	public final void setSum(final double sum) {
		this.sum = sum;
	}

	public final void setSqrSum(final double sqrSum) {
		this.sqrSum = sqrSum;
	}
}
