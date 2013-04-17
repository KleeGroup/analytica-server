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
package com.kleegroup.analytica.hcube.query;

import java.util.Date;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.dimension.TimePosition;

/**
 * Une selection temporelle permet de définir un ensemble de position sur un niveau temporel donné.
 * exemple : 
 *  - tous les jours du 15 septembre 2000 au 15 novembre 2000.
 *  - toutes les années de 1914 à 1918 
 * @author npiedeloup, pchretien
 */
final class TimeSelection {
	private final TimePosition minTimePosition;
	private final TimePosition maxTimePosition;
	private final TimeDimension dimension;

	TimeSelection(final TimeDimension dimension, final Date minDate, final Date maxDate) {
		Assertion.notNull(minDate);
		Assertion.notNull(maxDate);
		Assertion.precondition(minDate.equals(maxDate) || minDate.before(maxDate), "la date min doit être inférieure à la date max");
		Assertion.notNull(dimension);
		//---------------------------------------------------------------------
		this.minTimePosition = new TimePosition(minDate, dimension);
		this.maxTimePosition = new TimePosition(maxDate, dimension);
		this.dimension = dimension;
	}

	/** {@inheritDoc} */
	public TimeDimension getDimension() {
		return dimension;
	}

	public TimePosition getMinTimePosition() {
		return minTimePosition;
	}

	public TimePosition getMaxTimePosition() {
		return maxTimePosition;
	}
}
