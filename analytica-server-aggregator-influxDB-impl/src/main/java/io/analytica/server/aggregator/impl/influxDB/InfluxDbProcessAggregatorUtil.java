package io.analytica.server.aggregator.impl.influxDB;

import io.analytica.api.Assertion;
import io.analytica.server.aggregator.ProcessAggregatorDto;
import io.analytica.server.aggregator.ProcessAggregatorException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

public class InfluxDbProcessAggregatorUtil {
	
	public static List<ProcessAggregatorDto> getResults(QueryResult queryResult,final String category, final String... columns) throws ProcessAggregatorException{
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
						if(serie.getColumns().get(i).equalsIgnoreCase(InfluxDBProcessAggregatorConnector.TAG_TIME)){
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
							try {
								Date date = dateFormat.parse(String.valueOf(values.get(i)));
								aggregatorDto.setTime(""+date.getTime());
							} catch (ParseException e) {
								aggregatorDto.setTime(String.valueOf(values.get(i)));
							}
							
						}
						else{
							if(!filterColumns||columnsArrayList.contains(serie.getColumns().get(i)))
							{
								aggregatorDto.addMetric(serie.getColumns().get(i), String.valueOf(values.get(i)));
							}
						}
					}
					treatedResults.add(aggregatorDto);
				}
			}
		}
		return treatedResults;
	}
	
//	public static  List<String> getResults(QueryResult queryResult, final String column)throws ProcessAggregatorException{
//		List<String> results = new ArrayList<String>();
//		List<Map<String,String>> mapResults = getResults(queryResult, new String[]{column});
//		for (Map<String, String> map : mapResults) {
//			if(map.containsKey(column)){
//				results.add(map.get(column));
//			}
//		}
//		return results;
//	}
	
	//private methodes
	public static  String correctCaracters(final String argument){
		return argument.replaceAll("/", "\\\\/");
	}
	public static  String getRegexTag(final String argument){
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
	
	public static  String getRegexMeasurement(final String argument){
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
