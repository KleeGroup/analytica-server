package io.analytica.server.aggregator.impl.influxDB.query.impl;

import java.util.List;

import selector.ProcessAggregatorDataSelector;
import selector.ProcessAggregatorSimpleDataSelector;
import selector.ProcessAggregatorSimpleDataSelector.ProcessAggregatorSimpleData;
import io.analytica.api.Assertion;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.impl.influxDB.InfluxDBDate;
import io.analytica.server.aggregator.impl.influxDB.Util;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBDataQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBSingleQuery;

public class InfluxDBSimpleDataQuery implements InfluxDBDataQuery,InfluxDBSingleQuery  {
	private ProcessAggregatorQuery aggregatorQuery;
	private ProcessAggregatorSimpleDataSelector dataSelector;
	
	public InfluxDBSimpleDataQuery(ProcessAggregatorQuery aggregatorQuery){
		Assertion.checkNotNull(aggregatorQuery, "Unable to create a InfluxDB Query from null");
		this.aggregatorQuery=aggregatorQuery;
		this.dataSelector=(ProcessAggregatorSimpleDataSelector) aggregatorQuery.getAggregatorDataSelector();
	}

	@Override
	public String getQuery() {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT ");
		
		if(dataSelector.isEmpty()){
			queryBuilder.append("* ");
		}
		else{
			 List<ProcessAggregatorSimpleData> selectors = dataSelector.getSelectors();
			 for (int i=0; i<selectors.size()-1;i++){
				 queryBuilder.append(Util.getRegexMeasurement(selectors.get(i).getDataName())).append(", ");
				}
			 queryBuilder.append(Util.getRegexMeasurement(selectors.get(selectors.size()-1).getDataName())).append(" ");
		}
			
		final String type = Util.getRegexFilterMeasurement(aggregatorQuery.getAggregatorDataFilter().getType());
		
		final String locations = Util.getRegexFilterTag(aggregatorQuery.getAggregatorDataFilter().getLocations());
		final String subCategories=Util.getRegexFilterTag(aggregatorQuery.getAggregatorDataFilter().getCategories());
		queryBuilder.append(" FROM ").append(type);
		final boolean hasLocations = aggregatorQuery.getAggregatorDataFilter().getLocations()!=null;
		final boolean hasCategories = aggregatorQuery.getAggregatorDataFilter().getCategories()!=null;
		if(hasLocations||hasCategories){
			queryBuilder.append(" where ");
			if(hasLocations){
				queryBuilder.append(InfluxDBQuery.TAG_CATEGORY).append("=").append(locations);
			}
			if(hasCategories){
				if(hasLocations){
					queryBuilder.append(" and ");
				}
				queryBuilder.append(InfluxDBQuery.TAG_LOCATION).append("=").append(subCategories);
			}
		}
		
		
		
		if(aggregatorQuery.hasRange()){
			final String timeFrom = Util.getRegexDate(new InfluxDBDate(aggregatorQuery.getAggregatorDataRange().getMinDate()).getDate());
			final String timeTo =Util.getRegexDate( new InfluxDBDate(aggregatorQuery.getAggregatorDataRange().getMaxDate()).getDate());
			queryBuilder.append(" and ").append(InfluxDBQuery.TAG_TIME).append(" > ").append(timeFrom)
			.append(" and ").append(InfluxDBQuery.TAG_TIME).append(" < ").append(timeTo);
		}
		
		return queryBuilder.toString();
	}

	
	
	@Override
	public String getCategory() {
		StringBuilder categories = new StringBuilder();
		if(aggregatorQuery.getAggregatorDataFilter().getType()!=null){
			categories.append(aggregatorQuery.getAggregatorDataFilter().getType()).append(ProcessAggregatorQuery.SEPARATOR);
		}
		if(aggregatorQuery.getAggregatorDataFilter().getCategories()!=null){
			categories.append(aggregatorQuery.getAggregatorDataFilter().getCategories());
		}
		return categories.toString();
	}

	@Override
	public ProcessAggregatorDataSelector getSelector() {
		return dataSelector;
	}
	

}
