package io.analytica.server.aggregator.impl.influxDB;

import io.analytica.api.Assertion;
import io.analytica.server.aggregator.ProcessAggregatorDto;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQuery;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import selector.ProcessAggregatorDataSelector;
import selector.ProcessAggregatorDataSelectorType;

public class Util {
	
	public static List<ProcessAggregatorDto> aggregateResults(List<List<ProcessAggregatorDto>> results){
		Map <Long,ProcessAggregatorDto> aggregatedResults = new TreeMap<Long,ProcessAggregatorDto>();
		for (List<ProcessAggregatorDto> list : results) {
			for (ProcessAggregatorDto processAggregatorDto : list) {
				if(!aggregatedResults.containsKey(processAggregatorDto.getTime())){
					
					ProcessAggregatorDto aggregatorDto = new ProcessAggregatorDto();
					aggregatorDto.setTime(processAggregatorDto.getTime());
					aggregatorDto.setCategory(processAggregatorDto.getCategory());
					aggregatedResults.put(processAggregatorDto.getTime(), aggregatorDto);
				}
				aggregatedResults.get(processAggregatorDto.getTime()).addMetrics(processAggregatorDto.getValues());
			}
		}
		return new ArrayList<ProcessAggregatorDto>(aggregatedResults.values());
	}
	
	public static List<ProcessAggregatorDto> parseResults(QueryResult queryResult,final ProcessAggregatorDataSelector selector, final String category, final String... columns) throws ProcessAggregatorException{
		List<ProcessAggregatorDto> treatedResults = new ArrayList<ProcessAggregatorDto>();
		List<Result> results = queryResult.getResults();
		List<String> columnsArrayList = Arrays.asList(columns);
		boolean filterColumns=columns!=null && columns.length>0;
		if(!isResultEmpty(results)){
			Assertion.checkArgument(results.size()==1 , "");
			List<Series> series = results.get(0).getSeries();
			for (Series serie : series) {
				int numberOfColumns = serie.getColumns().size();
				for (List<Object> values : serie.getValues()) {
					if(values.size()!=numberOfColumns){
						throw new ProcessAggregatorException("Error while parsing the Result. The number of columns doesnt match");
					}
					ProcessAggregatorDto aggregatorDto = new ProcessAggregatorDto();
					aggregatorDto.setCategory(category);
					for(int i=0;i<numberOfColumns;i++){
						if(serie.getColumns().get(i).equalsIgnoreCase(InfluxDBQuery.TAG_TIME)){
							if(String.valueOf(values.get(i)).length()==0){
								aggregatorDto.setTime(null);
							}
							else{
								try {
									Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(String.valueOf(values.get(i)));
									aggregatorDto.setTime((date.getTime()+TimeZone.getTimeZone("CET").getRawOffset()));
								}
								catch (RuntimeException e){
									aggregatorDto.setTime(null);
								}
								catch (ParseException e) {
									aggregatorDto.setTime(Long.parseLong(String.valueOf(values.get(i))));
								}
							}
						}
						else{
							if(!filterColumns||columnsArrayList.contains(serie.getColumns().get(i)))
							{
								Object result =values.get(i);
								if(result==null){
									result=selector.getDefaultValue(serie.getColumns().get(i));
								}
								aggregatorDto.addMetric(serie.getColumns().get(i), String.valueOf(result));
							}
						}
					}
					treatedResults.add(aggregatorDto);
				}
			}
		}
		return treatedResults;
	}
	
	
	//private methodes
	public static  String correctCaracters(final String argument){
		return argument.replaceAll("/", "\\\\/");
	}
	
	public static String getRegexAggregatedMeasurement(final String data, final ProcessAggregatorDataSelectorType aggregationRule, final String label){
		StringBuilder builder = new StringBuilder();
			 builder.append(aggregationRule)
					.append("(\"").append(data).append("\") AS \"")
					.append(label).append("\"");
		return builder.toString();
	}
	
	public static String getNotNullComparison(final String argument){
		StringBuilder builder = new StringBuilder();
		builder.append(" (")
		.append("\"").append(argument).append("\" > 0 OR ")
		.append("\"").append(argument).append("\" < 0 OR ")
		.append("\"").append(argument).append("\"=").append(getRegexFilterTag(null))
		.append(") ");
		return builder.toString();
	}
	
	public static  String getRegexFilterTag(final String argument){
		if (argument==null){
			return "~ /.*/";
		}
		final String localArgument=correctCaracters(argument);
		if (localArgument.compareToIgnoreCase("*")==0){
			return "~ /.*/";
		}
		int indexOfStar = localArgument.indexOf("*");
		if(indexOfStar==0){
			return "~ /."+localArgument+".*/";
		}
		return "~ /.*"+localArgument+".*/";
	}
	
	public static String getRegexDate(final String date){
		return "'"+date+"'";
	}
	public static String getRegexMeasurement(final String argument){
	Assertion.checkNotNull(argument);	
	return "\"" + argument +"\"";
	}
	
	public static  String getRegexFilterMeasurement(final String argument){
		if (argument==null){
			return "/.*/";
		}
		final String localArgument=correctCaracters(argument);
		if (localArgument.compareToIgnoreCase("*")==0){
			return "/.*/";
		}
		int indexOfStar = localArgument.indexOf("*");
		if(indexOfStar==0){
			return "/."+localArgument+".*/";
		}
		return "/.*"+localArgument+".*/";
	}
	
	
	public static boolean  isResultEmpty(List<Result> results){
		return results.isEmpty() || (results.size()==1 && (results.get(0).getSeries()==null || results.get(0).getSeries().isEmpty()));
	}

}
