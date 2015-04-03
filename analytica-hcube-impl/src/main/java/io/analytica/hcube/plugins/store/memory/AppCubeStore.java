package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.HCubeStoreException;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HKeyUtil;
import io.analytica.hcube.dimension.HLocation;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.result.HSerie;
import io.vertigo.lang.Assertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class AppCubeStore {
	private static final int QUEUE_SIZE = 5000;
	private final AppQueue queue;
	private final Map<HKey, HCube> store;

	AppCubeStore() {
		queue = new AppQueue();
		store = new HashMap<>();
	}

	void push(final HKey key, final HCube cube) {
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		//populate a queue
		queue.push(new AppQueue.QueueItem(key, cube));
		if (queue.size() > QUEUE_SIZE) {
			flushQueue();
		}
	}

	//flushing queue into store
	private void flushQueue() {
		for (AppQueue.QueueItem item = queue.pop(); item != null; item = queue.pop()) {
			for (final HKey upKeys : HKeyUtil.drillUp(item.key)) {
				merge(upKeys, item.cube);
			}
		}
		printStats();
	}

	//On construit un nouveau cube à partir de l'ancien(peut être null) et du nouveau.
	private void merge(final HKey key, final HCube cube) {
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		final HCube oldCube = store.get(key);
		final HCube newCube;
		if (oldCube != null) {
			final HCubeBuilder cubeBuilder = new HCubeBuilder();
			for (final HMetric metric : cube.getMetrics()) {
				cubeBuilder.withMetric(metric);
			}
			for (final HMetric metric : oldCube.getMetrics()) {
				cubeBuilder.withMetric(metric);
			}

			newCube = cubeBuilder.build();
		} else {
			newCube = cube;
		}
		store.put(key, newCube);
	}

	private void printStats() {
		System.out.println("memStore : " + store.size() + " cubes");
	}

	List<HSerie> findAll(final HQuery query, final HSelector selector) throws HCubeStoreException {
		Assertion.checkNotNull(query);
		Assertion.checkNotNull(selector);
		//---------------------------------------------------------------------
		flushQueue();
		//On itère sur les séries indexées par les catégories de la sélection.
		final List<HSerie> series = new ArrayList<>();

		for (final HCategory currentCategory : selector.findCategories(query.getCategorySelection())) {
			final Map<HTime, HCube> cubes = new LinkedHashMap<>();

			for (final HTime currentTime : selector.findTimes(query.getTimeSelection())) {
				for (final HLocation currentLocation : selector.findLocations(query.getLocationSelection())) {
					final HKey key = new HKey(currentLocation, currentTime, currentCategory);
					final HCube cube = store.get(key);
					//---
					//2 stratégies possibles : on peut choisir de retourner tous les cubes ou seulement ceux avec des données
					cubes.put(currentTime, cube == null ? new HCubeBuilder().build() : cube);
				}
			}
			//A nouveau on peut choisir de retourner toutes les series ou seulement celles avec des données
			series.add(new HSerie(currentCategory, cubes));
		}
		printStats();
		return series;
	}

	/*@Override
	public synchronized String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final HCube cube : store.values()) {
			sb.append(cube);
			sb.append("\r\n");
		}
		return sb.toString();
	}*/

	long size() {
		flushQueue();
		return store.size();
	}

}
