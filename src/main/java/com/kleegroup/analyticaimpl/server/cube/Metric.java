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

import kasper.kernel.util.Assertion;

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
 * @version $Id: Metric.java,v 1.5 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class Metric {
	public static final String F_MEAN = "mean";
	public static final String F_STD_DEV = "stdDev";
	public static final String F_COUNT = "count";
	public static final String F_SQR_SUM = "sqrSum";
	public static final String F_SUM = "sum";
	public static final String F_MAX = "max";
	public static final String F_MIN = "min";

	private final String name;

	private final long count;
	private final double min;
	private final double max;
	private final double sum;
	private final double sqrSum;

	public Metric(final String name, final long count, final double min, final double max, final double sum, final double sqrSum) {
		Assertion.notEmpty(name);
		//---------------------------------------------------------------------
		this.name = name;
		this.count = count;
		this.min = min;
		this.max = max;
		this.sum = sum;
		this.sqrSum = sqrSum;
	}

	/**
	 * @return Nom de metric
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Moyenne
	 */
	private double getMean() {
		if (count > 0) {
			return sum / count;
		}
		return Double.NaN;
	}

	/**
	 * @return Ecart type
	 */
	private double getStandardDeviation() {
		if (count > 1) {
			// formule non exacte puisque qu'on ne connaît pas toutes les valeurs, mais estimation suffisante
			// rq : écart type (ou sigma) se dit standard deviation en anglais
			return Math.round(100 * Math.sqrt((sqrSum - sum * sum / count) / ((double) count - 1))) / 100d;
		}
		return Double.NaN;
	}

	public double get(final String fname) {
		if (F_MIN.equals(fname)) {
			return min;
		} else if (F_MAX.equals(fname)) {
			return max;
		} else if (F_SUM.equals(fname)) {
			return sum;
		} else if (F_SQR_SUM.equals(fname)) {
			return sqrSum;
		} else if (F_SUM.equals(fname)) {
			return sum;
		} else if (F_COUNT.equals(fname)) {
			return count;
		} else if (F_STD_DEV.equals(fname)) {
			return getStandardDeviation();
		} else if (F_MEAN.equals(fname)) {
			return getMean();
		}
		throw new IllegalArgumentException("Fonction inconnue : " + fname);
	}
}
