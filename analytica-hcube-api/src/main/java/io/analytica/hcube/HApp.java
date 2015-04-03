package io.analytica.hcube;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.result.HResult;

import java.util.Map;

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
	void push(HKey key, HCube cube, String processKey) throws HCubeStoreException;

	void pushBulk(Map<HKey, HCube> data, String lasProcessKey) throws HCubeStoreException;

	long size(String type) throws HCubeStoreException;

	HResult execute(final String type, final HQuery query) throws HCubeStoreException;

	String getLastReceivedHCubeId() throws HCubeStoreException;
}
