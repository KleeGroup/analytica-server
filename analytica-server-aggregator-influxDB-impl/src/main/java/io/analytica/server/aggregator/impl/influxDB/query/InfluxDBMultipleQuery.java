package io.analytica.server.aggregator.impl.influxDB.query;

import java.util.List;

public interface InfluxDBMultipleQuery extends InfluxDBQuery{

	public List<InfluxDBSingleQuery> getSingleQueries();
}
