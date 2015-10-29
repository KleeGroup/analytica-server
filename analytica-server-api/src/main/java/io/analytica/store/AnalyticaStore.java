package io.analytica.store;

import io.analytica.api.KProcess;


public interface AnalyticaStore {

	/**
	 * Add a process.
	 * @param process Process to push
	 */
	void push(KProcess process);


	AnalyticaStoreResult execute(final AnalyticaStoreQuery analyticaStoreQuery);
	
}
