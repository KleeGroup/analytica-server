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
package com.kleegroup.analytica.server.query;

import java.util.Date;

import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup, pchretien
 * @version $Id: TimeSelection.java,v 1.5 2012/10/16 15:58:55 pchretien Exp $
 */
public final class TimeSelection implements Selection<TimeDimension> {
	private final Date minDate;
	private final Date maxDate;
	private final TimeDimension dimension;

	public TimeSelection(final TimeDimension dimension, final Date minDate, final Date maxDate) {
		Assertion.notNull(minDate);
		Assertion.notNull(maxDate);
		Assertion.precondition(minDate.before(maxDate), "la date min doit être inférieure à la date max");
		Assertion.notNull(dimension);
		//---------------------------------------------------------------------
		this.minDate = dimension.reduce(minDate);
		this.maxDate = dimension.reduce(maxDate);
		this.dimension = dimension;
	}

	public Date getMinValue() {
		return minDate;
	}

	public Date getMaxValue() {
		return maxDate;
	}

	/** {@inheritDoc} */
	public TimeDimension getDimension() {
		return dimension;
	}
}
