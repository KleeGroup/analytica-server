package selector;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessAggregatorAggregatedDataSelector implements ProcessAggregatorDataSelector {
	private List<ProcessAggregatorAggregatedData> datas;
	
	public ProcessAggregatorAggregatedDataSelector(){
		datas = new ArrayList<ProcessAggregatorAggregatedDataSelector.ProcessAggregatorAggregatedData>();
	}
	
	public ProcessAggregatorAggregatedDataSelector withSelector(final String dataName,final ProcessAggregatorDataSelectorType selectorType, final String label){
		datas.add(new ProcessAggregatorAggregatedData (dataName,selectorType,label));
		return this;
	}
	
	public ProcessAggregatorAggregatedDataSelector withSelector(final String dataName,final ProcessAggregatorDataSelectorType selectorType, final String label,final boolean notNull){
		datas.add(new ProcessAggregatorAggregatedData (dataName,selectorType,label,notNull));
		return this;
	}
	
	public ProcessAggregatorAggregatedDataSelector withSelector(final String dataName,final ProcessAggregatorDataSelectorType selectorType, final String label,final Integer defaultValue){
		datas.add(new ProcessAggregatorAggregatedData (dataName,selectorType,label,defaultValue));
		return this;
	}

	public ProcessAggregatorAggregatedDataSelector withSelector(final String dataName,final ProcessAggregatorDataSelectorType selectorType, final String label,final Integer defaultValue, final boolean notNull){
		datas.add(new ProcessAggregatorAggregatedData (dataName,selectorType,label,defaultValue,notNull));
		return this;
	}
	public boolean isEmpty(){
		return datas.isEmpty();
	}
	
	public List<ProcessAggregatorAggregatedData> getSelectors(){
		return datas;
	} 
	
	public class ProcessAggregatorAggregatedData {
		private final String dataName;
		private final ProcessAggregatorDataSelectorType selectorType;
		private final Integer defaultValue;
		private final String label;
		private final boolean notNull;
		
		public ProcessAggregatorAggregatedData(final String dataName,final ProcessAggregatorDataSelectorType selectorType, final String label){
			this.dataName=dataName;
			this.selectorType=selectorType;
			this.label = label;
			this.defaultValue=null;
			this.notNull=false;
		}
		
		public ProcessAggregatorAggregatedData(final String dataName,final ProcessAggregatorDataSelectorType selectorType, final String label, final boolean notNull){
			this.dataName=dataName;
			this.selectorType=selectorType;
			this.label = label;
			this.defaultValue=null;
			this.notNull=notNull;
		}
		
		public ProcessAggregatorAggregatedData(final String dataName,final ProcessAggregatorDataSelectorType selectorType, final String label,final Integer defaultValue){
			this.dataName=dataName;
			this.selectorType=selectorType;
			this.label = label;
			this.defaultValue=defaultValue;
			this.notNull=false;
		}
		
		public ProcessAggregatorAggregatedData(final String dataName,final ProcessAggregatorDataSelectorType selectorType, final String label,final Integer defaultValue, final boolean notNull){
			this.dataName=dataName;
			this.selectorType=selectorType;
			this.label = label;
			this.defaultValue=defaultValue;
			this.notNull=notNull;
		}
		
		public String getDataName() {
			return dataName;
		}
		
		public ProcessAggregatorDataSelectorType getSelectorType() {
			return selectorType;
		}
		
		public Integer getDefaultValue() {
			return defaultValue;
		}
		
		public String getLabel() {
			return label;
		}

		public boolean isNotNull() {
			return notNull;
		}
}

	@Override
	public Integer getDefaultValue(String dataName) {
		for (ProcessAggregatorAggregatedData processAggregatorAggregatedData : datas) {
			
			if(processAggregatorAggregatedData.getLabel().equalsIgnoreCase(dataName)){
				return processAggregatorAggregatedData.getDefaultValue();
			}
		}
		return null;
	}
}
