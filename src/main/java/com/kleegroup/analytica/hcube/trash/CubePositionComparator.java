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
package com.kleegroup.analyticaimpl.server.plugins.cubestore.memory;

import java.util.Comparator;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.CubePosition;
import com.kleegroup.analytica.hcube.dimension.TimePosition;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;

/**
 * @author npiedeloup
 * @version $Id: CubeKeyComparator.java,v 1.5 2012/04/17 09:11:33 pchretien Exp $
 */
final class CubePositionComparator implements Comparator<CubePosition> {

	public int compare(final CubePosition o1, final CubePosition o2) {
		final WhatPosition what1 = o1.getWhatPosition();
		final WhatPosition what2 = o2.getWhatPosition();
		//	Assertion.invariant(what1.getDimension().equals(what2.getDimension()), "On ne compare pas des dimenssions différentes : {0} != {1}", what1, what2);
		if (what1.equals(what2)) {
			final TimePosition time1 = o1.getTimePosition();
			final TimePosition time2 = o2.getTimePosition();
			Assertion.invariant(time1.getDimension().equals(time2.getDimension()), "On ne compare pas des dimenssions différentes : {0} != {1}", time1, time2);
			return time1.getValue().compareTo(time2.getValue());
		}
		return what1.getValue().toString().compareTo(what2.getValue().toString());
	}

}
