package io.analytica.server.aggregator.impl.influxDB.query.impl;

import selector.ProcessAggregatorDataSelector;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBSingleQuery;

public class InfluxDBBasicQuery implements InfluxDBQuery,InfluxDBSingleQuery  {
	
	private final String query;
	private final String category;
	private final ProcessAggregatorDataSelector selector;
	public InfluxDBBasicQuery(final String query, final String category, final ProcessAggregatorDataSelector selector){
		this.query=query;
		this.category= category;
		this.selector=selector;
	}
	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public String getCategory() {
		return category;
	}
	@Override
	public ProcessAggregatorDataSelector getSelector() {
		return selector;
	}

}
