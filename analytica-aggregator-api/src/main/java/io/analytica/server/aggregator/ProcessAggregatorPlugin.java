package io.analytica.server.aggregator;

import io.analytica.api.KProcess;
import io.analytica.server.store.Identified;
import io.vertigo.lang.Plugin;


public interface ProcessAggregatorPlugin extends Plugin {

	/**
	 * Add a process.
	 * @param process Process to push
	 */
	void push(Identified<KProcess> process);

	String getLastInsertedProcess(final String appName);

	ProcessAggregatorResult execute(final ProcessAggregatorQuery aggregatorQuery);
	
}
