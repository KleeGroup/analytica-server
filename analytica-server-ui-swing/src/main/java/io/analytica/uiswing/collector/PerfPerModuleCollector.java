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
package io.analytica.uiswing.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfPerModuleCollector implements PerfCollector {
	public static final String RATIO_PER_MODULE_NAME = "ratioModule";

	private static final transient ProcessStatsTree PROCESS_STATS_TREE = new ProcessStatsTree();
	private static final transient ThreadLocal THREAD_STATS = new ThreadLocal(); //map par thread

	private ThreadStats getThreadStats() {
		ThreadStats threadStats = (ThreadStats) THREAD_STATS.get();
		if (threadStats == null) {
			threadStats = new ThreadStats();
			THREAD_STATS.set(threadStats);
		}
		return threadStats;
	}

	public void onProcessStart(final String moduleName, final String processeId, final Object obj, final Object[] params) {
		getThreadStats().startProcess(processeId, moduleName);
	}

	public void onProcessFinish(final String moduleName, final String processId, final Object obj, final Object[] params, final Object ret, final long duration, final boolean success) {
		final String firstProcess = getThreadStats().getFirstProcess(); //il faut la récupérer avant le finish
		final long tempsRestant = getThreadStats().finishProcess(duration, processId, moduleName);
		if (getThreadStats().getProcessQueueSize() == 0) { //Si on vient de tout dépiler, on enregistre le total
			PROCESS_STATS_TREE.addRequest(firstProcess, null, null, duration);
		}
		PROCESS_STATS_TREE.addRequest(firstProcess, moduleName, null, tempsRestant);
		PROCESS_STATS_TREE.addRequest(firstProcess, moduleName, processId, tempsRestant);
	}

	public void onProcessError(final String moduleName, final String processId, final Object obj, final Object[] params, final Throwable throwable) {
		//rien
	}

	public Map getResults() {
		final Map result = new HashMap();
		result.put(RATIO_PER_MODULE_NAME, PROCESS_STATS_TREE);
		return result;
	}

	public void clearResults() {
		PROCESS_STATS_TREE.getResults().clear();
	}

	public ProcessStatsCollection getResults(final String moduleName) {
		if (RATIO_PER_MODULE_NAME.equals(moduleName)) {
			return PROCESS_STATS_TREE;
		}
		return null;
	}

	public void clearResults(final String moduleName) {
		getResults(moduleName).getResults().clear();
	}

	private static class ThreadStats {
		private final List processQueue = new ArrayList();
		private final List outTimeQueue = new ArrayList();
		private final List startTimeQueue = new ArrayList();

		public ThreadStats() {
			//rien
		}

		void startProcess(final String processName, final String moduleName) {
			processQueue.add(processName);
			outTimeQueue.add(new Long(0));
			startTimeQueue.add(new Long(System.currentTimeMillis()));
		}

		long finishProcess(final long duration, final String processName, final String moduleName) {
			int lastIndex = outTimeQueue.size() - 1;
			final int processeIndex = processQueue.lastIndexOf(processName);
			if (processeIndex != -1) {
				//long processeStartTime = ((Long)startTimeQueue.get(processeIndex)).longValue();
				while (processeIndex < lastIndex) {
					processQueue.remove(lastIndex);
					outTimeQueue.remove(lastIndex);
					startTimeQueue.remove(lastIndex);

					//long startTime = ((Long)startTimeQueue.get(lastIndex)).longValue();
					//finishProcess((processeStartTime+duration)-startTime,(String) processQueue.get(lastIndex),moduleName);
					lastIndex = outTimeQueue.size() - 1;
				}
			}

			lastIndex = outTimeQueue.size() - 1;
			final long lastOutTime = ((Long) outTimeQueue.get(lastIndex)).longValue();
			final long newDuration = duration - lastOutTime;
			processQueue.remove(lastIndex);
			outTimeQueue.remove(lastIndex);
			startTimeQueue.remove(lastIndex);
			if (!outTimeQueue.isEmpty()) {
				Long outTime = (Long) outTimeQueue.get(lastIndex - 1);
				outTime = new Long(outTime.longValue() + duration);
				outTimeQueue.set(lastIndex - 1, outTime);
			}
			return newDuration;
		}

		String getFirstProcess() {
			return (String) (processQueue.isEmpty() ? null : processQueue.get(0));
		}

		int getProcessQueueSize() {
			return processQueue.size();
		}

		List getProcessQueue() {
			return processQueue;
		}
	}

	public StringBuffer print(final StringBuffer out) {
		return out;
	}
}
