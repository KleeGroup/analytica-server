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

import io.vertigo.kernel.lang.Assertion;

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

	private final HTimeDimension up;

	/**
	 * Constructeur.
	 * @param up Niveau supérieur
	 */
	HTimeDimension(final HTimeDimension up) {
		this.up = up;
	}

	/**
	 * @return Niveau supérieur ou null.
	 */
	public HTimeDimension drillUp() {
		return up;
	}

	/**
	 * Normalise la valeur pour correspondre au niveau d'agregation de cette dimension.
	 * @param date Valeur
	 * @return Valeur normalisée
	 */
	public Date reduce(final Date date) {
		Assertion.checkNotNull(date);
		//---------------------------------------------------------------------
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
			final long reducedTime = ((date.getTime() / divide) * divide); //we truncate time for this dimension
			return new Date(reducedTime);
		}

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
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

		return calendar.getTime();
	}

	public String getPattern() {
		switch (this) {
			case Minute:
			case SixMinutes:
				return "yyyy/MM/dd HH:mm";
			case Hour:
				return "yyyy/MM/dd HH";
			case Day:
				return "yyyy/MM/dd";
			case Month:
				return "yyyy/MM";
			case Year:
				return "yyyy";
			default:
				throw new RuntimeException("TimeDimension inconnu : " + name());
		}
	}
}
