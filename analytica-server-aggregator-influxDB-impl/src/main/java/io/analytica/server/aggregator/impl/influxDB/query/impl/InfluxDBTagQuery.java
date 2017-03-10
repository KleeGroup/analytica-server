package io.analytica.server.aggregator.impl.influxDB.query.impl;

import selector.ProcessAggregatorDataSelector;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.impl.influxDB.Util;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBMetaDataQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQuery;

public class InfluxDBTagQuery implements InfluxDBMetaDataQuery{
	private String tagKey;
	private ProcessAggregatorQuery aggregatorQuery;
	private ProcessAggregatorDataSelector selector;
	public InfluxDBTagQuery(ProcessAggregatorQuery aggregatorQuery,String tagKey, ProcessAggregatorDataSelector selector){
		this.tagKey=tagKey;
		this.aggregatorQuery=aggregatorQuery;
		this.selector=selector;
	}

	@Override
	public String getQuery() {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SHOW TAG VALUES FROM ").append(getType())
		.append(" with key=").append(tagKey)
		.append(" where ").append(InfluxDBQuery.TAG_CATEGORY).append("=").append(getSubCategories())
		.append(" and ").append(InfluxDBQuery.TAG_LOCATION).append("=").append(getLocations());
		
		return queryBuilder.toString();
	}

	@Override
	public String getCategory() {
		StringBuilder categories = new StringBuilder();
		if(aggregatorQuery!=null && aggregatorQuery.getAggregatorDataFilter()!=null && aggregatorQuery.getAggregatorDataFilter().getType()!=null){
			categories.append(aggregatorQuery.getAggregatorDataFilter().getType()).append(ProcessAggregatorQuery.SEPARATOR);
		}
		if(aggregatorQuery!=null && aggregatorQuery.getAggregatorDataFilter()!=null && aggregatorQuery.getAggregatorDataFilter().getCategories()!=null){
			categories.append(aggregatorQuery.getAggregatorDataFilter().getCategories());
		}
		return categories.toString();
	}
	
	private String getSubCategories(){
		String subCategories = null;
		if(aggregatorQuery!=null && aggregatorQuery.getAggregatorDataFilter()!=null){
			subCategories= aggregatorQuery.getAggregatorDataFilter().getCategories();
		}
		return Util.getRegexFilterTag(subCategories);
	}
	
	private String getLocations(){
		String locations = null;
		if(aggregatorQuery!=null && aggregatorQuery.getAggregatorDataFilter()!=null){
			locations= aggregatorQuery.getAggregatorDataFilter().getLocations();
		}
		return Util.getRegexFilterTag(locations);
	}
	
	private String getType(){
		String type = null;
		if(aggregatorQuery!=null && aggregatorQuery.getAggregatorDataFilter()!=null){
			type= aggregatorQuery.getAggregatorDataFilter().getType();
		}
		return Util.getRegexFilterMeasurement(type);
	}

	@Override
	public ProcessAggregatorDataSelector getSelector() {
		return selector;
	}
}
