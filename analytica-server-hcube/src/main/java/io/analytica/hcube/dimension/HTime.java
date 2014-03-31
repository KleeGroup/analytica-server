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
package io.analytica.hcube.dimension;

import io.analytica.hcube.HKey;
import io.vertigo.kernel.lang.DateBuilder;

import java.util.Date;

/**
 * @author npiedeloup
 * @version $Id: TimePosition.java,v 1.2 2012/04/17 09:11:15 pchretien Exp $
 */
public final class HTime extends HKey<Date> implements HPosition<HTime> {
	private final HTimeDimension dimension;

	public HTime(final Date date, final HTimeDimension timeDimension) {
		super(timeDimension.reduce(date));
		//---------------------------------------------------------------------
		dimension = timeDimension;
	}

	/** {@inheritDoc} */
	public HTime drillUp() {
		final HTimeDimension upTimeDimension = dimension.drillUp();
		return upTimeDimension != null ? new HTime(this.id(), upTimeDimension) : null;
	}

	/** {@inheritDoc} */
	public HTimeDimension getDimension() {
		return dimension;
	}

	public Date getValue() {
		return id();
	}

	public HTime next() {
		final Date nextDate;
		switch (dimension) {
			case Year:
				nextDate = new DateBuilder(id()).addYears(1).toDateTime();
				break;
			case Month:
				nextDate = new DateBuilder(id()).addMonths(1).toDateTime();
				break;
			case Day:
				nextDate = new DateBuilder(id()).addDays(1).toDateTime();
				break;
			case Hour:
				nextDate = new DateBuilder(id()).addHours(1).toDateTime();
				break;
			case SixMinutes:
				nextDate = new DateBuilder(id()).addMinutes(6).toDateTime();
				break;
			case Minute:
				nextDate = new DateBuilder(id()).addMinutes(1).toDateTime();
				break;
			default:
				throw new RuntimeException("TimeDimension inconnu : " + dimension.name());
		}
		return new HTime(nextDate, dimension);
	}
}
