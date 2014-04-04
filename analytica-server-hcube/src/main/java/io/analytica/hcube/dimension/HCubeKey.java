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
 * @version $Id: CubeKey.java,v 1.2 2012/04/17 09:11:15 pchretien Exp $
 */
public final class HCubeKey {
	private final HTime time;
	private final HCategory category;

	public HCubeKey(final HTime time, final HCategory category) {
		Assertion.checkNotNull(time);
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		this.time = time;
		this.category = category;
	}

	public HTime getTime() {
		return time;
	}

	public HCategory getCategory() {
		return category;
	}

	/*	public HLocation getLocation() {
			return location;
		}*/

	/**
	 * Calcule la liste de tous les cubes auxquels le présent cube appartient
	 * Cette méthode permet de préparer toutes les agrégations.
	 * @return Liste de tous les cubes auxquels le présent cube appartient
	 */
	public List<HCubeKey> drillUp() {
		final List<HCubeKey> upperCubeKeys = new ArrayList<>();
		//on remonte les axes, le premier sera le plus bas niveau
		HTime hTime = getTime();
		while (hTime != null) {
			//	HLocation hLocation = getLocation();
			//	while (hLocation != null) {
			HCategory hCategory = getCategory();
			while (hCategory != null) {
				upperCubeKeys.add(new HCubeKey(hTime, hCategory/*, hLocation*/));
				//On remonte l'arbre des categories
				hCategory = hCategory.drillUp();
			}
			//On remonte l'arbre des Location
			//	hLocation = hLocation.drillUp();
			//	}
			//On remonte time
			hTime = hTime.drillUp();
		}
		return upperCubeKeys;
	}

	/**
	 * Vérifie l'inclusion de clé, util pour controller le merge de Cube.
	 * @param cubeKey Clé dont on veut vérifier l'inclusion
	 * @return Si la CubeKey courante est DANS la CubeKey en paramètre
	 */
	public boolean contains(final HCubeKey cubeKey) {
		if (equals(cubeKey)) {
			return true;
		}
		return contains(time, cubeKey.time) && contains(category, cubeKey.category); //&& contains(location, cubeKey.location);
	}

	/**
	 * Vérifie si la position est contenue dans une autre autre.
	 * Une position A est contenue dans une position B  
	 * Si A = B
	 * Si B peut être obtenu par drillUp successifs sur A.
	 * @param otherPosition
	 * @return si la position est contenue dans la otherPosition
	 */
	private static <P extends HPosition<P>> boolean contains(final P position, final P otherPosition) {
		//On vérifie que l'autre position est contenue dans la première
		//========[----position-----]====
		//=============[other]===========
		//pour ce faire on remonte les positions jusqu'à les faire coincider.
		P upperPosition = otherPosition;
		while (upperPosition != null) {
			if (position.equals(upperPosition)) {
				return true;
			}
			upperPosition = upperPosition.drillUp();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return time.hashCode() + category.hashCode() * 31;
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof HCubeKey) {
			HCubeKey other = HCubeKey.class.cast(object);
			return time.equals(other.time) && category.equals(other.category);
		}
		return false;
	}

	@Override
	public final String toString() {
		return "cube:" + time + "; " + category.id();
	}
}
