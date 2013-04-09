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
package com.kleegroup.analyticaimpl.server.plugins.cubestore.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.cube.Cube;
import com.kleegroup.analytica.hcube.cube.CubeBuilder;
import com.kleegroup.analytica.hcube.cube.MetaData;
import com.kleegroup.analytica.hcube.cube.Metric;
import com.kleegroup.analytica.hcube.dimension.CubePosition;
import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.dimension.TimePosition;
import com.kleegroup.analytica.hcube.dimension.WhatDimension;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;
import com.kleegroup.analytica.hcube.query.TimeSelection;
import com.kleegroup.analytica.hcube.query.WhatSelection;
import com.kleegroup.analytica.server.data.DataKey;
import com.kleegroup.analytica.server.data.DataType;
import com.kleegroup.analyticaimpl.server.CubeStorePlugin;

/**
 * Implémentation mémoire du stockage des Cubes.
 * @author npiedeloup
 * @version $Id: MemoryCubeStorePlugin.java,v 1.11 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class MemoryCubeStorePlugin implements CubeStorePlugin {
	//on utilise le caractère 254 (à la fin de l'ascii) pour faire la borne max de recherche : 
	//ainsi l'espace de recherche est [prefix, prefix+(char(154))]
	//cette technique permet d'utiliser le subList plutot qu'un startwith très couteux
	private final static char LAST_CHAR = 254;
	private final Comparator<CubePosition> cubeKeyComparator = new CubeKeyComparator();
	private final Map<TimeDimension, Map<WhatDimension, SortedMap<CubePosition, Cube>>> store;
	private String lastProcessIdStored;

	/**
	 * Constructeur.
	 */
	public MemoryCubeStorePlugin() {
		super();
		store = new HashMap<TimeDimension, Map<WhatDimension, SortedMap<CubePosition, Cube>>>();
		for (final TimeDimension timeDimension : TimeDimension.values()) {
			final Map<WhatDimension, SortedMap<CubePosition, Cube>> timeStore = new HashMap<WhatDimension, SortedMap<CubePosition, Cube>>();
			store.put(timeDimension, timeStore);
			for (final WhatDimension whatDimension : WhatDimension.values()) {
				final SortedMap<CubePosition, Cube> whatStore = new TreeMap<CubePosition, Cube>(cubeKeyComparator);
				timeStore.put(whatDimension, whatStore);
			}
		}
	}

	/** {@inheritDoc} */
	public void merge(final Cube lowLevelCube) {
		Assertion.notNull(lowLevelCube);
		//---------------------------------------------------------------------
		//on remonte les axes, le premier sera le plus bas niveau
		TimePosition timePosition = lowLevelCube.getKey().getTimePosition();
		while (timePosition != null) {
			WhatPosition whatPosition = lowLevelCube.getKey().getWhatPosition();
			while (whatPosition != null) {
				final CubePosition storedCubeKey = new CubePosition(timePosition, whatPosition);
				store(lowLevelCube, storedCubeKey);
				//On remonte what
				whatPosition = whatPosition.drillUp();
			}
			//On remonte time
			timePosition = timePosition.drillUp();
		}
	}

	private void store(final Cube cube, final CubePosition cubeKey) {
		final CubeBuilder cubeBuilder = new CubeBuilder(cubeKey);
		cubeBuilder.withCube(cube);

		final Cube oldCube = loadStore(cubeKey).get(cubeKey);
		if (oldCube != null) {
			cubeBuilder.withCube(oldCube);
		}
		loadStore(cubeKey).put(cubeKey, cubeBuilder.build());
	}

	private SortedMap<CubePosition, Cube> loadStore(final CubePosition key) {
		return store.get(key.getTimePosition().getDimension()).get(key.getWhatPosition().getDimension());
	}

	private List<Cube> load(final CubePosition fromKey, final CubePosition toKey) {//fromKey inclus, toKey exclus 
		Assertion.precondition(fromKey.getTimePosition().getDimension().equals(toKey.getTimePosition().getDimension()), "La dimension temporelle du from et du to doit être la même from:{1} != to:{0}", fromKey.getTimePosition().getDimension(), toKey.getTimePosition().getDimension());
		Assertion.precondition(fromKey.getWhatPosition().getDimension().equals(toKey.getWhatPosition().getDimension()), "La dimension sémantique du from et du to doit être la même from:{1} != to:{0}", fromKey.getWhatPosition().getDimension(), toKey.getWhatPosition().getDimension());
		//---------------------------------------------------------------------
		final SortedMap<CubePosition, Cube> dimensionStore = loadStore(fromKey);
		final SortedMap<CubePosition, Cube> subStore = dimensionStore.subMap(fromKey, toKey);
		final List<Cube> result = new ArrayList<Cube>(subStore.values());
		return result;
	}

	/** {@inheritDoc} */
	public List<Cube> load(final TimeSelection timeSelection, final boolean aggregateTime, final WhatSelection whatSelection, final boolean aggregateWhat, final List<DataKey> metrics) {
		//On prépare les bornes de temps
		final TimePosition minTimePosition = timeSelection.getMinTimePosition();
		final TimePosition maxTimePosition = timeSelection.getMaxTimePosition();

		//On remplit une liste de cube avec tous les what voulu
		final List<Cube> allCubes = new ArrayList<Cube>();
		for (final String whatValue : whatSelection.getWhatValues()) {
			final WhatPosition minWhat = new WhatPosition(whatValue, whatSelection.getDimension());
			final WhatPosition maxWhat = new WhatPosition(whatValue + LAST_CHAR, whatSelection.getDimension());
			final CubePosition fromKey = new CubePosition(minTimePosition, minWhat);
			final CubePosition toKey = new CubePosition(maxTimePosition, maxWhat);
			allCubes.addAll(load(fromKey, toKey));
		}
		//On prepare un index de metric attendu
		final Set<String> metricNames = new HashSet<String>();
		final Set<String> metaDataNames = new HashSet<String>();
		for (final DataKey dataKey : metrics) {
			if (dataKey.getType() == DataType.metaData) {
				metaDataNames.add(dataKey.getName());
			} else {
				metricNames.add(dataKey.getName());
			}
		}

		//On aggrege les metrics/meta demandées en fonction des parametres 
		final WhatPosition allWhat = new WhatPosition(WhatDimension.SEPARATOR, whatSelection.getDimension());
		final SortedMap<CubePosition, CubeBuilder> cubeBuilderIndex = new TreeMap<CubePosition, CubeBuilder>(cubeKeyComparator);
		for (final Cube cube : allCubes) {
			//Si on aggrege sur une dimension, on la fige plutot que prendre la position de la donnée
			final WhatPosition useWhat = aggregateWhat ? allWhat : cube.getKey().getWhatPosition();
			final TimePosition useTime = aggregateTime ? minTimePosition : cube.getKey().getTimePosition();
			final CubeBuilder cubeBuilder = obtainCubeBuilder(useTime, useWhat, cubeBuilderIndex);

			for (final Metric metric : cube.getMetrics()) {
				if (metricNames.contains(metric.getName())) {
					cubeBuilder.withMetric(metric);
				}
			}
			for (final MetaData metaData : cube.getMetaDatas()) {
				if (metaDataNames.contains(metaData.getName())) {
					cubeBuilder.withMetaData(metaData);
				}
			}
		}
		final List<Cube> cube = new ArrayList<Cube>(cubeBuilderIndex.size());
		for (final CubeBuilder cubeBuilder : cubeBuilderIndex.values()) {
			cube.add(cubeBuilder.build());
		}
		return cube;
	}

	private CubeBuilder obtainCubeBuilder(final TimePosition timePosition, final WhatPosition whatPosition, final SortedMap<CubePosition, CubeBuilder> timeIndex) {
		final CubePosition key = new CubePosition(timePosition, whatPosition);
		CubeBuilder cubeBuilder = timeIndex.get(key);
		if (cubeBuilder == null) {
			cubeBuilder = new CubeBuilder(key);
			timeIndex.put(key, cubeBuilder);
		}
		return cubeBuilder;
	}

	/** {@inheritDoc} */
	public List<TimePosition> loadSubTimePositions(final TimeSelection timeSelection) {
		final TimeDimension timeDimension = timeSelection.getDimension().drillDown();
		if (timeDimension == null) {
			return Collections.emptyList();
		}
		final TimePosition minTime = timeSelection.getMinTimePosition();
		final TimePosition maxTime = timeSelection.getMaxTimePosition();

		final WhatPosition minWhat = new WhatPosition("/", WhatDimension.Global);
		final WhatPosition maxWhat = new WhatPosition("/" + LAST_CHAR, WhatDimension.Global);
		final CubePosition fromKey = new CubePosition(minTime, minWhat);
		final CubePosition toKey = new CubePosition(maxTime, maxWhat);
		final List<Cube> allCubes = load(fromKey, toKey);

		final SortedSet<TimePosition> result = new TreeSet<TimePosition>();
		for (final Cube cube : allCubes) {
			result.add(cube.getKey().getTimePosition());
		}

		return new ArrayList<TimePosition>(result);
	}

	/** {@inheritDoc} */
	public List<WhatPosition> loadSubWhatPositions(final TimeSelection timeSelection, final WhatSelection whatSelection) {
		final TimePosition minTime = timeSelection.getMinTimePosition();
		final TimePosition maxTime = timeSelection.getMaxTimePosition();

		final WhatDimension whatDimension = whatSelection.getDimension().drillDown();
		if (whatDimension == null) {
			return Collections.emptyList();
		}
		final List<Cube> allCubes = new ArrayList<Cube>();
		for (final String whatValue : whatSelection.getWhatValues()) {
			final WhatPosition minWhat = new WhatPosition(whatValue, whatDimension);
			final WhatPosition maxWhat = new WhatPosition(whatValue + LAST_CHAR, whatDimension);
			final CubePosition fromKey = new CubePosition(minTime, minWhat);
			final CubePosition toKey = new CubePosition(maxTime, maxWhat);
			allCubes.addAll(load(fromKey, toKey));
		}

		final SortedSet<WhatPosition> result = new TreeSet<WhatPosition>();
		for (final Cube cube : allCubes) {
			result.add(cube.getKey().getWhatPosition());
		}

		return new ArrayList<WhatPosition>(result);
	}

	/** {@inheritDoc} */
	public List<DataKey> loadDataKeys(final TimeSelection timeSelection, final WhatSelection whatSelection) {
		final TimePosition minTime = timeSelection.getMinTimePosition();
		final TimePosition maxTime = timeSelection.getMaxTimePosition();

		final WhatDimension whatDimension = whatSelection.getDimension().drillDown();
		final List<Cube> allCubes = new ArrayList<Cube>();
		for (final String whatValue : whatSelection.getWhatValues()) {
			final WhatPosition minWhat = new WhatPosition(whatValue, whatDimension);
			final WhatPosition maxWhat = new WhatPosition(whatValue + LAST_CHAR, whatDimension);
			final CubePosition fromKey = new CubePosition(minTime, minWhat);
			final CubePosition toKey = new CubePosition(maxTime, maxWhat);
			allCubes.addAll(load(fromKey, toKey));
		}

		final Set<String> resultMetric = new HashSet<String>();
		final Set<String> resultMetadata = new HashSet<String>();
		for (final Cube cube : allCubes) {
			for (final Metric metric : cube.getMetrics()) {
				resultMetric.add(metric.getName());
			}
			for (final MetaData metaData : cube.getMetaDatas()) {
				resultMetadata.add(metaData.getName());
			}
		}
		final List<DataKey> result = new ArrayList<DataKey>();
		for (final String metric : resultMetric) {
			result.add(new DataKey(metric, DataType.count));
		}
		for (final String metaData : resultMetadata) {
			result.add(new DataKey(metaData, DataType.metaData));
		}
		return result;
	}

	/** {@inheritDoc} */
	public String loadLastProcessIdStored() {
		return lastProcessIdStored;
	}

	/** {@inheritDoc} */
	public void saveLastProcessIdStored(final String newLastProcessIdStored) {
		lastProcessIdStored = newLastProcessIdStored;
	}
}
