package selector;

import io.analytica.api.Assertion;

public class ProcessAggregatorDataSelectorBuilder {
	public final static String SELECTOR_SEPARATOR=";";
	public final static String SELECTOR_DETAIL=":";
	public final static String SELECTOR_AGGRETATION_START="(";
	public final static String SELECTOR_ARGUMENTS_START="[";
	public final static String SELECTOR_DEFAULT_START="!";
	public final static String SELECTOR_NOT_NULL="#";
	public final static String SELECTOR_ARGUMENTS_SEPARATOR=",";
	
	private final StringBuilder selectorsBuilder;
	
	public ProcessAggregatorDataSelectorBuilder(){
		selectorsBuilder = new StringBuilder();
	}

	public ProcessAggregatorDataSelectorBuilder withAggregatedSelector(final String dataName, final ProcessAggregatorDataSelectorType dataSelectorType){
		Assertion.checkState(dataSelectorType!=ProcessAggregatorDataSelectorType.CLUSTERED, "Wrong Selector. Needed a aggregated selector. Found a clustered selector");
		selectorsBuilder.append(SELECTOR_SEPARATOR).append(dataSelectorType.toString()).append(SELECTOR_DETAIL).append(dataName);
		return this;
	}

	public ProcessAggregatorDataSelectorBuilder withClusteredSelector(final String dataName,final String border){
		selectorsBuilder.append(SELECTOR_SEPARATOR).append(ProcessAggregatorDataSelectorType.CLUSTERED.toString()).append(SELECTOR_DETAIL).append(dataName).append(SELECTOR_DETAIL).append(border);
		return this;
	}
	
	public ProcessAggregatorDataSelectorBuilder withSimpleSelector(final String dataName){
		selectorsBuilder.append(SELECTOR_SEPARATOR).append(dataName);
		return this;
	}
	
	public ProcessAggregatorDataSelectorBuilder withRawSelectors(final String rawSelectors){
		selectorsBuilder.append(SELECTOR_SEPARATOR).append(rawSelectors);
		return this;
	}
	
	public final String getLabel(final String data , final ProcessAggregatorDataSelectorType selectorType){
		return data + ":" + selectorType.toString();
	}
	public ProcessAggregatorDataSelector build(){
		String[] selectors = selectorsBuilder.toString().split(ProcessAggregatorDataSelectorBuilder.SELECTOR_SEPARATOR);
		Assertion.checkArgument(selectors.length>0, "Cannot create a InfluxDBDataQuery from empty data");
		boolean isSimpleSelector = false;
		boolean isAgggregatedSelector = false;
		boolean isClusteredSelector = false;
		final ProcessAggregatorClusteredDataSelector clusteredDataSelector = new ProcessAggregatorClusteredDataSelector();
		final ProcessAggregatorAggregatedDataSelector aggregatedDataSelector = new ProcessAggregatorAggregatedDataSelector();
		final ProcessAggregatorSimpleDataSelector simpleDataSelector = new ProcessAggregatorSimpleDataSelector();
		for(int i = 0; i<selectors.length; i++){
			if(selectors[i].isEmpty()){
				continue;
			}
			ProcessAggregatorRawSelector rawSelector = new ProcessAggregatorRawSelector(selectors[i]);
			if(!rawSelector.hasAggregationRule()){
				isSimpleSelector=true;
            	simpleDataSelector.withSelector(rawSelector.getDataName(),rawSelector.getDefaultValue(),rawSelector.isNotNull());
			}
			else if(rawSelector.isClustered()){
				isClusteredSelector= true;
				clusteredDataSelector.withCluster(rawSelector.getDataName(),Integer.parseInt(rawSelector.getArguments().get(0)),selectors[i],rawSelector.getDefaultValue());
			}
			else{
				isAgggregatedSelector=true;
				aggregatedDataSelector.withSelector(rawSelector.getDataName(), rawSelector.getSelectorType(),selectors[i],rawSelector.getDefaultValue(),rawSelector.isNotNull());
			}
			Assertion.checkArgument(!AtLeastTwo(isSimpleSelector, isAgggregatedSelector, isClusteredSelector),"Cannot use multiple types of selectors");
		}
		if(isSimpleSelector){
			return simpleDataSelector;
		}
		if(isAgggregatedSelector){
			return aggregatedDataSelector;
		}
		if(isClusteredSelector){
			return clusteredDataSelector;
		}
		return null;
	}
	
	private boolean AtLeastTwo(final boolean first, final boolean second, final boolean third){
		return first&&second || first&&third || second&&third;
	
	}

}
