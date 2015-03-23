package io.analytica.hcube.impl;

import io.analytica.hcube.HApp;
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
			public List<HCategory> findCategories(final HCategorySelection categorySelection) {
				return cubeStore.findCategories(appName, categorySelection);
			}

			@Override
			public List<HLocation> findLocations(final HLocationSelection locationSelection) {
				return cubeStore.findLocations(appName, locationSelection);
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public HSelector getSelector() {
		return selector;
	}

	/** {@inheritDoc} */
	@Override
	public HResult execute(final String type, final HQuery query) {
		return new HResult(query, selector.findCategories(query.getCategorySelection()), cubeStore.execute(appName, type, query, selector));
	}

	/** {@inheritDoc} */
	@Override
	public void push(final String type, final HKey key, final HCube cube) {
		cubeStore.push(appName, type, key, cube);
	}

	/** {@inheritDoc} */
	@Override
	public long size(final String type) {
		return cubeStore.size(appName, type);
	}

	/** {@inheritDoc} */
	@Override
	public String getAppName() {
		return appName;
	}
}
