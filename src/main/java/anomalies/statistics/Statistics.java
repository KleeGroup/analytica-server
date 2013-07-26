/**
 * Evaluate the prinipal characteristics of the signal
 * such as the mean , the variance, standard deviation,...
 */
package anomalies.statistics;

import java.util.List;

import kasperimpl.spaces.spaces.kraft.DataPoint;
import anomalies.signal.Signal;

/**
 * @author statchum
 * 
 */
public class Statistics {

	public Statistics() {
	}

	/*
	 * evaluates the mean of the last "period" mesures of a signal from limit position backward
	 */
	public double mean(final Signal signal, final int period, final int limit) {
		final List<DataPoint> points = signal.getPoints();
		double sum = 0, mean = 0;
		final int start = period > limit ? 0 : limit - period;
		int N = 0;
		for (int i = start; i < limit; i++) {
			N++;
			final DataPoint point = points.get(i);
			sum += point.getValue();
		}
		mean = sum / N;

		return mean;
	}

	/*
	 * evaluates the variance of the last "period" mesures of a signal from limit position backward
	 */
	public double variance(final Signal signal, final int period, final int limit) {
		final List<DataPoint> points = signal.getPoints();
		double sum = 0, mean = 0, mSum = 0;
		double variance = 0;
		int N = 0;
		final int start = period > limit ? 0 : limit - period;
		for (int i = start; i < limit; i++) {
			N++;
			final DataPoint point = points.get(i);
			sum += point.getValue() * point.getValue();
			mSum += point.getValue();
		}

		mean = mSum / N;
		variance = sum / N - mean * mean;

		return variance;
	}

	/*
	 * evaluates the standard deviation of the last "period" mesures of a signal from limit position backward
	 */
	public double standardDeviation(final Signal signal, final int period, final int limit) {
		final double variance = variance(signal, period, limit);
		return Math.sqrt(variance);
	}
}
