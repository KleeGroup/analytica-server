package io.analytica.hcube.impl;

import io.analytica.hcube.HApp;
import io.analytica.hcube.HCubeStoreException;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HLocation;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.query.HCategorySelection;
import io.analytica.hcube.query.HLocationSelection;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.query.HTimeSelection;
import io.analytica.hcube.result.HResult;
import io.vertigo.lang.Assertion;

import java.util.List;
import java.util.Map;

final class HAppImp implements HApp {
	private final HCubeStorePlugin cubeStore;
	private final String appName;
	private final HSelector selector;

	HAppImp(final HCubeStorePlugin cubeStore, final String appName) {
		Assertion.checkNotNull(cubeStore);
		Assertion.checkArgNotEmpty(appName);
		//---------------------------------------------------------------------
		this.cubeStore = cubeStore;
		this.appName = appName;
		selector = new HSelector() {
			private final HTimeSelector timeSelector = new HTimeSelector();

			@Override
			public List<HTime> findTimes(final HTimeSelection timeSelection) {
				return timeSelector.findTimes(timeSelection);
			}

			@Override
			public List<HCategory> findCategories(final HCategorySelection categorySelection) throws HCubeStoreException {
				return cubeStore.findCategories(appName, categorySelection);
			}

			@Override
			public List<HLocation> findLocations(final HLocationSelection locationSelection) throws HCubeStoreException {
				return cubeStore.findLocations(appName, locationSelection);
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public HSelector getSelector() {
		return selector;
	}

	/** {@inheritDoc} 
	 * @throws HCubeStoreException */
	@Override
	public HResult execute(final String type, final HQuery query) throws HCubeStoreException {
		return new HResult(query, selector.findCategories(query.getCategorySelection()), cubeStore.execute(appName, type, query, selector));
	}

	/** {@inheritDoc} 
	 * @throws HCubeStoreException */
	@Override
	public void push(final HKey key, final HCube cube, String processKey) throws HCubeStoreException {
		cubeStore.push(appName, key, cube, processKey);
	}

	/** {@inheritDoc} 
	 * @throws HCubeStoreException */
	@Override
	public long size(final String type) throws HCubeStoreException {
		return cubeStore.size(appName, type);
	}

	/** {@inheritDoc} */
	@Override
	public String getAppName() {
		return appName;
	}

	/** {@inheritDoc} 
	 * @throws HCubeStoreException */
	@Override
	public void pushBulk(Map<HKey, HCube> data, String lasProcessKey) throws HCubeStoreException {
		cubeStore.pushBulk(appName, data, lasProcessKey);
	}

	/** {@inheritDoc} 
	 * @throws HCubeStoreException */
	@Override
	public String getLastReceivedHCubeId() throws HCubeStoreException {
		return cubeStore.getLastCubeKey(appName);
	}
}
