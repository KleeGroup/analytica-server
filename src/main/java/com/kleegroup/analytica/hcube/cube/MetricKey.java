package com.kleegroup.analytica.hcube.cube;

import com.kleegroup.analytica.hcube.Identity;

public final class MetricKey extends Identity {
	public MetricKey(String name) {
		super("metric:" + name);
	}
}
