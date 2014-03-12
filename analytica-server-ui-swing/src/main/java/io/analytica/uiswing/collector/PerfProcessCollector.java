package io.analytica.uiswing.collector;

import java.util.HashMap;
import java.util.Map;

public class PerfProcessCollector implements PerfCollector {
	private static final transient Map<String, ProcessStatsMap> PROCESS_STATS_MAP_PAR_MODULE = new HashMap<String, ProcessStatsMap>();

	private static ProcessStatsMap getProcessStatsByModule(final String moduleName) {
		if (!PROCESS_STATS_MAP_PAR_MODULE.containsKey(moduleName)) {
			PROCESS_STATS_MAP_PAR_MODULE.put(moduleName, new ProcessStatsMap());
		}
		return PROCESS_STATS_MAP_PAR_MODULE.get(moduleName);
	}

	public void onProcessStart(final String moduleName, final String processeId, final Object obj, final Object[] params) {
		//rien
	}

	public void onProcessFinish(final String moduleName, final String processeId, final Object obj, final Object[] params, final Object ret, final long duration, final boolean success) {
		getProcessStatsByModule(moduleName).addRequest(processeId, duration);
	}

	public void onProcessError(final String moduleName, final String processId, final Object obj, final Object[] params, final Throwable throwable) {
		//rien
	}

	public ProcessStatsCollection getResults(final String moduleName) {
		return getProcessStatsByModule(moduleName);
	}

	public void clearResults(final String moduleName) {
		PROCESS_STATS_MAP_PAR_MODULE.remove(moduleName);
	}

	public Map<String, ProcessStatsCollection> getResults() {
		final Map<String, ProcessStatsCollection> results = new HashMap<String, ProcessStatsCollection>();
		for (final String key : PROCESS_STATS_MAP_PAR_MODULE.keySet()) {
			results.put(key, getProcessStatsByModule(key));
		}
		return results;
	}

	public void clearResults() {
		PROCESS_STATS_MAP_PAR_MODULE.clear();
	}

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
