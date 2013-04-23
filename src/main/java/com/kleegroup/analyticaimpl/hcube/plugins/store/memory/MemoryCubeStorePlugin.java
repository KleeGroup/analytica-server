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
package com.kleegroup.analyticaimpl.hcube.plugins.store.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HCubeBuilder;
import com.kleegroup.analytica.hcube.dimension.HCubePosition;
import com.kleegroup.analytica.hcube.dimension.HTimePosition;
import com.kleegroup.analytica.hcube.dimension.HCategoryPosition;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analyticaimpl.hcube.CubeStorePlugin;

/**
 * Implémentation mémoire du stockage des Cubes.
 * 
 * @author npiedeloup, pchretien
 * @version $Id: MemoryCubeStorePlugin.java,v 1.11 2013/01/14 16:35:20 npiedeloup Exp $
 */
final class MemoryCubeStorePlugin implements CubeStorePlugin {
	private final Map<HCubePosition, HCube> store = new HashMap<HCubePosition, HCube>();

	/**
	 * Constructeur.
	 */
	public MemoryCubeStorePlugin() {
		//
	}

	/** {@inheritDoc} */
	public synchronized void merge(final HCube lowLevelCube) {
		Assertion.notNull(lowLevelCube);
		//---------------------------------------------------------------------
		for (HCubePosition upCubePosition : lowLevelCube.getPosition().drillUp()) {
			HCube cube = merge(lowLevelCube, upCubePosition);
			store.put(cube.getPosition(), cube);
		}
	}

	//On construit un nouveau cube à partir de l'ancien(peut être null) et du nouveau.
	private final HCube merge(final HCube cube, final HCubePosition cubePosition) {
		final HCubeBuilder cubeBuilder = new HCubeBuilder(cubePosition)//
				.withCube(cube);

		final HCube oldCube = store.get(cubePosition);
		if (oldCube != null) {
			cubeBuilder.withCube(oldCube);
		}
		return cubeBuilder.build();
	}

	//	/** {@inheritDoc} */
	public synchronized List<HCube> findAll(HQuery query) {
		//On prépare les bornes de temps
		final HCategoryPosition categoryPosition = query.getCategoryPosition();

		//Sécurité pour éviter une boucle infinie
		List<HCube> cubes = new ArrayList<HCube>();

		for (HTimePosition currentTimePosition : query.getAllTimePositions()) {
			HCubePosition cubePosition = new HCubePosition(currentTimePosition, categoryPosition);
			HCube cube = store.get(cubePosition);
			//---
			cubes.add(cube == null ? new HCubeBuilder(cubePosition).build() : cube);
			//---
			currentTimePosition = currentTimePosition.next();
		}
		return cubes;
	}

	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		for (HCube cube : store.values()) {
			sb.append(cube);
			sb.append("\r\n");
		}
		return sb.toString();
	}

}
