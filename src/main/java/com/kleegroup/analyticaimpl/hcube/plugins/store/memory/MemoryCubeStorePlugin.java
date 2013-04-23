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

import com.kleegroup.analytica.hcube.cube.Cube;
import com.kleegroup.analytica.hcube.cube.CubeBuilder;
import com.kleegroup.analytica.hcube.dimension.CubePosition;
import com.kleegroup.analytica.hcube.dimension.TimePosition;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;
import com.kleegroup.analytica.hcube.query.Query;
import com.kleegroup.analyticaimpl.hcube.CubeStorePlugin;

/**
 * Implémentation mémoire du stockage des Cubes.
 * 
 * @author npiedeloup, pchretien
 * @version $Id: MemoryCubeStorePlugin.java,v 1.11 2013/01/14 16:35:20 npiedeloup Exp $
 */
final class MemoryCubeStorePlugin implements CubeStorePlugin {
	private final Map<CubePosition, Cube> store = new HashMap<CubePosition, Cube>();

	/**
	 * Constructeur.
	 */
	public MemoryCubeStorePlugin() {
		//
	}

	/** {@inheritDoc} */
	public synchronized void merge(final Cube lowLevelCube) {
		Assertion.notNull(lowLevelCube);
		//---------------------------------------------------------------------
		for (CubePosition upCubePosition : lowLevelCube.getPosition().drillUp()) {
			Cube cube = merge(lowLevelCube, upCubePosition);
			store.put(cube.getPosition(), cube);
		}
	}

	//On construit un nouveau cube à partir de l'ancien(peut être null) et du nouveau.
	private final Cube merge(final Cube cube, final CubePosition cubePosition) {
		final CubeBuilder cubeBuilder = new CubeBuilder(cubePosition)//
				.withCube(cube);

		final Cube oldCube = store.get(cubePosition);
		if (oldCube != null) {
			cubeBuilder.withCube(oldCube);
		}
		return cubeBuilder.build();
	}

	//	/** {@inheritDoc} */
	public synchronized List<Cube> findAll(Query query) {
		//On prépare les bornes de temps
		final WhatPosition whatPosition = query.getWhatPosition();

		//Sécurité pour éviter une boucle infinie
		List<Cube> cubes = new ArrayList<Cube>();

		for (TimePosition currentTimePosition : query.getAllTimePositions()) {
			CubePosition cubePosition = new CubePosition(currentTimePosition, whatPosition);
			Cube cube = store.get(cubePosition);
			//---
			cubes.add(cube == null ? new CubeBuilder(cubePosition).build() : cube);
			//---
			currentTimePosition = currentTimePosition.next();
		}
		return cubes;
	}

	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		for (Cube cube : store.values()) {
			sb.append(cube);
			sb.append("\r\n");
		}
		return sb.toString();
	}

}
