package io.analytica.server.aggregator;

import selector.ProcessAggregatorDataSelectorBuilder;
import selector.ProcessAggregatorDataSelectorType;

public class ProcessAggregatorQueryBuilder {
	private ProcessAggregatorDataRange aggregatorDataRange;
	private ProcessAggregatorDataFilter aggregatorDataFilter;
	private ProcessAggregatorDataSelectorBuilder aggregatorDataSelectorBuilder;
	
	public ProcessAggregatorQueryBuilder(final String applicationName){
		aggregatorDataFilter = new ProcessAggregatorDataFilter(applicationName);
		aggregatorDataSelectorBuilder = new ProcessAggregatorDataSelectorBuilder();
	}
	
	public ProcessAggregatorQueryBuilder withDateRange(final String dimention, final String timeFrom , final String timeTo){
		aggregatorDataRange= new ProcessAggregatorDataRange(dimention, timeFrom, timeTo);
		return this;
	}
	
	public ProcessAggregatorQueryBuilder withLocations(final String locations){
		aggregatorDataFilter.withLocations(locations);
		return this;
	}
	
	public ProcessAggregatorQueryBuilder withSelectors(final String selectors){
		aggregatorDataSelectorBuilder.withSimpleSelector(selectors);
		return this;
	}
	
	public ProcessAggregatorQueryBuilder withSelectors(final String dataName, final ProcessAggregatorDataSelectorType dataSelectorType){
		aggregatorDataSelectorBuilder.withAggregatedSelector(dataName, dataSelectorType);
		return this;
	}
	public ProcessAggregatorQueryBuilder withCategories(final String type, final String subCategories){
		final StringBuilder sb = new StringBuilder();
		sb.append(type);
		if (!subCategories.startsWith(ProcessAggregatorQuery.SEPARATOR)) {
			sb.append(ProcessAggregatorQuery.SEPARATOR);
		} 
		sb.append(subCategories);
		aggregatorDataFilter.withCategories(sb.toString());
		return this;
	}
	
	public ProcessAggregatorQueryBuilder withCategories(final String subCategories){
		aggregatorDataFilter.withCategories(subCategories);
		return this;
	}
	
	public ProcessAggregatorQueryBuilder withType(final String type){
		aggregatorDataFilter.withType(type);
		return this;
	}
	
	public ProcessAggregatorQuery build(){
		return new ProcessAggregatorQuery(aggregatorDataFilter, aggregatorDataSelectorBuilder.build(), aggregatorDataRange);
	}
	
	
	
	
}
