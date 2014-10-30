package io.analytica.hcube;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.result.HResult;

/**
 * An app is defined by its name.
 *
 * @author pchretien, npiedeloup
 */
public interface HApp {
	String getAppName();

	HSelector getSelector();

	/**
	 * push a cube into the database.
	 * if key exists then the cube is merged else the cube is added.
	 * @param cube HCube to add or merge
	 */
	void push(String type, HKey key, HCube cube);

	long size(String type);

	HResult execute(final String type, final HQuery query);
}
