package io.analytica.server.aggregator;

public enum ProcessAggregatorDateType {
	PAST("PAST"),
	NOW("NOW"),
	FUTURE("FUTURE");

	private final String dateType;

	private ProcessAggregatorDateType(final String dateType) {
		this.dateType =dateType;
	}

	@Override
	public String toString() {
		return dateType;
	}
	
}
