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

import java.util.ArrayList;
import java.util.List;

/**
 * Position (ou clé) du cube dans l'espace multidimensionnel. 
 * 
 * A partir d'une position il est possible d'accéder à la liste de toutes les positions qui la contiennent.
 * Inversement il est possible de savoir si une poition est contenue dans une autre.
 *  
 * @author npiedeloup, pchretien
 */
public final class HKey {
	private final String type;
	private final HTime time;
	private final HCategory[] categories;
	private final int hash;

	public HKey(final String type, final HTime time, final String... categories) {
		this(type, time, to(categories));
	}

	public HKey(final String type, final HTime time, final List<HCategory> categories) {
		this(type, time, categories.toArray(new HCategory[categories.size()]));
	}

	private HKey(final String type, final HTime time, final HCategory[] categories) {
		Assertion.checkArgNotEmpty(type);
		Assertion.checkNotNull(time);
		Assertion.checkNotNull(categories);
		//---------------------------------------------------------------------
		this.type = type;
		this.time = time;
		this.categories = categories.clone();
		hash = type.hashCode() + time.hashCode() >> 3 + categories[0].hashCode() >> 6;
	}

	public static HCategory[] to(final String[] strCategories) {
		HCategory[] categories = new HCategory[strCategories.length];
		for (int i = 0; i < categories.length; i++) {
			categories[i] = new HCategory(strCategories[i]);
		}
		return categories;
	}

	public String getType() {
		return type;
	}

	public HTime getTime() {
		return time;
	}

	public HCategory[] getCategories() {
		return categories;
	}

	/**
	 * Calcule la liste de tous les cubes auxquels le présent cube appartient
	 * Cette méthode permet de préparer toutes les agrégations.
	 * @return Liste de tous les cubes auxquels le présent cube appartient
	 */
	public List<HKey> drillUp() {
		final List<HKey> upperKeys = new ArrayList<>();
		//on remonte les axes, le premier sera le plus bas niveau
		HTime hTime = getTime();
		while (hTime != null) {
			HCategory[] hCategories = categories.clone();
			while (hCategories[0] != null) {
				upperKeys.add(new HKey(type, hTime, hCategories));
				//On remonte l'arbre des categories
				hCategories[0] = hCategories[0].drillUp();
			}
			//On remonte time
			hTime = hTime.drillUp();
		}
		return upperKeys;
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
			if (type.equals(other.type) && time.equals(other.time) && (categories.length == other.categories.length)) {
				for (int i = 0; i < categories.length; i++) {
					if (!categories[i].equals(other.categories[i])) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public final String toString() {
		return " { type:" + type + ", time:" + time + ", categories:" + categories + " }";
	}
}
