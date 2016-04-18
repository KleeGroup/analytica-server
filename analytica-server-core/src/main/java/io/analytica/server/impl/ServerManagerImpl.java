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

import io.analytica.api.KProcess;
import io.analytica.server.ServerManager;
import io.analytica.server.aggregator.ProcessAggregatorDto;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.aggregator.ProcessAggregatorPlugin;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.ProcessAggregatorResult;
import io.analytica.server.store.Identified;
import io.analytica.server.store.ProcessStorePlugin;
import io.vertigo.lang.Activeable;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.Option;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.apache.log4j.Logger;

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
	private final Option<ProcessStatsPlugin> processStatsPlugin;
	static final Logger logger = Logger.getLogger(ServerManagerImpl.class);
	private static final int NB_PROCESS_A_TRAITER = 5000;

	/**
	 * Constructeur.
	 * @param processStorePlugin Plugin de stockage des Process
	 * @param hcubeManager Manager de stockage des Cubes
	 */
	@Inject
	public ServerManagerImpl(final ProcessAggregatorPlugin processAggregatorPlugin, final ProcessStorePlugin processStorePlugin, final Option<ProcessStatsPlugin> processStatsPlugin, final QueryNetApiPlugin queryNetApiPlugin, final ProcessNetApiPlugin processNetApiPlugin) {
		super();
		Assertion.checkNotNull(processAggregatorPlugin);
		Assertion.checkNotNull(processStorePlugin);
		Assertion.checkNotNull(processStatsPlugin);
		//-----------------------------------------------------------------
		this.processAggregatorPlugin = processAggregatorPlugin;
		this.processStorePlugin = processStorePlugin;
		this.processStatsPlugin = processStatsPlugin;
	}

	/** {@inheritDoc} */
	@Override
	public void push(final KProcess process) {
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
				logger.error("Erreur lors du chargement des données depuis Berkley", e);
			}
		}
	}
	
	int storeNexProcesses() throws ProcessAggregatorException{
		final List<String> processStoreApps = processStorePlugin.getApps();
		int numberOfProcessesTreated = 0;
		for (final String appName : processStoreApps) {
			final List<Identified<KProcess>> nextProcesses = processStorePlugin.getProcess(appName, processAggregatorPlugin.getLastInsertedProcess(appName), NB_PROCESS_A_TRAITER);
			for (final Identified<KProcess> process : nextProcesses) {
				processAggregatorPlugin.push(process);
				numberOfProcessesTreated++;
			}
		}
		return numberOfProcessesTreated;
	}

	@Override
	public List<ProcessAggregatorDto> findAllLocations(String appName)
			throws ProcessAggregatorException {
		return processAggregatorPlugin.findAllLocations(appName);
	}

	@Override
	public List<ProcessAggregatorDto> findAllTypes(final String appName)
			throws ProcessAggregatorException {
		return processAggregatorPlugin.findAllTypes(appName);
	}

	@Override
	public List<ProcessAggregatorDto> findAllCategories(String appName)
			throws ProcessAggregatorException {
		return processAggregatorPlugin.findAllCategories(appName);
	}

	@Override
	public List<ProcessAggregatorDto> findCategories(String appName, String type,String subCategories, String location)throws ProcessAggregatorException {
		return processAggregatorPlugin.findCategories(appName,type,subCategories,location);
	}

	@Override
	public List<ProcessAggregatorDto> getTimeLine(String appName, String timeFrom,
			String timeTo, String timeDim, String type, String subCategories,
			String location,Map<String, String> datas) throws ProcessAggregatorException {
		return processAggregatorPlugin.getTimeLine(appName,timeFrom,timeTo,timeDim,type,subCategories,location,datas);
	}
}
