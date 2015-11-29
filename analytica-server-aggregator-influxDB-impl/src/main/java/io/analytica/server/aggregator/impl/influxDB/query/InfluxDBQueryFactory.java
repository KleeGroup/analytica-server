package io.analytica.server.aggregator.impl.influxDB.query;

import selector.ProcessAggregatorAggregatedDataSelector;
import selector.ProcessAggregatorClusteredDataSelector;
import selector.ProcessAggregatorSimpleDataSelector;
import io.analytica.api.Assertion;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.impl.influxDB.query.impl.InfluxDBAggregatedDataQuery;
import io.analytica.server.aggregator.impl.influxDB.query.impl.InfluxDBClusteredDataQuery;
import io.analytica.server.aggregator.impl.influxDB.query.impl.InfluxDBSimpleDataQuery;

public class InfluxDBQueryFactory {

	public static InfluxDBDataQuery getDataQuery(ProcessAggregatorQuery query){
		Assertion.checkNotNull(query, "Cannot create a InfluxDBDataQuery from null data");
		if (query.getAggregatorDataSelector() instanceof ProcessAggregatorSimpleDataSelector){
			return new InfluxDBSimpleDataQuery(query);
		}
		if (query.getAggregatorDataSelector() instanceof ProcessAggregatorAggregatedDataSelector){
			return new InfluxDBAggregatedDataQuery(query);
		}
		if (query.getAggregatorDataSelector() instanceof ProcessAggregatorClusteredDataSelector){
			return new InfluxDBClusteredDataQuery(query);
		}
		return null;
	}

}
