package io.analytica.server.aggregator;

import java.util.List;
import java.util.Map;

import io.analytica.api.KProcess;
import io.analytica.server.store.Identified;
import io.vertigo.lang.Plugin;


public interface ProcessAggregatorPlugin extends Plugin {

	/**
	 * Add a process.
	 * @param process Process to push
	 */
	public static final String CATEGORIES_SEPARATOR="/";
	
	void push(Identified<KProcess> process);

	String getLastInsertedProcess(final String appName);
	
	List<ProcessAggregatorDto> findAllLocations(final String appName) throws ProcessAggregatorException;
	
	List<ProcessAggregatorDto> findAllTypes(String appName) throws ProcessAggregatorException;

	List<ProcessAggregatorDto> findAllCategories(String appName)throws ProcessAggregatorException;

	List<ProcessAggregatorDto> findCategories(String appName, String type, String subCategories, String location)throws ProcessAggregatorException;

	List<ProcessAggregatorDto> getTimeLine(String appName, String timeFrom, String timeTo, String timeDim, String type, String subCategories, String location, Map<String, String> datas)throws ProcessAggregatorException;

	
}
