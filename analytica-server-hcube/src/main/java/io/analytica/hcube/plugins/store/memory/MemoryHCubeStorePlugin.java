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
package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HCubeKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.impl.HCubeStorePlugin;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.lang.Assertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation mémoire du stockage des Cubes.
 * 
 * @author npiedeloup, pchretien
 */
public final class MemoryHCubeStorePlugin implements HCubeStorePlugin {
	private final List<HCube> queue = new ArrayList<>();
	private final Map<HCubeKey, HCube> store = new HashMap<>();

	/** {@inheritDoc} */
	public synchronized void merge(final HCube cube) {
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		//populate a queue
		queue.add(cube);
		if (queue.size() > 5000) {
			flushQueue();
		}
	}

	//flushing queue into store
	private void flushQueue() {
		for (final HCube cube : queue) {
			for (final HCubeKey upCubeKeys : cube.getKey().drillUp()) {
				merge(cube, upCubeKeys);
			}
		}
		queue.clear();
		printStats();

	}

	private void printStats() {
		System.out.println("memStore : " + store.size() + " cubes");
	}

	//On construit un nouveau cube à partir de l'ancien(peut être null) et du nouveau.
	private final void merge(final HCube cube, final HCubeKey cubeKey) {

		final HCube oldCube = store.get(cubeKey);
		final HCube newCube;
		if (oldCube != null) {
			newCube = new HCubeBuilder(cubeKey).withMetrics(cube.getMetrics()).withMetrics(oldCube.getMetrics()).build();
		} else if (cube.getKey().equals(cubeKey)) {
			newCube = cube;
		} else {
			newCube = new HCubeBuilder(cubeKey).withMetrics(cube.getMetrics()).build();
		}
		store.put(newCube.getKey(), newCube);
	}

	/** {@inheritDoc} */
	public synchronized Map<HCategory, HSerie> findAll(final HQuery query, final HCategoryDictionary categoryDictionary) {
		Assertion.checkNotNull(query);
		Assertion.checkNotNull(categoryDictionary);
		//---------------------------------------------------------------------
		flushQueue();
		//On itère sur les séries indexées par les catégories de la sélection.
		final Map<HCategory, HSerie> cubeSeries = new HashMap<>();

		for (final HCategory category : query.getAllCategories(categoryDictionary)) {
			final List<HCube> cubes = new ArrayList<>();

			for (HTime currentTime : query.getAllTimes()) {
				final HCubeKey cubeKey = new HCubeKey(currentTime, category/*, null*/);
				final HCube cube = store.get(cubeKey);
				//---
				//2 stratégies possibles : on peut choisir de retourner tous les cubes ou seulement ceux avec des données
				cubes.add(cube == null ? new HCubeBuilder(cubeKey).build() : cube);
				/*if (cube != null) {
					cubes.add(new HCubeBuilder(cubeKey).build());
				}*/
				//---
				currentTime = currentTime.getDimension().next(currentTime.inMillis());
			}
			//A nouveau on peut choisir de retourner toutes les series ou seulement celles avec des données 
			//if (!cubes.isEmpty()) {
			cubeSeries.put(category, new HSerie(category, cubes));
			//}
		}
		printStats();
		return cubeSeries;
	}

	/** {@inheritDoc} */
	/*@Override
	public synchronized String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final HCube cube : store.values()) {
			sb.append(cube);
			sb.append("\r\n");
		}
		return sb.toString();
	}*/
}
