package io.analytica.server.aggregator.impl.influxDB.query.impl;


import java.util.List;

import selector.ProcessAggregatorAggregatedDataSelector;
import selector.ProcessAggregatorAggregatedDataSelector.ProcessAggregatorAggregatedData;
import selector.ProcessAggregatorDataSelector;
import io.analytica.api.Assertion;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.impl.influxDB.InfluxDBDate;
import io.analytica.server.aggregator.impl.influxDB.Util;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBDataQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBSingleQuery;

public class InfluxDBAggregatedDataQuery implements InfluxDBDataQuery,InfluxDBSingleQuery {
	private ProcessAggregatorQuery aggregatorQuery;
	private ProcessAggregatorAggregatedDataSelector dataSelector;
	
	public InfluxDBAggregatedDataQuery(ProcessAggregatorQuery aggregatorQuery){
		Assertion.checkNotNull(aggregatorQuery, "Unable to create a InfluxDBAggregatedDataQuery query from null");
		Assertion.checkArgument(aggregatorQuery.getAggregatorDataSelector().getClass().equals(ProcessAggregatorAggregatedDataSelector.class), "Needed ProcessAggregatorAggregatedDataSelector. Found "+aggregatorQuery.getAggregatorDataSelector().getClass());
		Assertion.checkArgument(aggregatorQuery.hasRange()," Unable to create a InfluxDBAggregatedDataQuery query without range");
		//-----------------------------------------------------------------------------------------
		this.aggregatorQuery=aggregatorQuery;
		this.dataSelector = (ProcessAggregatorAggregatedDataSelector) aggregatorQuery.getAggregatorDataSelector();
	}

	@Override
	public String getQuery() {
		
		final String subCategories = Util.getRegexFilterTag(aggregatorQuery.getAggregatorDataFilter().getCategories());
		final String locations = Util.getRegexFilterTag(aggregatorQuery.getAggregatorDataFilter().getLocations());
		final String type = Util.getRegexFilterMeasurement(aggregatorQuery.getAggregatorDataFilter().getType());
		final String timeFrom =Util.getRegexDate(new InfluxDBDate(aggregatorQuery.getAggregatorDataRange().getMinDate()).getDate());
		final String timeTo = Util.getRegexDate(new InfluxDBDate(aggregatorQuery.getAggregatorDataRange().getMaxDate()).getDate());
		final String timeDim = aggregatorQuery.getAggregatorDataRange().getDimention();
		
		StringBuilder queryBuilder = new StringBuilder();
				
		queryBuilder.append("SELECT ");
		
		if(dataSelector.isEmpty()){
			queryBuilder.append("* ");
		}
		else{
			List<ProcessAggregatorAggregatedData> entries = dataSelector.getSelectors();
			for (int i=0; i<entries.size()-1;i++){
				queryBuilder.append(Util.getRegexAggregatedMeasurement(entries.get(i).getDataName(),entries.get(i).getSelectorType(),entries.get(i).getLabel())).append(", ");
			}
			ProcessAggregatorAggregatedData lastData=entries.get(entries.size()-1);
			queryBuilder.append(Util.getRegexAggregatedMeasurement(lastData.getDataName(),lastData.getSelectorType(),lastData.getLabel())).append(" ");
		}
		
		queryBuilder.append(" FROM ").append(type)
		.append(" where ").append(InfluxDBQuery.TAG_CATEGORY).append("=").append(subCategories)
		.append(" and ").append(InfluxDBQuery.TAG_LOCATION).append("=").append(locations)
		.append(" and ").append(InfluxDBQuery.TAG_TIME).append(" > ").append(timeFrom)
		.append(" and ").append(InfluxDBQuery.TAG_TIME).append(" < ").append(timeTo);
		
		//setting the not null check
		if(!dataSelector.isEmpty()){
			for (ProcessAggregatorAggregatedData selector :dataSelector.getSelectors()){
				if(selector.isNotNull()){
					queryBuilder.append(" and ").append(Util.getNotNullComparison(selector.getDataName()));
				}
			}
		}
		
		//setting the group by 
		queryBuilder.append(" group by ").append(InfluxDBQuery.TAG_TIME).append("(").append(timeDim).append(")");
		
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

	@Override
	public ProcessAggregatorDataSelector getSelector() {
		return dataSelector;
	}
}
