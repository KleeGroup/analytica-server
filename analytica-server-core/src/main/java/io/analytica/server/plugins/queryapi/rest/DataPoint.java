/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.server.plugins.queryapi.rest;

import io.vertigo.lang.Assertion;

/*
 * Couple(date, metrique)
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public final class DataPoint {
	private final long x;
	private final String y;

	public DataPoint(final long timeInMs, final String value) {
		Assertion.checkNotNull(timeInMs);
		//---------------------------------------------------------------------
		x = timeInMs;
		y = value;
	}

	/**
	 * @return time in seconds
	 */
	public long getTimeInS() {
		return x;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return y;
	}
}
