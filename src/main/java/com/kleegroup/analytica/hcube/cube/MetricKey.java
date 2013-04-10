package com.kleegroup.analytica.hcube.cube;

import com.kleegroup.analytica.hcube.Identity;

/** 
 * Cl� de la m�trique.
 * @author npiedeloup, pchretien
 * @version $Id: Metric.java,v 1.5 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class MetricKey extends Identity {
	public MetricKey(String name) {
		super("metric:" + name);
	}
}
