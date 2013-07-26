/**
 * 
 */
package anomalies.test;

import java.util.Date;

import kasperimpl.spaces.spaces.kraft.DataPoint;
import anomalies.performance.PerformanceManager;
import anomalies.signal.Signal;

/**
 * @author statchum
 * 
 */
public class SampleTest extends Thread {
	PerformanceManager manager = new PerformanceManager(5, 0, 2);
	Signal sampleSignal = new Signal();
	private final int nbreMesures;

	private double resonseTime(final long val) {
		return 3.5 * Math.random();
	}

	SampleTest(final int nbreMesures) {
		this.nbreMesures = nbreMesures;
	}

	@Override
	public void run() {
		DataPoint point;
		for (int nombre = 1; nombre < nbreMesures; nombre++) {
			point = new DataPoint(new Date(System.currentTimeMillis()), resonseTime(0));
			manager.checkMeasure(point, sampleSignal);
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
				return;
			}
		}
	}

	public static void main(final String args[]) {
		final SampleTest lecompteur = new SampleTest(5000);

		lecompteur.start();

	}
}
