/**
 * 
 */
package anomalies.signal;

import java.util.ArrayList;
import java.util.List;

import kasperimpl.spaces.spaces.kraft.DataPoint;
import anomalies.statistics.Statistics;

/**
 * @author statchum
 * 
 */
public class Signal {
	// Un signal est une liste de nombre complexes
	private final List<DataPoint> points;
	private double mean, sigma;
	private final int period; // number of points in the signal to be considered

	public static Statistics stats = new Statistics();

	// -----------------------------------------------------------------
	public Signal() {
		points = new ArrayList<DataPoint>();
		mean = sigma = 0;
		period = 30;
	}

	public Signal(final List<DataPoint> points) {
		this.points = points;
		mean = sigma = 0;
		period = points.size();
	}

	public Signal(final List<DataPoint> points, final int period) {
		this.points = points;
		this.period = period;

		mean = getMean(period, points.size());
		sigma = getStandardDeviation(period, points.size());
	}

	public List<DataPoint> getPoints() {
		return points;
	}

	public double getMean(final int period, final int limit) {
		return stats.mean(this, period, limit);
	}

	public double getStandardDeviation(final int period, final int limit) {
		return stats.standardDeviation(this, period, limit);
	}

	/*
	 * nsd : number of standard deviations
	 */
	public double getVariance(final int period, final int limit) {
		return stats.variance(this, period, limit);
	}

	/*
	 * upperBrand value of the last point of the signal
	 */
	public double upperBand(final int nsd) {
		return mean + sigma * nsd;
	}

	/*
	 * lowerBrand value of the last point of the signal
	 */
	public double lowerBand(final int nsd) {
		return mean - sigma * nsd;
	}

	public void addPoint(final DataPoint point) {
		points.add(point);
		mean = getMean(period, points.size());
		sigma = getStandardDeviation(period, points.size());
	}

}
