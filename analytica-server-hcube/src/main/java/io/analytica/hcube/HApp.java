package io.analytica.hcube;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.result.HResult;

import java.util.Set;

public interface HApp {
	String getName();

	HSelector getSelector();

	/**
	 * Ajout d'un cube.
	 * @param cube HCube à ajouter 
	 */
	void push(HKey key, HCube cube);

	long size();

	HResult execute(final HQuery query);

	//-----------
	void register(HMetricKey metricKey);

	Set<String> getMetricKeys();

	HMetricKey getMetricKey(String name);
}
