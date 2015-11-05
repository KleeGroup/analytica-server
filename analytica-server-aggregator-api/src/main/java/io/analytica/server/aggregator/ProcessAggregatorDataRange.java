package io.analytica.server.aggregator;

import java.util.Date;

import io.vertigo.lang.Assertion;

public class ProcessAggregatorDataRange {
	
	private final Date minDate;
	private final Date maxDate;
	private final String dimention;
	
	public ProcessAggregatorDataRange(final String dimention, final String timeFrom, final String timeTo){
		this.minDate = readDate(timeFrom, dimention);
		this.maxDate = readDate(timeTo, dimention);
		this.dimention = dimention;
	}
	
	
	public Date getMinDate() {
		return minDate;
	}


	public Date getMaxDate() {
		return maxDate;
	}


	public String getDimention() {
		return dimention;
	}


	private static Date readDate(final String timeStr, final String dimention) {
		Assertion.checkArgNotEmpty(timeStr);
		// ---------------------------------------------------------------------
		if ("NOW".equals(timeStr)) {
			return new Date();
		} else if (timeStr.startsWith("NOW-")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW-".length()));
			return new Date(System.currentTimeMillis() - deltaMs);
		} else if (timeStr.startsWith("NOW+")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW+".length()));
			return new Date(System.currentTimeMillis() + deltaMs);
		}
		throw new RuntimeException("time must be NOW or NOW+999f or NOW-999f where f is h|d|m");
	}
	
	
	/**
	 *
	 * @param deltaAsString Delta en millisecondes
	 * @return delta en millisecondes
	 */
	private static long readDeltaAsMs(final String deltaAsString) {
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
