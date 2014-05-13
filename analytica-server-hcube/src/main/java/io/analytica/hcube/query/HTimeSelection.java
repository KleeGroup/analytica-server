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

import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;
import io.vertigo.kernel.lang.Assertion;

import java.util.Date;

/**
 * Selection temporelle permettant de définir un ensemble de positions sur un niveau temporel donné.
 * exemple : 
 *  - tous les jours du 15 septembre 2000 au 15 novembre 2000.
 *  - toutes les années de 1914 à 1918 
 * @author npiedeloup, pchretien
 */
public final class HTimeSelection {
	private final HTime minTime;
	private final HTime maxTime;

	//	private final TimeDimension dimension;

	HTimeSelection(final HTimeDimension dimension, final Date minDate, final Date maxDate) {
		Assertion.checkNotNull(minDate);
		Assertion.checkNotNull(maxDate);
		Assertion.checkArgument(minDate.equals(maxDate) || minDate.before(maxDate), "la date min doit être inférieure à la date max");
		Assertion.checkNotNull(dimension);
		//---------------------------------------------------------------------
		minTime = new HTime(minDate, dimension);
		maxTime = new HTime(maxDate, dimension);
	}

	public HTime getMinTime() {
		return minTime;
	}

	public HTime getMaxTime() {
		return maxTime;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "{ from:'" + minTime + "', to:'" + maxTime + "' }";
	}
}
