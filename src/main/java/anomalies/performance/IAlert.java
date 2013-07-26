/**
 * 
 */
package anomalies.performance;

import kasperimpl.spaces.spaces.kraft.DataPoint;

/**
 * @author statchum
 * 
 */
public interface IAlert {
	void launch(DataPoint point);

}
