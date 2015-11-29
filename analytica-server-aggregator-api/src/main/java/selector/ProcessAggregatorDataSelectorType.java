package selector;

public enum ProcessAggregatorDataSelectorType {
	MEAN("MEAN"),
	MAX("MAX"),
	MIN("MIN"),
	SUM("SUM"),
	COUNT("COUNT"),
	CLUSTERED("CLUSTERED");

	private final String processAggregatorDataSelectorType;

	private ProcessAggregatorDataSelectorType(final String processAggregatorDataSelectorType) {
		this.processAggregatorDataSelectorType = processAggregatorDataSelectorType.toUpperCase();
	}

	@Override
	public String toString() {
		return processAggregatorDataSelectorType;
	}
	
	public static ProcessAggregatorDataSelectorType fromString(String text) {
	    if (text != null) {
	      for (ProcessAggregatorDataSelectorType processAggregatorDataSelectorType : ProcessAggregatorDataSelectorType.values()) {
	        if (text.equalsIgnoreCase(processAggregatorDataSelectorType.processAggregatorDataSelectorType)) {
	          return processAggregatorDataSelectorType;
	        }
	      }
	    }
	    return null;
	  }
}
