package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.cube.HMetricKey;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final  class AppMetricStore {
	private final Map<String, HMetricKey> metricKeys = Collections.synchronizedMap(new HashMap<String, HMetricKey>());

	void register(HMetricKey metricKey) {
		Assertion.checkNotNull(metricKey);
		//---------------------------------------------------------------------
		metricKeys.put(metricKey.getName(), metricKey);
	}

	Set<String> getMetricKeys() {
		return metricKeys.keySet();
	}

	HMetricKey getMetricKey(String metricName) {
		Assertion.checkArgNotEmpty(metricName);
		//---------------------------------------------------------------------
		final HMetricKey metricKey = metricKeys.get(metricName);
		//---------------------------------------------------------------------
		Assertion.checkNotNull(metricKey, "metricKey '{0}' is not registred", metricName);
		return metricKey;
	}

}
