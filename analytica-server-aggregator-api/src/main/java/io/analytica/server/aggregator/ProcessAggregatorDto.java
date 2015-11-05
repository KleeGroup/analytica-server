package io.analytica.server.aggregator;

import java.util.HashMap;
import java.util.Map;

public class ProcessAggregatorDto {
	
	private String time;
	private String category;
	private Map<String,String> values;
	
	public ProcessAggregatorDto( ){
		values= new HashMap<String,String>();
	}

	public void addMetric(final String metricName, final String metricValue){
		values.put(metricName, metricValue);
	}
	public void addMetrics(final Map<String,String> metrics){
		values.putAll(metrics);
	}
	

	public void setTime(String time) {
		this.time = time;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTime() {
		return time;
	}

	public String getCategory() {
		return category;
	}

	public Map<String, String> getValues() {
		return values;
	}
	
	public String getMeasure(final String metric){
		return values.get(metric);
	}
	
}
