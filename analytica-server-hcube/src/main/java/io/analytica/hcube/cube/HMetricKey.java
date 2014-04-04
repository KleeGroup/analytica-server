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
package io.analytica.hcube.cube;

import io.vertigo.kernel.lang.Assertion;

/** 
 * Clé de la métrique.
 * @author npiedeloup, pchretien
 * @version $Id: Metric.java,v 1.5 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class HMetricKey {
	private final boolean clustered;
	private final String name;

	public HMetricKey(String name, boolean clustered) {
		Assertion.checkArgNotEmpty(name);
		//---------------------------------------------------------------------
		this.name = name;
		this.clustered = clustered;
	}

	public boolean isClustered() {
		return clustered;
	}

	@Override
	public final int hashCode() {
		return name.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		if (object instanceof HMetricKey) {
			return name.equals(((HMetricKey) object).name);
		}
		return false;
	}

	@Override
	public final String toString() {
		return "metricKey::" + name.toString();
	}
}
