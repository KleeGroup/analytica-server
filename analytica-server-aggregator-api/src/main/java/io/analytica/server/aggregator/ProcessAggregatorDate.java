package io.analytica.server.aggregator;

import io.vertigo.lang.Assertion;

import java.util.Date;

public class ProcessAggregatorDate {

	private  ProcessAggregatorDateType dateType;
	private Date date;
	private  String delta;
	
	public ProcessAggregatorDate(final String stringDate, String dimention, ProcessAggregatorDateRoundType round){
		Assertion.checkArgNotEmpty(stringDate);
		// ---------------------------------------------------------------------
		final long dateWithoutRoundMs = System.currentTimeMillis();
		final long dimentionMs = readDeltaAsMs(dimention);
		final long dateWithRoundMs;
		if(round.equals(ProcessAggregatorDateRoundType.CEIL)){
			dateWithRoundMs = (dateWithoutRoundMs/dimentionMs +1)*dimentionMs;
		}
		else if(round.equals(ProcessAggregatorDateRoundType.FLOOR)){
			dateWithRoundMs = (dateWithoutRoundMs/dimentionMs)*dimentionMs;
		}
		else{
			dateWithRoundMs=dateWithoutRoundMs;
		}

		if ("NOW".equals(stringDate)) {
			date= new Date(dateWithRoundMs);
			dateType=ProcessAggregatorDateType.NOW;
			delta=null;
		} else if (stringDate.startsWith("NOW-")) {
			dateType=ProcessAggregatorDateType.PAST;
			delta=stringDate.substring("NOW-".length());
			final long deltaMs = readDeltaAsMs(delta);
			date = new Date(dateWithRoundMs - deltaMs);
		} else if (stringDate.startsWith("NOW+")) {
			dateType=ProcessAggregatorDateType.FUTURE;
			delta=stringDate.substring("NOW+".length());
			final long deltaMs = readDeltaAsMs(delta);
			date = new Date(dateWithRoundMs + deltaMs);
		}
		else{
		throw new RuntimeException("time must be NOW or NOW+999f or NOW-999f where f is h|d|m");
		}
	}
	
	public ProcessAggregatorDateType getDateType() {
		return dateType;
	}


	public Date getDate() {
		return date;
	}

	public String getDelta() {
		return delta;
	}



	/**
	 *
	 * @param deltaAsString Delta en millisecondes
	 * @return delta en millisecondes
	 */
	private long readDeltaAsMs(final String deltaAsString) {
		final Long delta;
		char unit = deltaAsString.charAt(deltaAsString.length() - 1);

		if (unit >= '0' && unit <= '9') {
			unit = 'd';
			delta = Long.valueOf(deltaAsString);
		} else {
			delta = Long.valueOf(deltaAsString.substring(0, deltaAsString.length() - 1));
		}

		switch (unit) {
			case 'd'://day
				return delta * 24 * 60 * 60 * 1000L;

			case 'h'://hour
				return delta * 60 * 60 * 1000L;

			case 'm': //minute
				return delta * 60 * 1000L;

			default:
				throw new RuntimeException("La durée doit préciser l'unité de temps utilisée : d=jour, h=heure, m=minute");
		}
	}

}
