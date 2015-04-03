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

import java.util.HashMap;
import java.util.Map;

public class PerfProcessCollector implements PerfCollector {
	private static final transient Map<String, ProcessStatsMap> PROCESS_STATS_MAP_PAR_MODULE = new HashMap<>();

	private static ProcessStatsMap getProcessStatsByModule(final String moduleName) {
		if (!PROCESS_STATS_MAP_PAR_MODULE.containsKey(moduleName)) {
			PROCESS_STATS_MAP_PAR_MODULE.put(moduleName, new ProcessStatsMap());
		}
		return PROCESS_STATS_MAP_PAR_MODULE.get(moduleName);
	}

	@Override
	public void onProcessStart(final String moduleName, final String processeId, final Object obj, final Object[] params) {
		//rien
	}

	@Override
	public void onProcessFinish(final String moduleName, final String processeId, final Object obj, final Object[] params, final Object ret, final long duration, final boolean success) {
		getProcessStatsByModule(moduleName).addRequest(processeId, duration);
	}

	@Override
	public void onProcessError(final String moduleName, final String processId, final Object obj, final Object[] params, final Throwable throwable) {
		//rien
	}

	@Override
	public ProcessStatsCollection getResults(final String moduleName) {
		return getProcessStatsByModule(moduleName);
	}

	@Override
	public void clearResults(final String moduleName) {
		PROCESS_STATS_MAP_PAR_MODULE.remove(moduleName);
	}

	@Override
	public Map<String, ProcessStatsCollection> getResults() {
		final Map<String, ProcessStatsCollection> results = new HashMap<>();
		for (final String key : PROCESS_STATS_MAP_PAR_MODULE.keySet()) {
			results.put(key, getProcessStatsByModule(key));
		}
		return results;
	}

	@Override
	public void clearResults() {
		PROCESS_STATS_MAP_PAR_MODULE.clear();
	}

	@Override
	public StringBuffer print(final StringBuffer out) {
		for (final Map.Entry<String, ProcessStatsMap> entry : PROCESS_STATS_MAP_PAR_MODULE.entrySet()) {
			out.append(entry.getKey()).append("\n");
			for (final Map.Entry entry2 : entry.getValue().getResults().entrySet()) {
				out.append(entry2.getKey()).append("\n");
			}
		}
		return out;
	}
}
