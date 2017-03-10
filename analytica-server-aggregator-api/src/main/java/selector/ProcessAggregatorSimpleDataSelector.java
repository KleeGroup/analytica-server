package selector;

import java.util.ArrayList;
import java.util.List;

public class ProcessAggregatorSimpleDataSelector implements ProcessAggregatorDataSelector{
	
	private List<ProcessAggregatorSimpleData> datas;
	
	public ProcessAggregatorSimpleDataSelector(){
		datas = new ArrayList<ProcessAggregatorSimpleData>();
	}
	
	public ProcessAggregatorSimpleDataSelector withSelector(final String selector){
		datas.add(new ProcessAggregatorSimpleData(selector));
		return this;
	}
	public ProcessAggregatorSimpleDataSelector withSelector(final String selector,final boolean notNull){
		datas.add(new ProcessAggregatorSimpleData(selector,notNull));
		return this;
	}
	public ProcessAggregatorSimpleDataSelector withSelector(final String selector, final Integer defaultValue){
		datas.add(new ProcessAggregatorSimpleData(selector,defaultValue));
		return this;
	}
	
	public ProcessAggregatorSimpleDataSelector withSelector(final String selector, final Integer defaultValue,final boolean notNull){
		datas.add(new ProcessAggregatorSimpleData(selector,defaultValue,notNull));
		return this;
	}
	
	public boolean isEmpty(){
		return datas.isEmpty();
	}
	
	public List<ProcessAggregatorSimpleData> getSelectors(){
		return datas;
	} 
	
	public class ProcessAggregatorSimpleData{
		private final String dataName;
		private final Integer defaultValue;
		private final boolean notNull;
		
		public ProcessAggregatorSimpleData(final String dataName, final Integer defaultValue){
			this.dataName=dataName;
			this.defaultValue=defaultValue;
			this.notNull=false;
		}
		
		public ProcessAggregatorSimpleData(final String dataName, final Integer defaultValue,final boolean notNull){
			this.dataName=dataName;
			this.defaultValue=defaultValue;
			this.notNull=notNull;
		}
		
		public ProcessAggregatorSimpleData(final String dataName){
			this.dataName=dataName;
			this.defaultValue=null;
			this.notNull=false;
		}

		public ProcessAggregatorSimpleData(final String dataName,final boolean notNull){
			this.dataName=dataName;
			this.defaultValue=null;
			this.notNull=notNull;
		}
		
		public String getDataName() {
			return dataName;
		}

		public Integer getDefaultValue() {
			return defaultValue;
		}

		public boolean isNotNull() {
			return notNull;
		}		
	}

	@Override
	public Integer getDefaultValue(String dataName) {
		for (ProcessAggregatorSimpleData processAggregatorSimpleData : datas) {
			if(processAggregatorSimpleData.getDataName().equalsIgnoreCase(dataName)){
				return processAggregatorSimpleData.getDefaultValue();
			}
		}
		return null;
	}
}
