package io.analytica.server.aggregator;

public class ProcessAggregatorQuery {
	final String applicationName;
	final String locations;
	final String categories;
	final ProcessAggregatorDataRange aggregatorDataRange;
	public static String  SEPARATOR="/";
	
	public ProcessAggregatorQuery(final String applicationName,final String locations,final String categories,final ProcessAggregatorDataRange aggregatorDataRange){
		this.aggregatorDataRange = aggregatorDataRange;
		this.applicationName = applicationName;
		this.locations = locations;
		this.categories = categories;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getLocations() {
		return locations;
	}

	public String getCategories() {
		return categories;
	}

	public ProcessAggregatorDataRange getAggregatorDataRange() {
		return aggregatorDataRange;
	}
	

}
