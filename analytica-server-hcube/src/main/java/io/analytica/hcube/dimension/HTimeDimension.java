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

	//	/**
	//	 * @return Niveau inférieur ou null.
	//	 */
	//	public TimeDimension drillDown() {
	//		switch (this) {
	//			case Year:
	//				return Month;
	//			case Month:
	//				return Day;
	//			case Day:
	//				return Hour;
	//			case Hour:
	//				return Minute;
	//			case Minute:
	//				return null;
	//			default:
	//				throw new KRuntimeException("TimeDimension inconnu");
	//		}
	//	}
	//
	//	/**
	//	 * Retourne la date maximum pour cette dimenssion.
	//	 * @param date Date de départ
	//	 * @return Date maximum
	//	 */
	//	public Date getMaxDate(final Date date) {
	//		Assertion.checkNotNull(date);
	//		final Date reduceDate = reduce(date);
	//		Assertion.checkArgument(reduceDate.equals(date), "La date de début doit déjà être réduite à cette dimenssion, et correspondre au point de début de cette dimenssion");
	//		//---------------------------------------------------------------------
	//		final Calendar calendar = Calendar.getInstance();
	//		calendar.setTime(reduceDate);
	//		switch (this) {
	//			case Year:
	//				calendar.add(Calendar.YEAR, 1);
	//				break;
	//			case Month:
	//				calendar.add(Calendar.MONTH, 1);
	//				break;
	//			case Day:
	//				calendar.add(Calendar.DAY_OF_YEAR, 1);
	//				break;
	//			case Hour:
	//				calendar.add(Calendar.HOUR_OF_DAY, 1);
	//				break;
	//			case Minute:
	//				calendar.add(Calendar.MINUTE, 1);
	//				break;
	//		}
	//		return calendar.getTime();
	//	}

	/**
	 * Normalise la valeur pour correspondre au niveau d'agregation de cette dimension.
	 * @param date Valeur
	 * @return Valeur normalisée
	 */
	public Date reduce(final Date date) {
		Assertion.checkNotNull(date);
		//---------------------------------------------------------------------
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final int minute = calendar.get(Calendar.MINUTE);
		switch (this) {
			case Year:
				calendar.set(Calendar.MONTH, 0);
				//$FALL-THROUGH$
			case Month:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				//$FALL-THROUGH$
			case Day:
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				//$FALL-THROUGH$
			case Hour:
				calendar.set(Calendar.MINUTE, 0);
				//$FALL-THROUGH$
			case SixMinutes:
				calendar.set(Calendar.MINUTE, minute - minute % 6);
				//$FALL-THROUGH$				
			case Minute:
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				//$FALL-THROUGH$
			default:
				return calendar.getTime();
		}
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
