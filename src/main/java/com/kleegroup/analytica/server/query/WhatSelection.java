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
package com.kleegroup.analytica.server.query;

import java.util.Arrays;
import java.util.List;

import kasper.kernel.util.Assertion;

/**
 * 
 * @author npiedeloup, pchretien
 * @version $Id: WhatSelection.java,v 1.6 2012/04/17 09:03:44 pchretien Exp $
 */
public final class WhatSelection implements Selection<WhatDimension> {
	private final List<String> whatValues;
	private final WhatDimension dimension;

	public WhatSelection(final WhatDimension dimension, final String... whatValues) {
		Assertion.notNull(dimension);
		Assertion.notNull(whatValues);
		for (final String whatValue : whatValues) {
			Assertion.precondition(whatValue.equals(dimension.reduce(whatValue)), "La valeur de what:\"{0}\", est d'une dimension inférieur à celle de la selection: {1}, il y a perte d'information.", whatValue, dimension);
		}
		//---------------------------------------------------------------------
		this.whatValues = Arrays.asList(whatValues);
		this.dimension = dimension;
	}

	public List<String> getWhatValues() {
		return whatValues;
	}

	/** {@inheritDoc} */
	public WhatDimension getDimension() {
		return dimension;
	}
}
