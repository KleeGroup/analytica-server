package io.analytica.server.aggregator.impl.influxDB.query;

public interface InfluxDBSingleQuery extends InfluxDBQuery{
	
	public String getQuery();
	public String getCategory();
}
