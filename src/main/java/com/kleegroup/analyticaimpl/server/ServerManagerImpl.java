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
package com.kleegroup.analyticaimpl.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import kasper.kernel.lang.Activeable;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.server.ServerManager;
import com.kleegroup.analytica.server.data.Data;
import com.kleegroup.analytica.server.data.DataKey;
import com.kleegroup.analytica.server.data.DataSet;
import com.kleegroup.analytica.server.data.DataType;
import com.kleegroup.analytica.server.data.TimeDimension;
import com.kleegroup.analytica.server.data.TimeSelection;
import com.kleegroup.analytica.server.data.WhatSelection;
import com.kleegroup.analyticaimpl.server.cube.Cube;
import com.kleegroup.analyticaimpl.server.cube.MetaData;
import com.kleegroup.analyticaimpl.server.cube.Metric;
import com.kleegroup.analyticaimpl.server.cube.WhatPosition;

/**
 * Manager Serveur d'Analytica.
 * @author npiedeloup
 * @version $Id: ServerManagerImpl.java,v 1.22 2013/01/14 16:35:19 npiedeloup Exp $
 */
public final class ServerManagerImpl implements ServerManager, Activeable {

	private static final long HOUR_TIME_MILLIS = 60 * 60 * 1000;
	private final ProcessStorePlugin processStorePlugin;
	private final ProcessEncoderPlugin encoderPlugin;
	private final CubeStorePlugin cubeStorePlugin;
	private Timer asyncCubeStoreTimer = null;

	/**
	 * Constructeur.
	 * @param processStorePlugin Plugin de stockage des Process
	 * @param encoderPlugin Plugin de converssion de process en cube
	 * @param cubeStorePlugin Plugin de stockage des Cubes
	 */
	@Inject
	public ServerManagerImpl(final ProcessStorePlugin processStorePlugin, final ProcessEncoderPlugin encoderPlugin, final CubeStorePlugin cubeStorePlugin) {
		super();
		Assertion.notNull(processStorePlugin);
		Assertion.notNull(encoderPlugin);
		Assertion.notNull(cubeStorePlugin);
		//-----------------------------------------------------------------
		this.processStorePlugin = processStorePlugin;
		this.encoderPlugin = encoderPlugin;
		this.cubeStorePlugin = cubeStorePlugin;
	}

	/** {@inheritDoc} */
	public void push(final KProcess process) {
		processStorePlugin.add(process);
	}

	/** {@inheritDoc} */
	public int store50NextProcessesAsCube() {
		final String lastProcessIdStored = cubeStorePlugin.loadLastProcessIdStored();
		final List<Identified<KProcess>> nextProcesses = processStorePlugin.getProcess(lastProcessIdStored, 200);
		for (final Identified<KProcess> process : nextProcesses) {
			//System.out.println("READ " + process.getKey());
			storeAsCube(process.getData());
			cubeStorePlugin.saveLastProcessIdStored(process.getKey());
		}
		return nextProcesses.size();
	}

	private void storeAsCube(final KProcess process) {
		//Encode le process et ses sous process
		final List<Cube> cubes = encoderPlugin.encode(process);
		for (final Cube cube : cubes) {
			cubeStorePlugin.merge(cube);
		}
	}

	/** {@inheritDoc} */
	public List<Data> getData(final TimeSelection timeSelection, final WhatSelection whatSelection, final List<DataKey> metrics) {
		final List<Cube> aggregatedCubes = cubeStorePlugin.load(timeSelection, true, whatSelection, true, metrics);
		if (aggregatedCubes.isEmpty()) {
			return Collections.emptyList(); //TODO npi que faire si pas de ligne, l'aggregation devrait retourner toujours une ligne, non ?
		}
		//---------------------------------------------------------------------
		Assertion.postcondition(aggregatedCubes.size() == 1, "La liste de cube doit être agrégée sur tout les axes, il doit dont y avoir un seul élément dans la liste (size:{0})", aggregatedCubes.size());
		//---------------------------------------------------------------------
		final Cube aggregatedCube = aggregatedCubes.get(0);
		//On convertit le cube en liste de Data : 1 par metrics
		final List<Data> datas = new ArrayList<Data>(metrics.size());
		for (final DataKey dataKey : metrics) {
			if (dataKey.getType() == DataType.metaData) {
				final List<String> metaDataValues = new ArrayList<String>();
				for (final MetaData metaData : aggregatedCube.getMetaData(dataKey.getName())) {
					metaDataValues.add(metaData.getValue());
				}
				datas.add(new Data(dataKey, metaDataValues));
			} else {
				datas.add(new Data(dataKey, getCubeValue(aggregatedCube, dataKey)));
			}
		}
		return datas;
	}

	/** {@inheritDoc} */
	public List<DataSet<Date, ?>> getDataTimeLine(final TimeSelection timeSelection, final WhatSelection whatSelection, final List<DataKey> metrics) {
		final List<Cube> aggregatedCubes = cubeStorePlugin.load(timeSelection, false, whatSelection, true, metrics);
		//On convertit la liste de cube en liste de DataSet : 1 par metrics
		final List<DataSet<Date, ?>> datas = convertToDataSet(aggregatedCubes, true, metrics);
		return datas;
	}

	/** {@inheritDoc} */
	public List<DataSet<String, ?>> getDataWhatLine(final TimeSelection timeSelection, final WhatSelection whatSelection, final List<DataKey> metrics) {
		final List<Cube> aggregatedCubes = cubeStorePlugin.load(timeSelection, true, whatSelection, false, metrics);
		//On convertit la liste de cube en liste de DataSet : 1 par metrics
		final List<DataSet<String, ?>> datas = convertToDataSet(aggregatedCubes, false, metrics);
		return datas;
	}

	private <X> List<DataSet<X, ?>> convertToDataSet(final List<Cube> aggregatedCubes, final boolean dateAsLabels, final List<DataKey> metrics) {
		final List<X> labels = new ArrayList<X>(aggregatedCubes.size());
		final Map<DataKey, List<Double>> valuesMap = new HashMap<DataKey, List<Double>>();
		final Map<DataKey, List<Set<String>>> metaDatasMap = new HashMap<DataKey, List<Set<String>>>();
		//1- on prépare les listes de valeurs et de metaData
		for (final DataKey dataKey : metrics) {
			if (dataKey.getType() == DataType.metaData) {
				metaDatasMap.put(dataKey, new ArrayList<Set<String>>());
			} else {
				valuesMap.put(dataKey, new ArrayList<Double>());
			}
		}
		//2- On parcour les cubes et on remplit les données
		for (final Cube cube : aggregatedCubes) {
			//Si la liste des cubes est limité on peut éviter ce if, 
			//mais il reste intéréssant de remplir les labels au même endroit que les valeurs
			if (dateAsLabels) {
				labels.add((X) cube.getKey().getTimePosition().getValue());
			} else {
				labels.add((X) cube.getKey().getWhatPosition().getValue());
			}
			for (final DataKey dataKey : metrics) {
				if (dataKey.getType() == DataType.metaData) {
					final List<Set<String>> metadatas = metaDatasMap.get(dataKey);
					final Set<String> values = new HashSet<String>();
					for (final MetaData metaData : cube.getMetaData(dataKey.getName())) {
						values.add(metaData.getValue());
					}
					metadatas.add(values);
				} else {
					final List<Double> values = valuesMap.get(dataKey);
					values.add(getCubeValue(cube, dataKey));
				}
			}
		}

		//3- On crée les DataSet
		final List<DataSet<X, ?>> datas = new ArrayList<DataSet<X, ?>>(metrics.size());
		for (final DataKey dataKey : metrics) {
			if (dataKey.getType() == DataType.metaData) {
				datas.add(new DataSet<X, Set<String>>(dataKey, labels, metaDatasMap.get(dataKey)));
			} else {
				datas.add(new DataSet<X, Double>(dataKey, labels, valuesMap.get(dataKey)));
			}
		}
		return datas;
	}

	private Double getCubeValue(final Cube cube, final DataKey dataKey) {
		final Metric metric = cube.getMetric(dataKey.getName());
		//Assertion.notNull(metric,"La metric {0} n''a pas été trouvée dans le cube {1}", dataKey.getName(), cube.getKey());
		//---------------------------------------------------------------------
		if (metric == null) { //la metric peut-être null sur certain cube (exemple 'CACHE_HIT' n'est présent que sur quelques cubes)
			return null;
		}
		return metric.get(dataKey.getType().name());
	}

	/** {@inheritDoc} */
	public List<TimeSelection> getSubTimeSelections(final TimeSelection timeSelection) {
		final List<TimeSelection> result;
		final TimeDimension subTimeDimension;
		//final TimeDimension subTimeDimension = getSubTimeDimension(timeSelection.getDimension());
		switch (timeSelection.getDimension()) {
			case Year:
				subTimeDimension = TimeDimension.Month;
				result = createSubTimeSelection(timeSelection, subTimeDimension, 3L * 31 * 24 * HOUR_TIME_MILLIS);
				break;
			case Month:
				subTimeDimension = TimeDimension.Day;
				result = createSubTimeSelection(timeSelection, subTimeDimension, 7L * 24 * HOUR_TIME_MILLIS);
				break;
			case Day:
				subTimeDimension = TimeDimension.Hour;
				result = createSubTimeSelection(timeSelection, subTimeDimension, 24L * HOUR_TIME_MILLIS);
				break;
			case Hour:
				subTimeDimension = TimeDimension.Minute;
				result = createSubTimeSelection(timeSelection, subTimeDimension, 6L * HOUR_TIME_MILLIS);
				break;
			case Minute:
				result = Collections.emptyList();
				break;
			default:
				throw new IllegalArgumentException("TimeDimension inconnue : " + timeSelection.getDimension());
		}

		//return cubeStorePlugin.loadSubTimeSelections(timeSelection);
		return result;
	}

	private List<TimeSelection> createSubTimeSelection(final TimeSelection timeSelection, final TimeDimension subTimeDimension, final long timeStepMillis) {
		final List<TimeSelection> result = new ArrayList<TimeSelection>();
		Date currentMaxDate;
		for (Date currentMinDate = timeSelection.getMinValue(); currentMinDate.before(timeSelection.getMaxValue()); currentMinDate = currentMaxDate) {
			currentMaxDate = new Date(currentMinDate.getTime() + timeStepMillis);
			final TimeSelection newTimeSelection = new TimeSelection(currentMinDate, currentMaxDate, subTimeDimension);
			result.add(newTimeSelection);
		}
		return result;
	}

	/** {@inheritDoc} */
	public List<WhatSelection> getSubWhatSelections(final TimeSelection timeSelection, final WhatSelection whatSelection) {
		final List<WhatPosition> subWhatPositions = cubeStorePlugin.loadSubWhatPositions(timeSelection, whatSelection);
		final List<WhatSelection> result = new ArrayList<WhatSelection>();
		for (final WhatPosition subWhatPosition : subWhatPositions) {
			result.add(new WhatSelection(subWhatPosition.getDimension(), subWhatPosition.getValue()));
		}
		return result;
	}

	/** {@inheritDoc} */
	public List<DataKey> getSubDataKeys(final TimeSelection timeSelection, final WhatSelection whatSelection) {
		return cubeStorePlugin.loadDataKeys(timeSelection, whatSelection);
	}

	/** {@inheritDoc} */
	public void start() {
		asyncCubeStoreTimer = new Timer(true);
		final TimerTask storeCubeTask = new StoreCubeTask();
		asyncCubeStoreTimer.schedule(storeCubeTask, 1000, 250); //50processes toutes les 250ms.. pour la vrai vie ok, pour les tests unitaires pas suffisant
	}

	/** {@inheritDoc} */
	public void stop() {
		asyncCubeStoreTimer.cancel();
		asyncCubeStoreTimer = null;
	}

	private class StoreCubeTask extends TimerTask {
		/** {@inheritDoc} */
		@Override
		public void run() {
			store50NextProcessesAsCube();
		}
	}
}
