package io.analytica.server.aggregator;

public enum ProcessAggregatorDateRoundType {
	FLOOR("FLOOR"),
	CEIL("CEIL"),
	NO_ROUND("NO_ROUND");

	private final String dateRoundType;

	private ProcessAggregatorDateRoundType(final String dateRoundType) {
		this.dateRoundType =dateRoundType;
	}

	@Override
	public String toString() {
		return dateRoundType;
	}
	
}
