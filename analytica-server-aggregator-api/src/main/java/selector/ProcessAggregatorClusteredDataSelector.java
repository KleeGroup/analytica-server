package selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ProcessAggregatorClusteredDataSelector implements ProcessAggregatorDataSelector{
	private List<ProcessAggregatorClusteredData> aggretatorData;
	private Map<String, SortedSet<Integer>> rawCluster;
	private Map<String, Map<Integer,Integer>>defaltVaules;
	private Map<String, Map<Integer,String>> labels;
	private boolean isBuildNeeded = true;
	
	public ProcessAggregatorClusteredDataSelector(){
		aggretatorData = new ArrayList<ProcessAggregatorClusteredDataSelector.ProcessAggregatorClusteredData>();
		rawCluster = new HashMap<String, SortedSet<Integer>>();
		labels = new HashMap<String, Map<Integer,String>>();
		defaltVaules = new HashMap<String, Map<Integer,Integer>>();
	}

	public ProcessAggregatorClusteredDataSelector withCluster(final String dataName, final int minValue, final int maxValue, final String label){
		aggretatorData.add(new ProcessAggregatorClusteredData(dataName,minValue,maxValue,label) );
		return this;
	}

	public ProcessAggregatorClusteredDataSelector withCluster(final String dataName, final int value,final String label){
		return withCluster(dataName, value, label, null);
	}
	
	public ProcessAggregatorClusteredDataSelector withCluster(final String dataName, final int value,final String label, final Integer defaultValue){
		if ( !rawCluster.containsKey(dataName)){
			rawCluster.put(dataName, new TreeSet<Integer>());
			rawCluster.get(dataName).add(Integer.MAX_VALUE);
			rawCluster.get(dataName).add(Integer.MIN_VALUE);
		}
		if(!labels.containsKey(dataName)){
			labels.put(dataName, new HashMap<Integer, String>());
		}
		if(!defaltVaules.containsKey(dataName)){
			defaltVaules.put(dataName, new HashMap<Integer, Integer>());
		}
		labels.get(dataName).put(value, label);
		rawCluster.get(dataName).add(value);
		defaltVaules.get(dataName).put(value, defaultValue);
		return this;
	}
	
	public List<ProcessAggregatorClusteredData> getAggretatorData() {
		if(isBuildNeeded){
			isBuildNeeded=false;
			for(Map.Entry<String, SortedSet<Integer>> entry : rawCluster.entrySet()){
				ArrayList<Integer> limits = new ArrayList<Integer>(entry.getValue());
				for (int i= 0; i<limits.size()-1; i++){
					 String label = null;
					 if(labels.containsKey(entry.getKey())){
						if( labels.get(entry.getKey()).containsKey(limits.get(i+1))){
							label = labels.get(entry.getKey()).get(limits.get(i+1));
						}
						else {
							label = labels.get(entry.getKey()).get(limits.get(i));
						}
					 }
					 Integer defaultValue = null;
					 if(defaltVaules.containsKey(entry.getKey())){
							if( defaltVaules.get(entry.getKey()).containsKey(limits.get(i+1))){
								defaultValue = defaltVaules.get(entry.getKey()).get(limits.get(i+1));
							}
							else {
								defaultValue = defaltVaules.get(entry.getKey()).get(limits.get(i));
							}
						 }
					aggretatorData.add(new ProcessAggregatorClusteredData(entry.getKey(), limits.get(i), limits.get(i+1),defaultValue,label));
				}
			}
		}
		return aggretatorData;
	}

	public class ProcessAggregatorClusteredData {
		private final String dataName;
		private final Integer minValue;
		private final Integer maxValue;
		private final Integer defaultValue;
		private final String label;
		
		public ProcessAggregatorClusteredData(String dataName,Integer minValue, Integer maxValue, String label){
			this.minValue=minValue;
			this.maxValue=maxValue;
			this.dataName=dataName;
			this.label=label;
			this.defaultValue=null;
		}
		
		public ProcessAggregatorClusteredData(String dataName,Integer minValue, Integer maxValue,final Integer defaultValue, String label){
			this.minValue=minValue;
			this.maxValue=maxValue;
			this.dataName=dataName;
			this.label=label;
			this.defaultValue=defaultValue;
		}
		
		public String getDataName() {
			return dataName;
		}
		
		public Integer getMinValue() {
			return minValue;
		}
		
		public Integer getMaxValue() {
			return maxValue;
		}

		public Integer getDefaultValue() {
			return defaultValue;
		}

		public String getLabel() {
			return label;
		}
		
	}

	@Override
	public Integer getDefaultValue(String dataName) {
		for (ProcessAggregatorClusteredData processAggregatorClusteredData : aggretatorData) {
			if(processAggregatorClusteredData.getDataName().equalsIgnoreCase(dataName)){
				return processAggregatorClusteredData.getDefaultValue();
			}
		}
		return null;
	}
}
