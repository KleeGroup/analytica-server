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
package com.kleegroup.analytica.hcube.dimension;

import java.util.ArrayList;
import java.util.List;

import com.kleegroup.analytica.hcube.HKey;

/**
 * Position (clé) du cube dans l'espace multidimensionnel. 
 * 
 * A partir d'une position il est possible d'accéder à la liste de toutes les positions qui la contiennent.
 * Inversement il est possible de savoir si une poition est contenue dans une autre.
 *  
 * @author npiedeloup, pchretien
 * @version $Id: CubeKey.java,v 1.2 2012/04/17 09:11:15 pchretien Exp $
 */
public final class CubePosition extends HKey {
	private final TimePosition timePosition;
	private final WhatPosition whatPosition;

	public CubePosition(final TimePosition timePosition, final WhatPosition whatPosition) {
		super("cube:" + timePosition.id() + "; " + whatPosition.id());
		//---------------------------------------------------------------------
		this.timePosition = timePosition;
		this.whatPosition = whatPosition;
	}

	public TimePosition getTimePosition() {
		return timePosition;
	}

	public WhatPosition getWhatPosition() {
		return whatPosition;
	}

	/**
	 * Calcule la liste de tous les cubes auxquels le présent cube appartient
	 * Cette méthode permet de préparer toutes les agrégations.
	 * @return Liste de tous les cubes auxquels le présent cube appartient
	 */
	public List<CubePosition> drillUp() {
		List<CubePosition> upperCubePositions = new ArrayList<CubePosition>();
		//on remonte les axes, le premier sera le plus bas niveau
		TimePosition timePosition = getTimePosition();
		while (timePosition != null) {
			WhatPosition whatPosition = getWhatPosition();
			while (whatPosition != null) {
				upperCubePositions.add(new CubePosition(timePosition, whatPosition));
				//On remonte what
				whatPosition = whatPosition.drillUp();
			}
			//On remonte time
			timePosition = timePosition.drillUp();
		}
		return upperCubePositions;
	}

	/**
	 * Vérifie l'inclusion de clé, util pour controller le merge de Cube.
	 * @param cubePosition Clé dont on veut vérifier l'inclusion
	 * @return Si la CubeKey courante est DANS la CubeKey en paramètre
	 */
	public boolean contains(final CubePosition cubePosition) {
		if (this.equals(cubePosition)) {
			return true;
		}
		return contains(timePosition, cubePosition.timePosition) && contains(whatPosition, cubePosition.whatPosition);
	}

	/**
	 * Vérifie si la position est contenue dans une autre autre.
	 * Une position A est contenue dans une position B  
	 * Si A = B
	 * Si B peut être obtenu par drillUp successifs sur A.
	 * @param otherPosition
	 * @return
	 */
	private static boolean contains(final Position position, final Position otherPosition) {
		//On vérifie que l'autre position est contenue dans la première
		//========[----position-----]====
		//=============[other]===========
		//pour ce faire on remonte les positions jusqu'à les faire coincider.
		Position upperPosition = otherPosition;
		while (upperPosition != null) {
			if (position.equals(upperPosition)) {
				return true;
			}
			upperPosition = upperPosition.drillUp();
		}
		return false;
	}
}
