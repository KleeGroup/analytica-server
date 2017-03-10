package selector;

import io.analytica.api.Assertion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessAggregatorRawSelector {
	private final String dataName;
	private ProcessAggregatorDataSelectorType selectorType=null;
	private List<String> arguments=new ArrayList<String>();
	private Integer defaultValue=null;
	private boolean notNull=false;
	
	public ProcessAggregatorRawSelector(final String selector){
		Assertion.checkArgNotEmpty(selector);
		//-----------------------------------------
		String [] values = selector.split(ProcessAggregatorDataSelectorBuilder.SELECTOR_DETAIL);
		dataName = values[0];
		for(int i=1; i<values.length;i++){
			if(values[i].startsWith(ProcessAggregatorDataSelectorBuilder.SELECTOR_AGGRETATION_START)){
				String selectorRule = values[i].substring(1, values[i].length()-1);
				selectorType = ProcessAggregatorDataSelectorType.fromString(selectorRule);
				Assertion.checkNotNull(selectorType," Unknown seletor rule "+selectorRule + " in "+values[i]+" in "+selector);
			}
			else if(values[i].startsWith(ProcessAggregatorDataSelectorBuilder.SELECTOR_ARGUMENTS_START)){
				arguments= Arrays.asList(values[i].substring(1, values[i].length()-1).split(ProcessAggregatorDataSelectorBuilder.SELECTOR_ARGUMENTS_SEPARATOR));
			}
			else if(values[i].startsWith(ProcessAggregatorDataSelectorBuilder.SELECTOR_DEFAULT_START)){
				defaultValue=Integer.parseInt(values[i].substring(1));
			}
			else if(values[i].startsWith(ProcessAggregatorDataSelectorBuilder.SELECTOR_NOT_NULL)){
				notNull = Boolean.parseBoolean(values[i].substring(1));
			}
			else {
				throw new IllegalArgumentException("Unable to parse "+selector+". Unkown attribute "+values[i]);
			}
		}
		checkConsistency();
	}

	public boolean hasAggregationRule(){
		return selectorType!=null;
	}
	
	public boolean isClustered(){
		return hasAggregationRule()&&selectorType.equals(ProcessAggregatorDataSelectorType.CLUSTERED);
	}
	
	public void checkConsistency(){
		if(isClustered()){
			Assertion.checkArgument(arguments.size()==1, "Needed one argument for the clustered aggregation. Found "+arguments.size());
		}
		if(defaultValue!=null){
			Assertion.checkArgument(!notNull,"Impossible to set a default value if all the null values are filtered");
		}
	}

	public ProcessAggregatorDataSelectorType getSelectorType() {
		return selectorType;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public Integer getDefaultValue() {
		return defaultValue;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public String getDataName() {
		return dataName;
	}
	
	
}
