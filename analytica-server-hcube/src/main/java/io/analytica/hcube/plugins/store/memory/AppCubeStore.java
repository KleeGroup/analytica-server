package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HCubeKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.lang.Assertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class AppCubeStore {
	private final List<HCube> queue;
	private final Map<HCubeKey, HCube> store;

	AppCubeStore() {
		queue = new ArrayList<>();
		store = new HashMap<>();
	}

	void merge(final HCube cube) {
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

	//On construit un nouveau cube à partir de l'ancien(peut être null) et du nouveau.
	private void merge(final HCube cube, final HCubeKey cubeKey) {

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

	private void printStats() {
		System.out.println("memStore : " + store.size() + " cubes");
	}

	Map<HCategory, HSerie> findAll(final HQuery query, final HCategoryDictionary categoryDictionary) {
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
