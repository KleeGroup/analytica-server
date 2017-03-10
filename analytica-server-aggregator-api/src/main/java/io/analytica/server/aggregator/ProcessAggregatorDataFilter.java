package io.analytica.server.aggregator;

public class ProcessAggregatorDataFilter {
	private String categories;
	private String type;
	private String locations;
	private String applicationName;
	
	public ProcessAggregatorDataFilter(final String applicationName){
		this.applicationName=applicationName;
	}
	
	public ProcessAggregatorDataFilter withCategories(final String categories){
		this.categories=categories;
		return this;
	}
	
	public ProcessAggregatorDataFilter withLocations(final String locations){
		this.locations=locations;
		return this;
	}

	public ProcessAggregatorDataFilter withType(final String type){
		this.type=type;
		return this;
	}

	public String getCategories() {
		return categories;
	}

	public String getCompleteCategories(){
		return type + ProcessAggregatorQuery.SEPARATOR + categories;
	}
	
	public String getLocations() {
		return locations;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getType() {
		return type;
	}
}
