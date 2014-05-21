package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.lang.Assertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class AppCubeStore {
	private static final class QueueItem {
		final HKey key;
		final HCube cube;

		QueueItem(HKey key, HCube cube) {
			Assertion.checkNotNull(key);
			Assertion.checkNotNull(cube);
			//---------------------------------------------------------------------
			this.key = key;
			this.cube = cube;
		}
	}

	private static final int QUEUE_SIZE = 5000;
	private final List<QueueItem> queue;
	private final Map<HKey, HCube> store;
	//---------------------------------------------------------------------
	private final String appName;
	private final CategoryRepository categoryRepository = new CategoryRepository();

	AppCubeStore(final String appName) {
		Assertion.checkArgNotEmpty(appName);
		//---------------------------------------------------------------------
		this.appName = appName;
		queue = new ArrayList<>();
		store = new HashMap<>();
	}

	void push(final HKey key, final HCube cube) {
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		categoryRepository.addCategory(key.getCategory());

		//populate a queue
		queue.add(new QueueItem(key, cube));
		if (queue.size() > QUEUE_SIZE) {
			flushQueue();
		}
	}

	//flushing queue into store
	private void flushQueue() {
		for (final QueueItem item : queue) {
			for (final HKey upKeys : item.key.drillUp()) {
				merge(upKeys, item.cube);
			}
		}
		queue.clear();
		printStats();
	}

	//On construit un nouveau cube � partir de l'ancien(peut �tre null) et du nouveau.
	private void merge(final HKey key, final HCube cube) {
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		final HCube oldCube = store.get(key);
		final HCube newCube;
		if (oldCube != null) {
			HCubeBuilder cubeBuilder = new HCubeBuilder();
			for (final HMetricKey metricKey : cube.getMetricKeys()) {
				cubeBuilder.withMetric(metricKey, cube.getMetric(metricKey));
			}
			for (final HMetricKey metricKey : oldCube.getMetricKeys()) {
				cubeBuilder.withMetric(metricKey, oldCube.getMetric(metricKey));
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

	List<HSerie> findAll(final HQuery query, final HSelector selector) {
		Assertion.checkNotNull(query);
		Assertion.checkNotNull(selector);
		//---------------------------------------------------------------------
		flushQueue();
		//On it�re sur les s�ries index�es par les cat�gories de la s�lection.
		List<HSerie> series = new ArrayList<>();

		for (final HCategory category : selector.getCategorySelector().findCategories(appName, query.getCategorySelection())) {
			final Map<HTime, HCube> cubes = new LinkedHashMap<>();

			for (HTime currentTime : selector.getTimeSelector().findTimes(query.getTimeSelection())) {
				final HKey key = new HKey(currentTime, category/*, null*/);
				final HCube cube = store.get(key);
				//---
				//2 strat�gies possibles : on peut choisir de retourner tous les cubes ou seulement ceux avec des donn�es
				cubes.put(currentTime, cube == null ? new HCubeBuilder().build() : cube);
				/*if (cube != null) {
					cubes.add(new HCubeBuilder(key).build());
				}*/
				//---
				currentTime = currentTime.getDimension().next(currentTime.inMillis());
			}
			//A nouveau on peut choisir de retourner toutes les series ou seulement celles avec des donn�es 
			//if (!cubes.isEmpty()) {
			series.add(new HSerie(category, cubes));
			//}
		}
		printStats();
		return series;
	}

	Set<HCategory> getAllSubCategories(HCategory category) {
		return categoryRepository.getAllSubCategories(category);
	}

	Set<HCategory> getAllRootCategories() {
		return categoryRepository.getAllRootCategories();
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

	long count() {
		flushQueue();
		return store.size();
	}

}
