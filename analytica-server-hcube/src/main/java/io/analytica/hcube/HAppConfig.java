package io.analytica.hcube;

import io.analytica.hcube.cube.HMetricKey;

import java.util.Set;

public interface HAppConfig {
	String getName();

	Set<String> getKeys();

	HMetricKey getKey(String name);

	Set<String> getTypes(); //ex SQL, Pages
}
