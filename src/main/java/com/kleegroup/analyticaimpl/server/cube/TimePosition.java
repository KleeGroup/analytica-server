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
package com.kleegroup.analyticaimpl.server.cube;

import java.util.Date;

import com.kleegroup.analytica.server.data.TimeDimension;

/**
 * @author npiedeloup
 * @version $Id: TimePosition.java,v 1.2 2012/04/17 09:11:15 pchretien Exp $
 */
public final class TimePosition extends Identity {
	private final TimeDimension timeDimension;
	private final Date date;

	public TimePosition(final Date date, final TimeDimension timeDimension) {
		super("Time:[" + timeDimension.name() + "]" + timeDimension.reduce(date).getTime());
		//Assertion.notNull(timeDimension); inutil
		//---------------------------------------------------------------------
		this.timeDimension = timeDimension;
		this.date = timeDimension.reduce(date);
	}

	public TimePosition drillUp() {
		final TimeDimension upTimeDimension = timeDimension.drillUp();
		return upTimeDimension != null ? new TimePosition(date, upTimeDimension) : null;
	}

	public TimeDimension getDimension() {
		return timeDimension;
	}

	public Date getValue() {
		return date;
	}

	/**
	 * Vérifie si la position est contenue dans une autre autre.
	 * Une position A est contenue dans une position B  
	 * Si A = B
	 * Si B peut être obtenu par drillUp successifs sur A.
	 * @param otherTime
	 * @return
	 */
	public boolean isIn(final TimePosition otherTime) {
		if (this.equals(otherTime)) {
			return true;
		}
		TimePosition upperTime = drillUp();
		while (upperTime != null && !upperTime.equals(otherTime)) {
			upperTime = upperTime.drillUp();
		}
		return otherTime.equals(upperTime);
	}
}
