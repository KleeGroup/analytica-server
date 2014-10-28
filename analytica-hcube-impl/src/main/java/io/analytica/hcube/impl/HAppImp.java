package io.analytica.hcube.impl;

import io.analytica.hcube.HApp;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.query.HCategorySelection;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.query.HTimeSelection;
import io.analytica.hcube.result.HResult;
import io.vertigo.lang.Assertion;

import java.util.List;
import java.util.Set;

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

			public List<HTime> findTimes(final HTimeSelection timeSelection) {
				return timeSelector.findTimes(timeSelection);
			}

			public Set<List<HCategory>> findCategories(final HCategorySelection categorySelection) {
				return cubeStore.findCategories(appName, categorySelection);
			}

		};
	}

	/** {@inheritDoc} */
	public HSelector getSelector() {
		return selector;
	}

	/** {@inheritDoc} */
	public HResult execute(final HQuery query) {
		return new HResult(query, selector.findCategories(query.getCategorySelection()), cubeStore.execute(appName, query, selector));
	}

	/** {@inheritDoc} */
	public void push(final HKey key, final HCube cube) {
		cubeStore.push(appName, key, cube);
	}

	/** {@inheritDoc} */
	public long size() {
		return cubeStore.size(appName);
	}

	/** {@inheritDoc} */
	public String getName() {
		return appName;
	}
}
