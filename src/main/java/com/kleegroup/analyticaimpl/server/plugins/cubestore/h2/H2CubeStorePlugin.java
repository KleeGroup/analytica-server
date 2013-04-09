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
package com.kleegroup.analyticaimpl.server.plugins.cubestore.h2;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import kasper.kernel.lang.Activeable;
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
import com.kleegroup.analytica.hcube.query.Query;
import com.kleegroup.analytica.hcube.query.TimeSelection;
import com.kleegroup.analytica.server.data.DataKey;
import com.kleegroup.analytica.server.data.DataType;
import com.kleegroup.analyticaimpl.server.CubeStorePlugin;
import com.kleegroup.analyticaimpl.server.plugins.cubestore.TicTac;
import com.kleegroup.analyticaimpl.server.plugins.cubestore.h2.dao.DaoException;
import com.kleegroup.analyticaimpl.server.plugins.cubestore.h2.dao.H2DataBase;
import com.kleegroup.analyticaimpl.server.plugins.cubestore.h2.dao.H2DataBaseImpl;

/**
 * Implémentation sur base H2 du stockage des Cubes.
 * @author npiedeloup
 * @version $Id: H2CubeStorePlugin.java,v 1.15 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class H2CubeStorePlugin implements CubeStorePlugin, Activeable {
	private final TicTac tic = new TicTac("CubeStore");
	private final H2DataBase store;
	private final CubeStatements statements;
	private final Map<CubePosition, Cube> naiveCache = new HashMap<CubePosition, Cube>();

	private final PriorityQueue<Cube> writeBuffer = new PriorityQueue<Cube>(100, new Comparator<Cube>() {
		@Override
		public int compare(final Cube o1, final Cube o2) {
			if (o1 == null) {
				return -1;
			} else if (o2 == null) {
				return 1;
			}
			final CubePosition k1 = o1.getKey();
			final CubePosition k2 = o2.getKey();
			return k1.getTimePosition().getValue().compareTo(k2.getTimePosition().getValue());
		}
	});
	/*private final SortedMap<CubeKey, Cube> writeBuffer = new TreeMap<CubeKey, Cube>(new Comparator<CubeKey>() {
		@Override
		public int compare(final CubeKey o1, final CubeKey o2) {
			return o1.getTimePosition().getValue().compareTo(o2.getTimePosition().getValue());
		}
	});*/
	private String writeBufferLastProcessIdStored;

	/**
	 * Constructeur.
	 * @param dbUrl Url de la base
	 * @param dbLogin Login
	 * @param dbPassword Password
	 * @throws DaoException exception à la création de la BDD
	 */
	@Inject
	public H2CubeStorePlugin(@Named("dbUrl") final String dbUrl, @Named("dbLogin") final String dbLogin, @Named("dbPassword") final String dbPassword) throws DaoException {
		super();
		store = new H2DataBaseImpl(dbUrl, dbLogin, dbPassword);
		statements = new CubeStatements(store);
		if (!store.isBddCreated()) {
			statements.createDataBase();
		}
	}

	/** {@inheritDoc} */
	public void merge(final Cube lowLevelCube) {
		Assertion.notNull(lowLevelCube);
		//---------------------------------------------------------------------
		//on remonte les axes, le premier sera le plus bas niveau
		try {
			final Connection conn = store.getConnection();
			try {
				TimePosition timePosition = lowLevelCube.getKey().getTimePosition();
				while (timePosition != null) {
					WhatPosition whatPosition = lowLevelCube.getKey().getWhatPosition();
					while (whatPosition != null) {
						final CubePosition storedCubeKey = new CubePosition(timePosition, whatPosition);
						store(lowLevelCube, storedCubeKey, conn);
						//On remonte what
						whatPosition = whatPosition.drillUp();
					}
					//On remonte time
					timePosition = timePosition.drillUp();
				}
				flushWriteBuffer(conn);
			} finally {
				store.close(conn);
			}
		} catch (final DaoException e) {
			throw new RuntimeException("Erreur lors de l'accès BDD", e);
		}
	}

	private long cacheUse = 0;
	private long cacheMiss = 0;
	private long cacheMissNoData = 0;
	private long writeData = 0;

	private void store(final Cube cube, final CubePosition cubeKey, final Connection conn) throws DaoException {
		tic.tic("store");
		tic.tic("mergeCube");
		final CubeBuilder cubeBuilder = new CubeBuilder(cubeKey);
		cubeBuilder.withCube(cube);
		tic.tac("mergeCube");
		tic.tic("loadCube");
		Cube oldCube = naiveCache.get(cubeKey);
		cacheUse++;
		if (oldCube == null) {
			cacheMiss++;
			oldCube = statements.loadCube(cubeKey, conn);
			if (oldCube == null) {
				cacheMissNoData++;
			}
		}
		tic.tac("loadCube");
		tic.tic("mergeCube2");
		if (oldCube != null) {
			cubeBuilder.withCube(oldCube);
		}
		tic.tac("mergeCube2");
		final Cube mergedCube = cubeBuilder.build();
		writeBuffer.offer(mergedCube);
		checkWriteBuffer(conn); //On garde les cube a écrire pour faire de l'écriture en masse
		//
		naiveCache.put(cubeKey, mergedCube);

		if (naiveCache.size() > 10000) {
			naiveCache.clear();
		}
		if (cacheUse % 5000 == 0) {
			System.out.println("Cache ratio " + (cacheUse - cacheMiss) * 10000L / cacheUse / 100d + "%   noData:" + cacheMissNoData * 10000L / cacheMiss / 100d + "  size:" + naiveCache.size() + " writeCount:" + writeData);
		}
		tic.tac("store");
	}

	private void computeAndStoreCube(final CubePosition cubeKey) throws DaoException {
		tic.tic("computeAndStoreCube");
		//final CubeBuilder cubeBuilder = new CubeBuilder(cubeKey.getTimePosition(), cubeKey.getWhatPosition());
		List<Cube> allCubes;
		final TimeDimension timeDimension = cubeKey.getTimePosition().getDimension();
		final WhatDimension whatDimension = cubeKey.getWhatPosition().getDimension();
		final TimeDimension lowerTimeDimension = timeDimension.drillDown() != null ? timeDimension.drillDown() : TimeDimension.Minute;
		final WhatDimension lowerWhatDimension = whatDimension.drillDown() != null ? whatDimension.drillDown() : WhatDimension.FullName;
		final Date startDate = cubeKey.getTimePosition().getValue();
		final Connection connection = store.getConnection();
		try {
			tic.tic("loadCubes");
			//On tente d'abord sur la même dimension temps, niveau inférieur sur What
			allCubes = statements.loadCubes(timeDimension, lowerWhatDimension, startDate, timeDimension.getMaxDate(startDate), connection);
			if (allCubes.isEmpty()) {
				//Puis sur la même dimension what, niveau inférieur sur le temps
				allCubes = statements.loadCubes(lowerTimeDimension, whatDimension, startDate, timeDimension.getMaxDate(startDate), connection);
			}
			if (allCubes.isEmpty()) {
				//Sinon on cherche niveau en dessous sur toutes les dimensions
				allCubes = statements.loadCubes(lowerTimeDimension, lowerWhatDimension, startDate, timeDimension.getMaxDate(startDate), connection);
			}
			tic.tac("loadCubes");
			if (allCubes.isEmpty() && !(lowerTimeDimension == TimeDimension.Minute && lowerWhatDimension == WhatDimension.FullName)) {
				tic.tic("subComputeAndStoreCube");
				//Sinon on construit le niveau du dessous
				final WhatPosition newWhat = new WhatPosition(cubeKey.getWhatPosition().getValue(), lowerWhatDimension);
				for (Date newDate = startDate; newDate.before(timeDimension.getMaxDate(startDate)); newDate = lowerTimeDimension.getMaxDate(newDate)) {
					final TimePosition newTime = new TimePosition(newDate, lowerTimeDimension);
					final CubePosition newCubeKey = new CubePosition(newTime, newWhat);
					computeAndStoreCube(newCubeKey);
				}
				allCubes = statements.loadCubes(lowerTimeDimension, lowerWhatDimension, startDate, timeDimension.getMaxDate(startDate), connection);
				//Assertion.postcondition(!allCubes.isEmpty(), "La liste des cubes est toujours vide, pour {0}", cubeKey);
				tic.tac("subComputeAndStoreCube");
			}
			tic.tic("cubeBuilderIndex");
			//On aggrege les metrics/meta 
			final SortedMap<CubePosition, CubeBuilder> cubeBuilderIndex = new TreeMap<CubePosition, CubeBuilder>();
			for (final Cube cube : allCubes) {
				final WhatPosition useWhat = new WhatPosition(cube.getKey().getWhatPosition().getValue(), whatDimension);
				final TimePosition useTime = new TimePosition(cube.getKey().getTimePosition().getValue(), timeDimension);
				final CubePosition cubePosition = new CubePosition(useTime, useWhat);
				final CubeBuilder cubeBuilder = obtainCubeBuilder(cubePosition, cubeBuilderIndex);
				cubeBuilder.withCube(cube);
				//A valider !!!!!!!
				//A valider !!!!!!!
				//A valider !!!!!!!
				//A valider !!!!!!!
				//				for (final Metric metric : cube.getMetrics()) {
				//					cubeBuilder.withMetric(metric);
				//				}
				//				for (final MetaData metaData : cube.getMetaDatas()) {
				//					cubeBuilder.withMetaData(metaData);
				//				}
			}
			tic.tac("cubeBuilderIndex");
			tic.tic("saveCubes");
			for (final CubeBuilder cubeBuilder : cubeBuilderIndex.values()) {
				statements.saveCube(cubeBuilder.build(), connection);
			}
			//Logger.getLogger(getClass()).info("StoreUpperCube :" + cubeKey);
			tic.tac("saveCubes");
			store.commit(connection);
			tic.reportAllEveryX(200);
		} finally {
			store.close(connection);
		}
		tic.tac("computeAndStoreCube");
	}

	private void checkWriteBuffer(final Connection conn) throws DaoException {
		//on ecrit si il y a plus de 50 cubes à écrire OU si il y a plusieurs cubes minutes
		//if (writeBuffer.size() > 50 || writeBuffer.firstKey().getTimePosition().getValue().before(writeBuffer.lastKey().getTimePosition().getValue())) {
		if (writeBuffer.size() > 100) {
			flushWriteBuffer(conn);
		}
	}

	private void flushWriteBuffer(final Connection conn) throws DaoException {
		if (!writeBuffer.isEmpty()) {
			Cube cube = writeBuffer.poll();
			while (cube != null) {
				tic.tic("saveCube");
				statements.saveCube(cube, conn);
				tic.tic("saveCube");
				writeData++;
				cube = writeBuffer.poll(); //next
			}
		}
		if (writeBufferLastProcessIdStored != null) { //s'il y a eut une modif du lastProcessIdStored
			statements.saveLastProcessIdStored(writeBufferLastProcessIdStored, conn);
		}
		store.commit(conn);
		writeBufferLastProcessIdStored = null;
	}

	/** {@inheritDoc} */
	public List<Cube> load(final Query query, final boolean aggregateTime, final boolean aggregateWhat, final List<DataKey> metrics) {
		tic.tic("load");//On prépare les bornes de temps
		final TimePosition minTime = query.getTimeSelection().getMinTimePosition();
		//final TimePosition maxTime = new TimePosition(timeSelection.getMaxValue(), timeSelection.getDimension());

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
		List<Cube> allCubes;
		try {
			tic.tic("loadCubes");
			final Connection connection = store.getConnection();
			try {
				allCubes = statements.loadCubes(query, metricNames, metaDataNames, aggregateTime, aggregateWhat, connection);
				if (allCubes.isEmpty()) {
					tic.tic("atRunTimeComputeAndStoreCube");
					//Sinon on construit le niveau du dessous
					for (Date newDate = minTime.getValue(); newDate.before(query.getTimeSelection().getDimension().getMaxDate(query.getTimeSelection().getMaxValue())); newDate = query.getTimeSelection().getDimension().getMaxDate(newDate)) {
						final TimePosition newTime = new TimePosition(newDate, query.getTimeSelection().getDimension());
						for (final String whatValue : query.getWhatSelection().getWhatValues()) {
							final WhatPosition newWhat = new WhatPosition(whatValue, query.getWhatSelection().getDimension());
							final CubePosition newCubeKey = new CubePosition(newTime, newWhat);
							computeAndStoreCube(newCubeKey);
						}
					}
					allCubes = statements.loadCubes(query, metricNames, metaDataNames, aggregateTime, aggregateWhat, connection);
					Assertion.postcondition(!allCubes.isEmpty(), "La liste des cubes est toujours vide pendant le load");
					tic.tac("atRunTimeComputeAndStoreCube");
				}
				//allCubes = statements.loadCubes(TimeDimension.Minute, WhatDimension.FullName, timeSelection.getMinValue(), timeSelection.getMaxValue(), whatSelection.getWhatValues(), metricNames, metaDataNames, aggregateTime, aggregateWhat, connection);
				//pas de commit : ReadOnly
			} finally {
				store.close(connection);
			}
			tic.tac("loadCubes");
		} catch (final DaoException e) {
			throw new RuntimeException("Erreur lors du chargement de cube", e);
		}
		tic.tic("cubeBuilderIndex");
		//On aggrege les metrics/meta demandées en fonction des parametres 
		final WhatPosition allWhat = new WhatPosition(WhatDimension.SEPARATOR, query.getWhatSelection().getDimension());
		final SortedMap<CubePosition, CubeBuilder> cubeBuilderIndex = new TreeMap<CubePosition, CubeBuilder>();
		for (final Cube cube : allCubes) {
			//Si on aggrege sur une dimension, on la fige plutot que prendre la position de la donnée
			final WhatPosition useWhat = aggregateWhat ? allWhat : drillUp(cube.getKey().getWhatPosition(), query.getWhatSelection().getDimension());
			final TimePosition useTime = aggregateTime ? minTime : drillUp(cube.getKey().getTimePosition(), query.getTimeSelection().getDimension());

			final CubePosition cubePosition = new CubePosition(useTime, useWhat);

			final CubeBuilder cubeBuilder = obtainCubeBuilder(cubePosition, cubeBuilderIndex);
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
		tic.tac("cubeBuilderIndex");
		tic.tic("cubes.add");
		final List<Cube> cubes = new ArrayList<Cube>(cubeBuilderIndex.size());
		for (final CubeBuilder cubeBuilder : cubeBuilderIndex.values()) {
			cubes.add(cubeBuilder.build());
		}
		if (cubes.size() == allCubes.size()) {
			System.out.println("------- Agregation inutile");
		} else {
			System.out.println("###############################################################");
			System.out.println("###############################################################");
			System.out.println("###############################################################");
			System.out.println("#######  Agregation UTILE  ####################################");
			System.out.println("###############################################################");
			System.out.println("###############################################################");
			System.out.println("###############################################################");
		}
		tic.tac("cubes.add");
		tic.tac("load");

		tic.reportAllEveryX(20);
		return cubes;
	}

	private WhatPosition drillUp(final WhatPosition whatPosition, final WhatDimension dimension) {
		WhatPosition resultWhatPosition = whatPosition;
		while (resultWhatPosition.getDimension() != dimension) {
			resultWhatPosition = resultWhatPosition.drillUp();
		}
		return resultWhatPosition;
	}

	private TimePosition drillUp(final TimePosition timePosition, final TimeDimension dimension) {
		TimePosition resultTimePosition = timePosition;
		while (resultTimePosition.getDimension() != dimension) {
			resultTimePosition = resultTimePosition.drillUp();
		}
		return resultTimePosition;
	}

	private CubeBuilder obtainCubeBuilder(CubePosition cubePosition, final SortedMap<CubePosition, CubeBuilder> timeIndex) {
		CubeBuilder cubeBuilder = timeIndex.get(cubePosition);
		if (cubeBuilder == null) {
			cubeBuilder = new CubeBuilder(cubePosition);
			timeIndex.put(cubePosition, cubeBuilder);
		}
		return cubeBuilder;
	}

	/** {@inheritDoc} */
	public List<TimePosition> loadSubTimePositions(final TimeSelection timeSelection) {
		final TimeDimension timeDimension = timeSelection.getDimension();
		final TimeDimension subTimeDimension = timeDimension.drillDown();
		if (subTimeDimension == null) {
			//Si minute, on retourne une liste vide
			return Collections.emptyList();
		}

		final List<TimePosition> timePositions;
		try {
			final Connection connection = store.getConnection();
			try {
				timePositions = statements.loadTimePositions(subTimeDimension, timeSelection.getMinValue(), timeSelection.getMaxValue(), connection);
				//pas de commit : ReadOnly
			} finally {
				store.close(connection);
			}
		} catch (final DaoException e) {
			throw new RuntimeException("Erreur lors du chargement des TimePositions", e);
		}
		return timePositions;
	}

	/** {@inheritDoc} */
	public List<WhatPosition> loadSubWhatPositions(final Query query) {
		final WhatDimension whatDimension = query.getWhatSelection().getDimension();
		final WhatDimension subWhatDimension = whatDimension.drillDown();
		if (subWhatDimension == null) {
			return Collections.emptyList();
			//Si FullName, on retourne une liste vide
		}

		final List<WhatPosition> whatPositions;
		try {
			final Connection connection = store.getConnection();
			try {
				whatPositions = statements.loadWhatPositions(query.getTimeSelection().getDimension(), subWhatDimension, query.getTimeSelection().getMinValue(), query.getTimeSelection().getMaxValue(), query.getWhatSelection().getWhatValues(), connection);
				//pas de commit : ReadOnly
			} finally {
				store.close(connection);
			}
		} catch (final DaoException e) {
			throw new RuntimeException("Erreur lors du chargement des WhatPositions", e);
		}
		return whatPositions;
	}

	/** {@inheritDoc} */
	public List<DataKey> loadDataKeys(final Query query) {
		final List<DataKey> dataKeys;
		try {
			final Connection connection = store.getConnection();
			try {
				dataKeys = statements.loadDataKeys(query, connection);
				//pas de commit : ReadOnly
			} finally {
				store.close(connection);
			}
		} catch (final DaoException e) {
			throw new RuntimeException("Erreur lors du chargement des TimePositions", e);
		}
		return dataKeys;
	}

	/** {@inheritDoc} */
	public String loadLastProcessIdStored() {
		try {
			final Connection connection = store.getConnection();
			try {
				if (writeBufferLastProcessIdStored != null) {
					return writeBufferLastProcessIdStored;
				} else {
					return statements.loadLastProcessIdStored(connection);
				}
				//pas de commit : ReadOnly
			} finally {
				store.close(connection);
			}
		} catch (final DaoException e) {
			throw new RuntimeException("Erreur lors du chargement du LastProcessIdStored", e);
		}
	}

	/** {@inheritDoc} */
	public void saveLastProcessIdStored(final String lastProcessIdStored) {
		writeBufferLastProcessIdStored = lastProcessIdStored;
		//		try {
		//			final Connection connection = store.getConnection();
		//			try {
		//				statements.saveLastProcessIdStored(lastProcessIdStored, connection);
		//				store.commit(connection);
		//			} finally {
		//				store.close(connection);
		//			}
		//		} catch (final DaoException e) {
		//			throw new RuntimeException("Erreur lors du chargement du LastProcessIdStored", e);
		//		}
	}

	/** {@inheritDoc} */
	public void start() {
		//rien
	}

	/** {@inheritDoc} */
	public void stop() {
		try {
			final Connection conn = store.getConnection();
			try {
				flushWriteBuffer(conn);
				store.commit(conn); //Le flush effectue déjà le commit
			} finally {
				store.close(conn);
			}
		} catch (final DaoException e) {
			throw new RuntimeException("Erreur lors de l'accès BDD", e);
		}
	}
}
