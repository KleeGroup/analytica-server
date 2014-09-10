/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidi�re - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HTimeDimension;
import io.vertigo.kernel.lang.Assertion;
import io.vertigo.kernel.lang.Builder;

import java.util.Date;

/**
 * Builder de la requ�te.
 * @author npiedeloup, pchretien
 */
public final class HQueryBuilder implements Builder<HQuery> {
	private String hType;
	private HTimeDimension hTimeDimension;
	private Date from;
	private Date to;
	//----
	private String categoryPattern;

	//	private boolean categoryChildren;

	public HQueryBuilder onType(final String type) {
		Assertion.checkNotNull(type);
		Assertion.checkState(this.hType == null, "type already set");
		//---------------------------------------------------------------------
		this.hType = type;
		return this;
	}

	public HQueryBuilder on(final HTimeDimension timeDimension) {
		Assertion.checkNotNull(timeDimension);
		Assertion.checkState(hTimeDimension == null, "timeDimension already set");
		//---------------------------------------------------------------------
		hTimeDimension = timeDimension;
		return this;
	}

	/**
	 * @param date lower selection (INCLUDED)
	 * @param date upper selection (EXCLUDED)
	 * @return HQueryBuilder
	 */
	public HQueryBuilder between(final String startdate, final String endDate) {
		return from(startdate).to(endDate);
	}

	/**
	 * @param date lower selection (INCLUDED)
	 * @param date upper selection (EXCLUDED)
	 * @return HQueryBuilder
	 */
	public HQueryBuilder between(final Date startdate, final Date endDate) {
		return from(startdate).to(endDate);
	}

	/**
	* @param date lower selection (INCLUDED)
	* @return HQueryBuilder
	*/
	private HQueryBuilder from(final String date) {
		Assertion.checkNotNull(hTimeDimension);
		//---------------------------------------------------------------------
		return from(readDate(date, hTimeDimension));
	}

	/**
	 * @param date lower selection (INCLUDED)
	 * @return HQueryBuilder
	 */
	private HQueryBuilder from(final Date date) {
		Assertion.checkNotNull(date);
		Assertion.checkState(from == null, "Date From already set");
		//---------------------------------------------------------------------
		from = date;
		return this;
	}

	/**
	 * @param date upper selection (EXCLUDED)
	 * @return HQueryBuilder
	 */
	private HQueryBuilder to(final Date date) {
		Assertion.checkNotNull(date);
		Assertion.checkState(to == null, "Date To already set");
		//---------------------------------------------------------------------
		to = date;
		return this;
	}

	/**
	 * @param date date upper selection (EXCLUDED)
	 * @return
	 */
	private HQueryBuilder to(final String date) {
		Assertion.checkNotNull(hTimeDimension);
		//---------------------------------------------------------------------
		return to(readDate(date, hTimeDimension));
	}

	public HQueryBuilder whereCategoryMatches(final String pattern) {
		Assertion.checkArgNotEmpty(pattern);
		Assertion.checkState(this.categoryPattern == null, "category's pattern is already set");
		//---------------------------------------------------------------------
		categoryPattern = pattern;
		return this;
	}

	/**
	 *
	 * @param timeStr : e.g: NOW+1h
	 * @param dimension : Dimension temporelle : ann�e/mois/jour/...
	 * @return Date obtenue � partir des deux indications pr�cedentes
	 */
	private static Date readDate(final String timeStr, final HTimeDimension dimension) {
		Assertion.checkArgNotEmpty(timeStr);
		// ---------------------------------------------------------------------
		if ("NOW".equals(timeStr)) {
			return new Date();
		} else if (timeStr.startsWith("NOW-")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW-".length()));
			return new Date(System.currentTimeMillis() - deltaMs);
		} else if (timeStr.startsWith("NOW+")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW+".length()));
			return new Date(System.currentTimeMillis() + deltaMs);
		}
		throw new RuntimeException("time must be NOW or NOW+999f or NOW-999f where f is h|d|m");

		//		final SimpleDateFormat sdf = new SimpleDateFormat(dimension.getPattern());
		//
		//		try {
		//			return sdf.parse(timeStr);
		//		} catch (final ParseException e) {
		//			throw new RuntimeException("Erreur de format de date (" + timeStr + "). Format attendu :" + sdf.toPattern());
		//		}
	}

	/**
	 *
	 * @param deltaAsString
	 * @return delta en millisecondes
	 */
	private static long readDeltaAsMs(final String deltaAsString) {
		final Long delta;
		char unit = deltaAsString.charAt(deltaAsString.length() - 1);

		if (unit >= '0' && unit <= '9') {
			unit = 'd';
			delta = Long.valueOf(deltaAsString);
		} else {
			delta = Long.valueOf(deltaAsString.substring(0, deltaAsString.length() - 1));
		}

		switch (unit) {
			case 'd'://day
				return delta * 24 * 60 * 60 * 1000L;

			case 'h'://hour
				return delta * 60 * 60 * 1000L;

			case 'm': //minute
				return delta * 60 * 1000L;

			default:
				throw new RuntimeException("La dur�e doit pr�ciser l'unit� de temps utilis�e : d=jour, h=heure, m=minute");
		}
	}

	/** {@inheritDoc} */
	public HQuery build() {
		if (categoryPattern == null) {
			categoryPattern = "*";
		}
		return new HQuery(hType, new HTimeSelection(hTimeDimension, from, to), new HCategorySelection(categoryPattern));
	}
}
