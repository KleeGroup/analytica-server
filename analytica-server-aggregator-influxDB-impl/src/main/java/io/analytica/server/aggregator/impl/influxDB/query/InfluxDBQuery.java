package io.analytica.server.aggregator.impl.influxDB.query;

import selector.ProcessAggregatorDataSelector;

public interface InfluxDBQuery {
	public static final String TAG_CATEGORY = "category";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_TIME = "time";
	public static final String MEASUREMENT="name";
	public static final String NO_CATEGORY=null;
	
	public ProcessAggregatorDataSelector getSelector();

}
