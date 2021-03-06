/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.server.impl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import io.analytica.api.AProcess;
import io.analytica.server.ServerManager;
import io.analytica.server.aggregator.ProcessAggegatorConstants;
import io.analytica.server.aggregator.ProcessAggregatorDto;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.aggregator.ProcessAggregatorPlugin;
import io.analytica.server.aggregator.ProcessAggregatorQueryBuilder;
import io.analytica.server.store.Identified;
import io.analytica.server.store.ProcessStorePlugin;
import io.vertigo.lang.Activeable;
import io.vertigo.lang.Assertion;

/**
 * Manager Serveur d'Analytica.
 * @author npiedeloup
 * @version $Id: ServerManagerImpl.java,v 1.22 2013/01/14 16:35:19 npiedeloup Exp $
 */
public final class ServerManagerImpl implements ServerManager, Activeable {
	public static long convertionTime = 0;
	private final ProcessStorePlugin processStorePlugin;
	private final ProcessAggregatorPlugin processAggregatorPlugin;
	private Timer asyncCubeStoreTimer = null;
	static final Logger logger = LogManager.getLogger(ServerManagerImpl.class);
	private static final int NB_PROCESS_A_TRAITER = 5000;

	/**
	 * Constructeur.
	 * @param processStorePlugin InfluxDBProcessAggregatorPlugin de stockage des Process
	 */
	@Inject
	public ServerManagerImpl(final ProcessAggregatorPlugin processAggregatorPlugin, final ProcessStorePlugin processStorePlugin, final QueryNetApiPlugin queryNetApiPlugin, final ProcessNetApiPlugin processNetApiPlugin) {
		super();
		Assertion.checkNotNull(processAggregatorPlugin);
		Assertion.checkNotNull(processStorePlugin);
		//-----------------------------------------------------------------
		this.processAggregatorPlugin = processAggregatorPlugin;
		this.processStorePlugin = processStorePlugin;
	}

	/** {@inheritDoc} */
	@Override
	public void push(final AProcess process) {
		processStorePlugin.add(process);
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		asyncCubeStoreTimer = new Timer("pushProcessToHCube", true);
		final TimerTask storeCubeTask = new StoreCubeTask();
		storeCubeTask.run();
		asyncCubeStoreTimer.schedule(storeCubeTask, 2500, 2500); //X processes toutes les 250ms.. pour la vrai vie ok, pour les tests unitaires pas suffisant
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		asyncCubeStoreTimer.cancel();
		asyncCubeStoreTimer = null;
	}

	private class StoreCubeTask extends TimerTask {

		public StoreCubeTask() {
		}

		/** {@inheritDoc} */
		@Override
		public void run() {
			try {
				storeNexProcesses();
			} catch (final ProcessAggregatorException e) {
				logger.error("Unable to store data into InfluxDB", e);
			}
		}
	}

	int storeNexProcesses() throws ProcessAggregatorException {
		final List<String> processStoreApps = processStorePlugin.getApps();
		int numberOfProcessesTreated = 0;
		for (final String appName : processStoreApps) {

			final ProcessAggregatorQueryBuilder queryBuilder = new ProcessAggregatorQueryBuilder(appName)
					.withType(ProcessAggegatorConstants.LAST_INSERTED_PROCESS)
					.withSelectors(ProcessAggegatorConstants.LAST_INSERTED_PROCESS);
			final List<Identified<AProcess>> nextProcesses = processStorePlugin.getProcess(appName, processAggregatorPlugin.getLastInsertedProcess(queryBuilder.build()), NB_PROCESS_A_TRAITER);
			for (final Identified<AProcess> process : nextProcesses) {
				processAggregatorPlugin.push(process);
				numberOfProcessesTreated++;
			}
		}
		return numberOfProcessesTreated;
	}

	@Override
	public List<ProcessAggregatorDto> findAllLocations(final String appName)
			throws ProcessAggregatorException {
		return processAggregatorPlugin.findAllLocations(new ProcessAggregatorQueryBuilder(appName).build());
	}

	@Override
	public List<ProcessAggregatorDto> findAllTypes(final String appName)
			throws ProcessAggregatorException {
		return processAggregatorPlugin.findAllTypes(new ProcessAggregatorQueryBuilder(appName).build());
	}

	@Override
	public List<ProcessAggregatorDto> findAllCategories(final String appName)
			throws ProcessAggregatorException {
		return processAggregatorPlugin.findAllCategories(new ProcessAggregatorQueryBuilder(appName).build());
	}

	@Override
	public List<ProcessAggregatorDto> findCategories(final String appName, final String type, final String subCategories, final String location) throws ProcessAggregatorException {
		final ProcessAggregatorQueryBuilder queryBuilder = new ProcessAggregatorQueryBuilder(appName)
				.withLocations(location)
				.withCategories(subCategories)
				.withType(type);
		return processAggregatorPlugin.findCategories(queryBuilder.build());
	}

	@Override
	public List<ProcessAggregatorDto> getTimeLine(final String appName, final String timeFrom,
			final String timeTo, final String timeDim, final String type, final String subCategories,
			final String location, final String datas) throws ProcessAggregatorException {
		final ProcessAggregatorQueryBuilder queryBuilder = new ProcessAggregatorQueryBuilder(appName)
				.withLocations(location)
				.withCategories(subCategories)
				.withType(type)
				.withDateRange(timeDim, timeFrom, timeTo)
				.withSelectors(datas);
		return processAggregatorPlugin.getTimeLine(queryBuilder.build());
	}
}
