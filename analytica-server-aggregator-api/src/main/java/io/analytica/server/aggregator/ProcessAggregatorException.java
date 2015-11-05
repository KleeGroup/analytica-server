package io.analytica.server.aggregator;

public class ProcessAggregatorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProcessAggregatorException(){
		super();
	}
	

	public ProcessAggregatorException(final String message) {
		super(message);
	}

	public ProcessAggregatorException(final String message, final Throwable t) {
		super(message, t);
	}
}
