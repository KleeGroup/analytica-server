package io.analytica.server.aggregator;

public class ProcessAggregatorDataRange {
	
	private final ProcessAggregatorDate minDate;
	private final ProcessAggregatorDate maxDate;
	private final String dimention;
	
	public ProcessAggregatorDataRange(final String dimention, final String timeFrom, final String timeTo){
		this.minDate = new ProcessAggregatorDate(timeFrom,dimention,ProcessAggregatorDateRoundType.FLOOR);
		this.maxDate = new ProcessAggregatorDate(timeTo,dimention,ProcessAggregatorDateRoundType.CEIL);
		this.dimention = dimention;
	}
	
	public String getDimention() {
		return dimention;
	}

	public ProcessAggregatorDate getMinDate() {
		return minDate;
	}

	public ProcessAggregatorDate getMaxDate() {
		return maxDate;
	}
}
