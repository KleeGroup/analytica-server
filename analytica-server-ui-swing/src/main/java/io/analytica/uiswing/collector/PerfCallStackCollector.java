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
package io.analytica.uiswing.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfCallStackCollector implements PerfCollector {
	public static final String CALL_STACK_MODULE_NAME = "CallStackModule";
	public static final String AUTRE_LIBELLE = "Autre";
	private static final boolean LOG_GRAPH_APPEL = false;
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

	@Override
	public void onProcessStart(final String moduleName, final String processeId, final Object obj, final Object[] params) {
		getThreadStats().startProcess(processeId, moduleName);
	}

	@Override
	public void onProcessFinish(final String moduleName, final String processId, final Object obj, final Object[] params, final Object ret, final long duration, final boolean success) {
		final long tempsRestant = getThreadStats().finishProcess(duration, processId, moduleName);
		final List processQueue = getThreadStats().getProcessQueue();
		PROCESS_STATS_TREE.addRequest(processQueue, processId, duration);
		PROCESS_STATS_TREE.addRequest(processQueue, processId, AUTRE_LIBELLE, tempsRestant);
	}

	@Override
	public void onProcessError(final String moduleName, final String processId, final Object obj, final Object[] params, final Throwable throwable) {
		//rien
	}

	@Override
	public Map getResults() {
		final Map result = new HashMap();
		result.put(CALL_STACK_MODULE_NAME, PROCESS_STATS_TREE);
		return result;
	}

	@Override
	public void clearResults() {
		PROCESS_STATS_TREE.getResults().clear();
	}

	@Override
	public ProcessStatsCollection getResults(final String moduleName) {
		if (CALL_STACK_MODULE_NAME.equals(moduleName)) {
			return PROCESS_STATS_TREE;
		}
		return null;
	}

	@Override
	public void clearResults(final String moduleName) {
		getResults(moduleName).getResults().clear();
	}

	private static class ThreadStats {
		private final List processQueue = new ArrayList();
		private final List outTimeQueue = new ArrayList();
		private final List startTimeQueue = new ArrayList();
		private final StringBuffer graphAppel = LOG_GRAPH_APPEL ? new StringBuffer() : null;

		public ThreadStats() {
			//rien
		}

		void startProcess(final String processName, final String moduleName) {
			processQueue.add(processName);
			outTimeQueue.add(new Long(0));
			startTimeQueue.add(new Long(System.currentTimeMillis()));
			if (LOG_GRAPH_APPEL) {
				graphAppel.append("\n").append(Thread.currentThread().getName()).append(" - ");
				graphAppel.append("#").append(processQueue.get(0)).append("#");
				for (int i = 0; i < outTimeQueue.size(); i++) {
					graphAppel.append("  ");
				}
				graphAppel.append("Start ").append(processName).append("(").append(moduleName).append(")");
			}
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
			String firstProcesse = (String) processQueue.get(0);
			if (LOG_GRAPH_APPEL) {
				graphAppel.append("\n").append(Thread.currentThread().getName()).append(" - ");
				graphAppel.append("#").append(firstProcesse).append("#");
				for (int i = 0; i < outTimeQueue.size(); i++) {
					graphAppel.append("  ");
				}
				graphAppel.append("Finish ").append(processName).append("(").append(moduleName).append(") in ").append(newDuration).append("ms (").append(duration).append("ms réel)");
			}

			processQueue.remove(lastIndex);
			outTimeQueue.remove(lastIndex);
			startTimeQueue.remove(lastIndex);

			if (!outTimeQueue.isEmpty()) {
				Long outTime = (Long) outTimeQueue.get(lastIndex - 1);
				if (LOG_GRAPH_APPEL) {
					graphAppel.append("\n#").append(firstProcesse).append("#");
					for (int i = 0; i < outTimeQueue.size(); i++) {
						graphAppel.append("  ");
					}
					graphAppel.append("setParentOutTime ").append(outTime.longValue()).append(" + ").append(duration);
				}

				outTime = new Long(outTime.longValue() + duration);
				outTimeQueue.set(lastIndex - 1, outTime);
			} else {
				if (LOG_GRAPH_APPEL) {
					graphAppel.append("\n#").append(firstProcesse).append("#");
					for (int i = 0; i < outTimeQueue.size(); i++) {
						graphAppel.append("  ");
					}
					graphAppel.append("Fin Complete");
					//ServerLog.fine(graphAppel.toString());
					graphAppel.setLength(0);
				}

				firstProcesse = null;
			}
			return newDuration;
		}

		//fixme : not used
		//		String getFirstProcess() {
		//			return (String) (processQueue.isEmpty() ? null : processQueue.get(0));
		//		}
		//
		//		int getProcessQueueSize() {
		//			return processQueue.size();
		//		}

		List getProcessQueue() {
			return processQueue;
		}
	}

	@Override
	public StringBuffer print(final StringBuffer out) {
		return out;
	}
}
