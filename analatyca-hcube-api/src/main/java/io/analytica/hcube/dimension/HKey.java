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

import io.vertigo.lang.Assertion;

/**
 * Position (ou clé) du cube dans l'espace multidimensionnel.
 *
 * A partir d'une position il est possible d'accéder à la liste de toutes les positions qui la contiennent.
 * Inversement il est possible de savoir si une poition est contenue dans une autre.
 *
 * @author npiedeloup, pchretien
 */
public final class HKey {
	private final HLocation location;
	private final HTime time;
	private final HCategory category;
	private final int hash;

	public HKey(final HLocation location, final HTime time, final HCategory category) {
		Assertion.checkNotNull(location);
		Assertion.checkNotNull(time);
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		this.location = location;
		this.time = time;
		this.category = category;
		hash = location.hashCode() + time.hashCode() >> 3 + category.hashCode() >> 6;
	}

	public HLocation getLocation() {
		return location;
	}

	public HTime getTime() {
		return time;
	}

	public HCategory getCategory() {
		return category;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof HKey) {
			final HKey other = HKey.class.cast(object);
			return location.equals(other.location) && time.equals(other.time) && category.equals(other.category);
		}
		return false;
	}

	@Override
	public final String toString() {
		return " { location:" + location + ", time:" + time + ", category:" + category + " }";
	}
}
