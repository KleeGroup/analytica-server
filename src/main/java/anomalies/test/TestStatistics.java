/**
 * 
 */
package anomalies.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import kasperimpl.spaces.spaces.kraft.DataPoint;

import org.junit.Before;
import org.junit.Test;

import anomalies.signal.Signal;

/**
 * @author statchum
 * 
 */
public class TestStatistics {

	private Signal signal;

	@Before
	public void buildSignal() {
		final List<DataPoint> points = new ArrayList<DataPoint>();
		DataPoint point;
		// -------------------------------------------------------

		for (int i = 1; i <= 60; i++) {

			point = new DataPoint(new Date(), i);
			points.add(point);
		}
		signal = new Signal(points, 60);
	}

	@Test
	public void testMean() {
		Assert.assertEquals(30.5, signal.getMean(60, 60), 0.01);
	}

	@Test
	public void testVariance() {
		Assert.assertEquals(299.9166667, signal.getVariance(60, 60), 0.01);
	}

	@Test
	public void testStandardDeviation() {
		Assert.assertEquals(17.31810228, signal.getStandardDeviation(60, 60), 0.01);
	}
}
