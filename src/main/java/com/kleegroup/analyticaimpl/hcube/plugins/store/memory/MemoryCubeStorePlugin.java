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
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HCubeKey;
import com.kleegroup.analytica.hcube.dimension.HTime;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.result.HSerie;
import com.kleegroup.analyticaimpl.hcube.CubeStorePlugin;

/**
 * Implémentation mémoire du stockage des Cubes.
 * 
 * @author npiedeloup, pchretien
 * @version $Id: MemoryCubeStorePlugin.java,v 1.11 2013/01/14 16:35:20 npiedeloup Exp $
 */
final class MemoryCubeStorePlugin implements CubeStorePlugin {
	private final Map<HCubeKey, HCube> store = new HashMap<HCubeKey, HCube>();

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
		for (final HCubeKey upCubeKeys : lowLevelCube.getKey().drillUp()) {
			final HCube cube = merge(lowLevelCube, upCubeKeys);
			store.put(cube.getKey(), cube);
		}
	}

	//On construit un nouveau cube à partir de l'ancien(peut être null) et du nouveau.
	private final HCube merge(final HCube cube, final HCubeKey cubeKey) {
		final HCubeBuilder cubeBuilder = new HCubeBuilder(cubeKey)//
				.withCube(cube);

		final HCube oldCube = store.get(cubeKey);
		if (oldCube != null) {
			cubeBuilder.withCube(oldCube);
		}
		return cubeBuilder.build();
	}

	/** {@inheritDoc} */
	public synchronized Map<HCategory, HSerie> findAll(final HQuery query) {
		Assertion.notNull(query);
		//---------------------------------------------------------------------
		//On itère sur les séries indexées par les catégories de la sélection.
		final Map<HCategory, HSerie> cubeSeries = new HashMap<HCategory, HSerie>();

		for (final HCategory category : query.getAllCategories()) {
			final List<HCube> cubes = new ArrayList<HCube>();
			for (HTime currentTime : query.getAllTimes()) {
				final HCubeKey cubeKey = new HCubeKey(currentTime, category);
				final HCube cube = store.get(cubeKey);
				//---
				//2 stratégies possibles : on peut choisir de retourner tous les cubes ou seulement ceux avec des données
				cubes.add(cube == null ? new HCubeBuilder(cubeKey).build() : cube);
				/*if (cube != null) {
					cubes.add(new HCubeBuilder(cubeKey).build());
				}*/
				//---
				currentTime = currentTime.next();
			}
			//A nouveau on peut choisir de retourner toutes les series ou seulement celles avec des données 
			//if (!cubes.isEmpty()) {
			cubeSeries.put(category, new HSerie(category, cubes));
			//}
		}
		return cubeSeries;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final HCube cube : store.values()) {
			sb.append(cube);
			sb.append("\r\n");
		}
		return sb.toString();
	}
}
