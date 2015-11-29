package io.analytica.server.aggregator.impl.influxDB.query.impl;

import selector.ProcessAggregatorDataSelector;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBMetaDataQuery;

public class InfluxDBMeasurementsQuery implements InfluxDBMetaDataQuery{
	private final ProcessAggregatorDataSelector selector;
	public InfluxDBMeasurementsQuery(final ProcessAggregatorDataSelector selector){
		this.selector=selector;
	}

	@Override
	public String getQuery() {
		return "SHOW MEASUREMENTS";
	}

	@Override
	public String getCategory() {
		return null;
	}

	@Override
	public ProcessAggregatorDataSelector getSelector() {
		return selector;
	}

}
