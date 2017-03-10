package io.analytica.server.aggregator;

import io.analytica.api.Assertion;
import selector.ProcessAggregatorDataSelector;
import selector.ProcessAggregatorDataSelectorBuilder;

public class ProcessAggregatorQuery {
	
	final private ProcessAggregatorDataFilter aggregatorDataFilter;
	final private ProcessAggregatorDataSelector aggregatorDataSelector;
	final private ProcessAggregatorDataRange aggregatorDataRange;
	public static String  SEPARATOR="/";
	
	public ProcessAggregatorQuery(final ProcessAggregatorDataFilter aggregatorDataFilter,ProcessAggregatorDataSelector aggregatorDataSelector,final ProcessAggregatorDataRange aggregatorDataRange){
		Assertion.checkNotNull(aggregatorDataFilter, "The data filter must be specified");
		if(aggregatorDataSelector==null){
			this.aggregatorDataSelector = new ProcessAggregatorDataSelectorBuilder().withSimpleSelector("*").build();
		}
		else{
			this.aggregatorDataSelector=aggregatorDataSelector;
		}
		this.aggregatorDataRange = aggregatorDataRange;
		this.aggregatorDataFilter=aggregatorDataFilter;
		
	}



	public ProcessAggregatorDataFilter getAggregatorDataFilter() {
		return aggregatorDataFilter;
	}



	public ProcessAggregatorDataSelector getAggregatorDataSelector() {
		return aggregatorDataSelector;
	}


	public boolean hasRange(){
		return aggregatorDataRange!=null;
	}
	public ProcessAggregatorDataRange getAggregatorDataRange() {
		return aggregatorDataRange;
	}
	

}
