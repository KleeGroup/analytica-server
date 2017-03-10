package io.analytica.server.aggregator.impl.influxDB.query.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import selector.ProcessAggregatorAggregatedDataSelector;
import selector.ProcessAggregatorClusteredDataSelector;
import selector.ProcessAggregatorDataSelector;
import selector.ProcessAggregatorDataSelectorType;
import selector.ProcessAggregatorClusteredDataSelector.ProcessAggregatorClusteredData;
import io.analytica.api.Assertion;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.impl.influxDB.InfluxDBDate;
import io.analytica.server.aggregator.impl.influxDB.Util;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBDataQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBMultipleQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBSingleQuery;

public class InfluxDBClusteredDataQuery implements InfluxDBDataQuery,InfluxDBMultipleQuery {
	private ProcessAggregatorQuery aggregatorQuery;
	private ProcessAggregatorClusteredDataSelector dataSelector;
	
	public InfluxDBClusteredDataQuery(ProcessAggregatorQuery aggregatorQuery){
		Assertion.checkNotNull(aggregatorQuery, "Unable to create a InfluxDBClusteredDataQuery query from null");
		Assertion.checkArgument(aggregatorQuery.getAggregatorDataSelector().getClass().equals(ProcessAggregatorClusteredDataSelector.class), "Needed ProcessAggregatorClusteredDataSelector. Found "+aggregatorQuery.getAggregatorDataSelector().getClass());
		Assertion.checkArgument(aggregatorQuery.hasRange()," Unable to create a InfluxDBClusteredDataQuery query without range");
		//-----------------------------------------------------------------------------------------
		this.aggregatorQuery=aggregatorQuery;
		this.dataSelector=(ProcessAggregatorClusteredDataSelector) aggregatorQuery.getAggregatorDataSelector();
		Assertion.checkArgument(!dataSelector.getAggretatorData().isEmpty()," Unable to create a InfluxDBClusteredDataQuery query without clusteres");
	}	

	@Override
	public List<InfluxDBSingleQuery> getSingleQueries() {
		final String subCategories = Util.getRegexFilterTag(aggregatorQuery.getAggregatorDataFilter().getCategories());
		final String locations = Util.getRegexFilterTag(aggregatorQuery.getAggregatorDataFilter().getLocations());
		final String type = Util.getRegexFilterMeasurement(aggregatorQuery.getAggregatorDataFilter().getType());
		final String timeFrom = Util.getRegexDate(new InfluxDBDate(aggregatorQuery.getAggregatorDataRange().getMinDate()).getDate());
		final String timeTo = Util.getRegexDate(new InfluxDBDate(aggregatorQuery.getAggregatorDataRange().getMaxDate()).getDate());
		final String timeDim = aggregatorQuery.getAggregatorDataRange().getDimention();
		
		List <InfluxDBSingleQuery> simpleDataQueries = new ArrayList<InfluxDBSingleQuery>();
		for(ProcessAggregatorClusteredData clusterdData : dataSelector.getAggretatorData()){
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("SELECT ");
			queryBuilder.append(Util.getRegexAggregatedMeasurement(clusterdData.getDataName(),ProcessAggregatorDataSelectorType.COUNT,clusterdData.getLabel())).append(" ");
			queryBuilder.append(" FROM ").append(type)
			.append(" where ").append(InfluxDBQuery.TAG_CATEGORY).append("=").append(subCategories)
			.append(" and ").append(InfluxDBQuery.TAG_LOCATION).append("=").append(locations)
			.append(" and ").append(InfluxDBQuery.TAG_TIME).append(" > ").append(timeFrom)
			.append(" and ").append(InfluxDBQuery.TAG_TIME).append(" < ").append(timeTo)
			.append(" and \"").append(clusterdData.getDataName()).append("\" > ").append(clusterdData.getMinValue())
			.append(" and \"").append(clusterdData.getDataName()).append("\" < ").append(clusterdData.getMaxValue())
			.append(" group by ").append(InfluxDBQuery.TAG_TIME).append("(").append(timeDim).append(")");
			ProcessAggregatorDataSelector localSelector = new ProcessAggregatorAggregatedDataSelector().withSelector(clusterdData.getDataName(), ProcessAggregatorDataSelectorType.COUNT,clusterdData.getLabel(), clusterdData.getDefaultValue());
			simpleDataQueries.add(new InfluxDBBasicQuery(queryBuilder.toString(), aggregatorQuery.getAggregatorDataFilter().getCompleteCategories(),localSelector));
		}
		
		return simpleDataQueries;
	}

	@Override
	public ProcessAggregatorDataSelector getSelector() {
		return dataSelector;
	}

}
