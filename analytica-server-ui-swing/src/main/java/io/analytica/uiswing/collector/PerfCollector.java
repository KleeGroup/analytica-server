package io.analytica.uiswing.collector;

import java.util.Map;

public interface PerfCollector {

	void onProcessError(String moduleName, String processId, Object obj, Object[] params, Throwable throwable);

	void onProcessStart(String moduleName, String processId, Object obj, Object[] params);

	void onProcessFinish(String moduleName, String processId, Object obj, Object[] params, Object ret, long duration, boolean success);

	/**
	 * Retourne une map dont la clé est le nom d'un module
	 * et la valeur une MethodStatsCollection
	 * @return Map
	 */
	Map<String, ProcessStatsCollection> getResults();

	void clearResults();

	ProcessStatsCollection getResults(String moduleName);

	void clearResults(String moduleName);

	StringBuffer print(StringBuffer out);
}
