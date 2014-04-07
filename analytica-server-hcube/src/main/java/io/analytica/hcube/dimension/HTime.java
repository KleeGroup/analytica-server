/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidère - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.hcube.dimension;

import io.vertigo.kernel.lang.Assertion;

import java.util.Date;

/**
 * HTime represents the time axis.
 * Time is a continuous value, htime is a discrete value ; this allows to aggregate all the values in the same time range.
 * 
 * htime is defined by 
 *  - a date (inMillis)
 *  - a timeDimension (minute, hour, .... or year) 
 * 
 * @author npiedeloup, pchretien
 */
public final class HTime implements HPosition<HTime> {
	private final HTimeDimension timeDimension;
	private final long time;

	public HTime(final Date date, final HTimeDimension timeDimension) {
		this(date.getTime(), timeDimension);
	}

	HTime(final long time, final HTimeDimension timeDimension) {
		Assertion.checkNotNull(timeDimension);
		//---------------------------------------------------------------------
		this.time = timeDimension.reduce(time);
		this.timeDimension = timeDimension;
	}

	/** {@inheritDoc} */
	public HTimeDimension getDimension() {
		return timeDimension;
	}

	public long inMillis() {
		return time;
	}

	@Override
	public final int hashCode() {
		return Long.valueOf(time).hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		if (object instanceof HTime) {
			final HTime other = (HTime) object;
			return time == other.time && timeDimension.equals(other.timeDimension);
		}
		return false;
	}

	/** {@inheritDoc} */
	public HTime drillUp() {
		return timeDimension.drillUp(time);
	}

	/** {@inheritDoc} */
	public HTime next() {
		return timeDimension.next(time);
	}

	@Override
	public String toString() {
		return new Date(time).toString();
	}
}
