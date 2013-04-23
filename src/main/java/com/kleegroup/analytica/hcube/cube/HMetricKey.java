package com.kleegroup.analytica.hcube.cube;

import com.kleegroup.analytica.hcube.HKey;

/** 
 * Clé de la métrique.
 * @author npiedeloup, pchretien
 * @version $Id: Metric.java,v 1.5 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class HMetricKey extends HKey {
	private final boolean clustered;

	public HMetricKey(String name, boolean clustered) {
		super(name);
		this.clustered = clustered;
	}

	public boolean isClustered() {
		return clustered;
	}

}
