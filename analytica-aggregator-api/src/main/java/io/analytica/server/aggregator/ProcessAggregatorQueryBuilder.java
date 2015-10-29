package io.analytica.server.aggregator;

import java.util.Date;


import io.vertigo.lang.Assertion;

public class ProcessAggregatorQueryBuilder {
	private ProcessAggregatorDataRange aggregatorDataRange;
	private String categories;
	private String locations;
	private String applicationName;
	
	public ProcessAggregatorQueryBuilder(final String applicationName){
		this.applicationName=applicationName;
	}
	
	public ProcessAggregatorQueryBuilder withDateRange(final String dimention, final String timeFrom , final String timeTo){
		aggregatorDataRange= new ProcessAggregatorDataRange(dimention, timeFrom, timeTo);
		return this;
	}
	
	public ProcessAggregatorQueryBuilder withLocations(final String locations){
		this.locations=locations;
		return this;
	}
	
	public ProcessAggregatorQueryBuilder withCategories(final String type, final String subCategories){
		final StringBuilder sb = new StringBuilder();
		sb.append(type);
		if (!subCategories.startsWith(ProcessAggregatorQuery.SEPARATOR)) {
			sb.append(ProcessAggregatorQuery.SEPARATOR);
		} 
		sb.append(subCategories);
		this.categories = sb.toString();
		return this;
	}
	
	public ProcessAggregatorQuery build(){
		return new ProcessAggregatorQuery(applicationName, locations, categories, aggregatorDataRange);
	}
	
	
	
	
}
