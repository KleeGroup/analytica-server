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
package io.analytica.hcube.dimension;

import io.vertigo.kernel.lang.Assertion;

/**
 * Une Location est composée d'une hierarchie de termes.
 *  * exemple :
 * MyApp :: Prod / Server1 / Jvm1 
 * Cet exemple illustre une Jvm1 d'unr application MyApp qui s'execute dans un environnement de production sur un Server1 
 * 
 * Il doit ncessairement y avoir une localisation parente représentant l'application (MyApp dans notre exemple). 
 * @author npiedeloup, pchretien
 */
public final class HLocation implements HPosition<HLocation> {
	private final String systemName;
	private final String[] systemLocation;
	private final String id;

	public HLocation(final String systemName) {
		this(systemName, new String[0]);
	}

	public HLocation(final String systemName, final String[] systemLocation) {
		Assertion.checkArgNotEmpty(systemName);
		//---------------------------------------------------------------------
		id = buildKey(systemName, systemLocation);
		this.systemLocation = systemLocation;
		this.systemName = systemName;
	}

	/** {@inheritDoc} */
	public HLocation drillUp() {
		if (systemLocation.length == 0) {
			return null;
		}
		final String[] redux = new String[systemLocation.length - 1];
		for (int i = 0; i < systemLocation.length - 1; i++) {
			redux[i] = systemLocation[i];
		}
		return new HLocation(systemName, redux);
	}

	public String[] getValue() {
		return systemLocation;
	}

	private static String buildKey(final String systemName, final String[] systemLocation) {
		final StringBuilder sb = new StringBuilder(systemName);
		for (final String element : systemLocation) {
			sb.append("/").append(element);
		}
		return sb.toString();
	}

	@Override
	public final int hashCode() {
		return id.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		if (object instanceof HLocation) {
			return id.equals(((HLocation) object).id);
		}
		return false;
	}

	@Override
	public final String toString() {
		return id;
	}
}
