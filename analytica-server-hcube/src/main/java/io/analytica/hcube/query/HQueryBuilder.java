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

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HTimeDimension;
import io.vertigo.kernel.lang.Assertion;
import io.vertigo.kernel.lang.Builder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Builder de la requête.
 * @author npiedeloup, pchretien
 */
public final class HQueryBuilder implements Builder<HQuery> {
	private HTimeDimension hTimeDimension;
	private Date from;
	private Date to;
	//----
	private HCategory category;
	private boolean categoryChildren;

	//----
	//	private HLocation location;
	//	private boolean locationChildren;

	public HQueryBuilder on(final String timeDimension) {
		Assertion.checkNotNull(timeDimension);
		Assertion.checkState(this.hTimeDimension == null, "timeDimension already set");
		//---------------------------------------------------------------------
		this.hTimeDimension = HTimeDimension.valueOf(timeDimension);
		return this;
	}

	public HQueryBuilder on(final HTimeDimension timeDimension) {
		Assertion.checkNotNull(timeDimension);
		Assertion.checkState(this.hTimeDimension == null, "timeDimension already set");
		//---------------------------------------------------------------------
		this.hTimeDimension = timeDimension;
		return this;
	}

	public HQueryBuilder from(final String date) {
		Assertion.checkNotNull(hTimeDimension);
		//---------------------------------------------------------------------
		return from(readDate(date, hTimeDimension));
	}

	public HQueryBuilder from(final Date date) {
		Assertion.checkNotNull(date);
		Assertion.checkState(from == null, "Date From already set");
		//---------------------------------------------------------------------
		from = date;
		return this;
	}

	public HQueryBuilder to(final Date date) {
		Assertion.checkNotNull(date);
		Assertion.checkState(to == null, "Date To already set");
		//---------------------------------------------------------------------
		to = date;
		return this;
	}

	public HQueryBuilder to(final String date) {
		Assertion.checkNotNull(hTimeDimension);
		//---------------------------------------------------------------------
		return to(readDate(date, hTimeDimension));
	}

	public HQueryBuilder withChildren(final String type, final String... subCategories) {
		return doWith(type, subCategories, true);
	}

	public HQueryBuilder with(final String type, final String... subCategories) {
		return doWith(type, subCategories, false);
	}

	private HQueryBuilder doWith(final String type, final String[] subTypes, final boolean children) {
		Assertion.checkNotNull(type);
		Assertion.checkState(category == null, "category already set");
		//---------------------------------------------------------------------
		category = new HCategory(type, subTypes);
		categoryChildren = children;
		return this;
	}

	public HQueryBuilder whereChildren(final String systemName, final String... systemLocation) {
		return doWith(systemName, systemLocation, true);
	}

	/*public HQueryBuilder where(final String systemName, final String... systemLocation) {
		return doWhere(systemName, systemLocation, false);
	}

	private HQueryBuilder doWhere(final String systemName, final String[] systemLocation, final boolean children) {
		Assertion.checkNotNull(systemName);
		Assertion.checkState(location == null, "location already set");
		//---------------------------------------------------------------------
		location = new HLocation(systemName, systemLocation);
		locationChildren = children;
		return this;
	}*/

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

		final SimpleDateFormat sdf = new SimpleDateFormat(dimension.getPattern());

		try {
			return sdf.parse(timeStr);
		} catch (final ParseException e) {
			throw new RuntimeException("Erreur de format de date (" + timeStr + "). Format attendu :" + sdf.toPattern());
		}
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
			case 'd':
				return delta * 24 * 60 * 60 * 1000L;

			case 'h':
				return delta * 60 * 60 * 1000L;

			case 'm':
				return delta * 60 * 1000L;

			default:
				throw new RuntimeException("La durée doit préciser l'unité de temps utilisée : d=jour, h=heure, m=minute");
		}
	}

	/** {@inheritDoc} */
	public HQuery build() {
		return new HQuery(new HTimeSelection(hTimeDimension, from, to), new HCategorySelection(category, categoryChildren)/*, new HLocationSelection(location, locationChildren)*/);
	}
}
