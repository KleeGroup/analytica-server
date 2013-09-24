/**
 * 
 */
package anomalies.performance.alert;

import java.util.ArrayList;
import java.util.List;

import kasperimpl.spaces.spaces.kraft.DataPoint;
import anomalies.performance.IAlert;

/**
 * @author statchum
 * 
 */
public class Alert implements IAlert {
	private int alertThrehsold;
	private int scale;// scale of the alert: the alert will be send according to its scale
	private int gravity; // gravity of the alert: the alert will be send according to its gravity
	private final List<DataPoint> abnormalousList;

	public Alert() {
		gravity = 0;
		abnormalousList = new ArrayList<DataPoint>();
	}

	/**
	 * @param gravity
	 */
	public void addgravity(final int gravity, final DataPoint point) {
		abnormalousList.add(point);
		this.gravity += gravity;
		if (this.gravity > alertThrehsold) {
			launch(point);
		}
	}

	public void launch(final DataPoint point) {
		final String msg = "Inconsistent measure occured on value =" + point.getValue() + " on " + point.getDate();
		final String subject = "[Analytica-Anomaly] Anomaly detected";
		final String to = "stephane.tatchum@kleegroup.com";
		final String from = "stephane.tatchum@kleegroup.com";
		//SendMail.send(from, to, subject, msg);
	}
}
