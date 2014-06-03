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

/**
 * Metric.
 * La métric est le résultat issue de l'aggrégation 
 *  - de mesures 
 *  - de metric
 * 
 * Une metric est identifiée par son nom. 
 * Des metrics ne peuvent être aggrégés ensemble 
 * que si elles concernent la même entité c'est à dire possède un même nom.
 * 
 * @author npiedeloup, pchretien
 */
public final class HMetric {
	private final HDistribution distribution;

	private final long count;
	private final double min;
	private final double max;
	private final double sum;
	private final double sqrSum;

	public HMetric(final long count, final double min, final double max, final double sum, final double sqrSum, final HDistribution distribution) {
		//Assertion.checkArgument(metricKey.isClustered() ^ clusteredValues != null, "la metric {0} cluster doit avoir des données clusterisées", metricKey);
		//---------------------------------------------------------------------
		this.count = count;
		this.min = min;
		this.max = max;
		this.sum = sum;
		this.sqrSum = sqrSum;
		//---------------------------------------------------------------------
		this.distribution = distribution;
	}

	/**
	 * @return Ecart type
	 */
	private double getStandardDeviation() {
		if (count > 1) {
			// formule non exacte puisque qu'on ne connaît pas toutes les valeurs, mais estimation suffisante
			// rq : écart type (ou sigma) se dit standard deviation en anglais
			return Math.sqrt((sqrSum - sum * sum / count) / (count - 1d));
		}
		return Double.NaN;
	}

	public double get(final HCounterType dataType) {
		Assertion.checkNotNull(dataType);
		//---------------------------------------------------------------------
		switch (dataType) {
			case count:
				return count;
			case max:
				return max;
			case min:
				return min;
			case sum:
				return sum;
			case mean:
				return getMean();
			case sqrSum:
				return sqrSum;
			case stdDev:
				return getStandardDeviation();
			default:
				throw new IllegalArgumentException("Fonction inconnue : " + dataType);
		}
	}

	public long getCount() {
		return count;
	}

	/**
	 * @return Moyenne
	 */
	public double getMean() {
		if (count > 0) {
			return sum / count;
		}
		return Double.NaN;
	}

	public HDistribution getDistribution() {
		return distribution;
	}

	@Override
	public String toString() {
		return "{ count:" + count + ", mean:" + getMean() + ", min:" + get(HCounterType.min) + ", max:" + get(HCounterType.max) + (distribution == null ? " " : ", distribution:" + getDistribution()) + "}";
	}
}
