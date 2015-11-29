package io.analytica.server.aggregator.impl.influxDB;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import io.analytica.server.aggregator.ProcessAggregatorDate;
import io.analytica.server.aggregator.ProcessAggregatorDateType;

public class InfluxDBDate {
	private final String date;
	
	public InfluxDBDate(ProcessAggregatorDate aggregatorDate){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		date = dateFormat.format(aggregatorDate.getDate());
	}

	public String getDate() {
		return date;
	}
	
}
