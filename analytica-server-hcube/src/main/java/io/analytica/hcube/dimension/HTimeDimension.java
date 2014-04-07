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

import io.vertigo.kernel.lang.DateBuilder;

import java.util.Calendar;
import java.util.Date;

/**
 * Niveaux heures, minutes ou secondes de la dimension hiérarchique temps.
 *
 * @author npiedeloup, pchretien
 * @version $Id: TimeDimension.java,v 1.3 2012/10/16 16:25:22 pchretien Exp $
 */
public enum HTimeDimension {
	/** 
	 * Année.
	 */
	Year(null),
	/**
	 * Mois.
	 */
	Month(Year),
	/**
	 * Jour.
	 */
	Day(Month),
	/**
	 * Heure.
	 */
	Hour(Day),
	/**
	 * 6 Minutes.
	 */
	SixMinutes(Hour),
	/**
	 * Minute.
	 */
	Minute(SixMinutes);

	private final HTimeDimension upTimeDimension;

	/**
	 * Constructeur.
	 * @param up Niveau supérieur
	 */
	HTimeDimension(final HTimeDimension upTimeDimension) {
		this.upTimeDimension = upTimeDimension;
	}

	/**
	 * Normalise la valeur pour correspondre au niveau d'agregation de cette dimension.
	 * @param date Valeur
	 * @return Valeur normalisée
	 */
	public long reduce(final long time) {
		if (this == Hour || this == SixMinutes || this == Minute) {
			final long divide;
			switch (this) {
				case Hour:
					divide = 3600000; //60*60*1000
					break;
				case SixMinutes:
					divide = 360000; //6*60*1000
					break;
				case Minute:
					divide = 60000; //60*1000
					break;
				case Day:
				case Month:
				case Year:
				default:
					throw new IllegalArgumentException("Invalid dimension :" + this);
			}
			return (time / divide) * divide; //we truncate time for this dimension
		}

		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		switch (this) {
			case Year:
				calendar.set(Calendar.MONTH, 0);
				//$FALL-THROUGH$
			case Month:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				//$FALL-THROUGH$
			case Day:
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				break;
			case Hour:
			case Minute:
			case SixMinutes:
			default:
				throw new IllegalArgumentException("Invalid dimension :" + this);
		}

		return calendar.getTimeInMillis();
	}

	public HTime drillUp(final long time) {
		return upTimeDimension != null ? new HTime(time, upTimeDimension) : null;
	}

	public HTime next(final long time) {
		final Date currentDate = new Date(time); //could be optimized with vertigo 0.3.1 when released
		final Date nextDate;
		switch (this) {
			case Year:
				nextDate = new DateBuilder(currentDate).addYears(1).toDateTime();
				break;
			case Month:
				nextDate = new DateBuilder(currentDate).addMonths(1).toDateTime();
				break;
			case Day:
				nextDate = new DateBuilder(currentDate).addDays(1).toDateTime();
				break;
			case Hour:
				return new HTime(time + 3600000, this);
			case SixMinutes:
				return new HTime(time + 360000, this);
			case Minute:
				return new HTime(time + 60000, this);
			default:
				throw new IllegalArgumentException("Invalid dimension :" + this);
		}
		return new HTime(nextDate, this);
	}
}
