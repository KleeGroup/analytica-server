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

import io.vertigo.kernel.exception.VRuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

public abstract class MultiPerfCollector extends StandardMBean implements PerfCollector {

	protected MultiPerfCollector(final Class<?> mbeanInterface) throws NotCompliantMBeanException {
		super(mbeanInterface);
	}

	private final List<PerfCollector> listPerfCollector = new ArrayList<PerfCollector>();

	public void clearResults() {
		for (final PerfCollector perfCollector : listPerfCollector) {
			perfCollector.clearResults();
		}
	}

	public Map<String, ProcessStatsCollection> getResults() {
		final Map<String, ProcessStatsCollection> results = new HashMap<String, ProcessStatsCollection>();
		Map<String, ProcessStatsCollection> collectorResults;
		for (final PerfCollector perfCollector : listPerfCollector) {
			collectorResults = perfCollector.getResults();
			if (collectorResults != null) {
				for (final String moduleName : collectorResults.keySet()) {
					if (!results.containsKey(moduleName)) {
						results.put(moduleName, getResults(moduleName));
					} //Sinon on a déjà récupéré toutes les données de ce modules (tout PerfCollector confondu)
				}
			}
		}
		return results;
	}

	public void clearResults(final String moduleName) {
		for (final PerfCollector perfCollector : listPerfCollector) {
			perfCollector.clearResults(moduleName);
		}
	}

	public ProcessStatsCollection getResults(final String moduleName) {
		ProcessStatsCollection results = null;
		ProcessStatsCollection collectorResults;
		for (final PerfCollector perfCollector : listPerfCollector) {
			collectorResults = perfCollector.getResults(moduleName);
			if (collectorResults != null && !collectorResults.getResults().isEmpty()) {
				if (results == null) {
					if (collectorResults instanceof ProcessStatsMap) {
						results = new ProcessStatsMap();
					} else if (collectorResults instanceof ProcessStatsTree) {
						results = new ProcessStatsTree();
					} else {
						throw new VRuntimeException("Type de ProcessStatsCollection inconnu : " + collectorResults.getClass().getName());
					}
				}
				results.merge(collectorResults);
			}
		}
		return results;
	}

	public void onProcessError(final String moduleName, final String processId, final Object obj, final Object[] params, final Throwable throwable) {
		for (final PerfCollector perfCollector : listPerfCollector) {
			perfCollector.onProcessError(moduleName, processId, obj, params, throwable);
		}
	}

	public void onProcessFinish(final String moduleName, final String processId, final Object obj, final Object[] params, final Object ret, final long duration, final boolean success) {
		for (final PerfCollector perfCollector : listPerfCollector) {
			perfCollector.onProcessFinish(moduleName, processId, obj, params, ret, duration, success);
		}
	}

	public void onProcessStart(final String moduleName, final String processId, final Object obj, final Object[] params) {
		for (final PerfCollector perfCollector : listPerfCollector) {
			perfCollector.onProcessStart(moduleName, processId, obj, params);
		}
	}

	public void registerPerfCollector(final PerfCollector perfCollector) {
		listPerfCollector.add(perfCollector);
	}

	public StringBuffer print(final StringBuffer out) {
		for (final PerfCollector perfCollector : listPerfCollector) {
			perfCollector.print(out);
		}
		return out;
	}
}
