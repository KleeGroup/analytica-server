package io.analytica.server.aggregator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.analytica.api.KProcess;
import io.analytica.api.KProcessBuilder;

public class ProcessAggregatorUtil {
	
	public static final String COUNT="Count";
	public static final String MEAN="Mean";
	public static final String MAX="Max";
	public static final String MIN="Min";
	
	public static List<KProcess> flatProcess(KProcess process){
		List<KProcessBuilder> processBuilders= new ArrayList<KProcessBuilder>();
		doFlatProcess(process, processBuilders);
		List<KProcess> processes = new ArrayList<KProcess>();
		for (KProcessBuilder processBuilder : processBuilders) {
			processes.add(processBuilder.build());
		}
		return processes;
	}

	
	private static Map<String,List<Double>> doFlatProcess(KProcess process, List<KProcessBuilder> parentProcessBuilders){
		Map<String,List<Double>> myMeasures = new HashMap<String, List<Double>>();
		Map<String,List<Double>> allChildMeasures = new HashMap<String, List<Double>>();
		Double subDuration = process.getMeasures().get(KProcess.SUB_DURATION)==null?0:process.getMeasures().get(KProcess.SUB_DURATION);
		myMeasures.put(process.getType(), new ArrayList<Double>(Arrays.asList(process.getDuration()-subDuration)));
		KProcessBuilder currentProcessBuilder = recreateBuilderFromProcess(process);
		if(process.getSubProcesses()!=null && !process.getSubProcesses().isEmpty()){
			for (KProcess childProcess : process.getSubProcesses()) {
				Map<String,List<Double>> childMeasures = doFlatProcess(childProcess,parentProcessBuilders);
				sumMeasures(allChildMeasures, childMeasures);
			}
		}
		for(Map.Entry<String,List<Double>> entry : allChildMeasures.entrySet()){
			for(Map.Entry<String, Double> minMaxMeanCount : getMinMaxMeanCountMetrics(entry.getKey(), entry.getValue()).entrySet())
			currentProcessBuilder.incMeasure(minMaxMeanCount.getKey(), minMaxMeanCount.getValue());
		}
		parentProcessBuilders.add(currentProcessBuilder);
		sumMeasures(myMeasures, allChildMeasures);
		return myMeasures;
	}
	
	private static Map<String,Double> getMinMaxMeanCountMetrics(final String name, final List<Double> metrics){
		Map<String,Double> minMaxMeanCount = new HashMap<String, Double>();
		if(!metrics.isEmpty()){
			int count = metrics.size();
			double sum = metrics.get(0);
			double min = metrics.get(0);
			double max = metrics.get(0);
			for(int i=1; i<count; i++){
				if(metrics.get(i)<min){
					min=metrics.get(i);
				}
				else{
					if(metrics.get(i)>max){
						max=metrics.get(i);
					}
				}
				sum+=metrics.get(i);
			}
			minMaxMeanCount.put(name+COUNT, (double) count);
			minMaxMeanCount.put(name+MIN,  min);
			minMaxMeanCount.put(name+MAX,  max);
			minMaxMeanCount.put(name+MEAN,  sum/(double)count);
		}
		return minMaxMeanCount;
	}
	private static void sumMeasures(Map<String,List<Double>> measuresInto, Map<String,List<Double>> measuresFrom){
		for(Map.Entry<String, List<Double>> entry : measuresFrom.entrySet()){
			if(!measuresInto.containsKey(entry.getKey())){
				measuresInto.put(entry.getKey(), new ArrayList<Double>());
			}
			 measuresInto.get(entry.getKey()).addAll( entry.getValue());
		}
	}
	
	private static KProcessBuilder recreateBuilderFromProcess (KProcess process){
		KProcessBuilder processBuilder = new KProcessBuilder(process.getAppName(), process.getType(), process.getStartDate(), process.getDuration());
		processBuilder.withCategory(process.getCategory()).withLocation(process.getLocation());
		for(Map.Entry<String,Double> entry : process.getMeasures().entrySet()){
			processBuilder.incMeasure(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<String, String> entry : process.getMetaDatas().entrySet()){
			processBuilder.addMetaData(entry.getKey(),entry.getValue());
		}
		return processBuilder;
	}
}
