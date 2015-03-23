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
package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HTimeDimension;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.Builder;

import java.util.Date;

/**
 * Builder de la requête.
 * @author npiedeloup, pchretien
 */
public final class HQueryBuilder implements Builder<HQuery> {
	private HCategorySelection categorySelection;
	private HLocationSelection locationSelection;
	private HTimeSelection timeSelection;

	/**
	 * @param startdate lower selection (INCLUDED)
	 * @param endDate upper selection (EXCLUDED)
	 * @return HQueryBuilder
	 */
	public HQueryBuilder between(final HTimeDimension timeDimension, final String startdate, final String endDate) {
		Assertion.checkNotNull(timeDimension);
		Assertion.checkNotNull(startdate);
		Assertion.checkNotNull(endDate);
		Assertion.checkState(timeSelection == null, "time selection already set");
		//---------------------------------------------------------------------

		final Date minDate = readDate(startdate, timeDimension);
		final Date maxDate = readDate(endDate, timeDimension);
		timeSelection = new HTimeSelection(timeDimension, minDate, maxDate);
		return this;
	}

	/**
	 * @param startDate lower selection (INCLUDED)
	 * @param endDate upper selection (EXCLUDED)
	 * @return HQueryBuilder
	 */
	public HQueryBuilder between(final HTimeDimension timeDimension, final Date startDate, final Date endDate) {
		Assertion.checkNotNull(timeDimension);
		Assertion.checkNotNull(startDate);
		Assertion.checkNotNull(endDate);
		Assertion.checkState(timeSelection == null, "time selection already set");
		//---------------------------------------------------------------------
		timeSelection = new HTimeSelection(timeDimension, startDate, endDate);
		return this;
	}

	public HQueryBuilder whereLocationMatches(final String pattern) {
		Assertion.checkArgNotEmpty(pattern);
		Assertion.checkState(locationSelection == null, "location's pattern is already set");
		//---------------------------------------------------------------------
		locationSelection = new HLocationSelection(pattern);
		return this;
	}

	public HQueryBuilder whereCategoryMatches(final String pattern) {
		Assertion.checkArgNotEmpty(pattern);
		Assertion.checkState(categorySelection == null, "category's pattern is already set");
		//---------------------------------------------------------------------
		categorySelection = new HCategorySelection(pattern);
		return this;
	}

	/**
	 *
	 * @param timeStr : e.g: NOW+1h
	 * @param dimension : Dimension temporelle : année/mois/jour/...
	 * @return Date obtenue à partir des deux indications précedentes
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
	 * @param deltaAsString Delta en millisecondes
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
				throw new RuntimeException("La durée doit préciser l'unité de temps utilisée : d=jour, h=heure, m=minute");
		}
	}

	/** {@inheritDoc} */
	@Override
	public HQuery build() {
		Assertion.checkNotNull(timeSelection, "date selection is required");
		if (categorySelection == null) {
			categorySelection = new HCategorySelection("*"); //par défaut on prend tous
		}
		if (locationSelection == null) {
			locationSelection = new HLocationSelection("*"); //par défaut on prend tous
		}
		return new HQuery(locationSelection, timeSelection, categorySelection);
	}
}
