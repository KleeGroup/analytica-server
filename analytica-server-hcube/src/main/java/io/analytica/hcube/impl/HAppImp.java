package io.analytica.hcube.impl;

import io.analytica.hcube.HApp;
import io.analytica.hcube.HAppConfig;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.query.HCategorySelection;
import io.analytica.hcube.query.HCategorySelector;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.query.HTimeSelector;
import io.analytica.hcube.result.HResult;
import io.vertigo.kernel.lang.Assertion;

import java.util.List;
import java.util.Set;

final class HAppImp implements HApp {
	private final HCubeStorePlugin cubeStore;
	private final HAppConfig config;
	private final HSelector selector;

	HAppImp(final HCubeStorePlugin cubeStore, final HAppConfig config) {
		Assertion.checkNotNull(cubeStore);
		Assertion.checkNotNull(config);
		//---------------------------------------------------------------------
		this.cubeStore = cubeStore;
		this.config = config;
		selector = new HSelector() {
			private HTimeSelector timeSelector = new HTimeSelectorImpl();

			public HTimeSelector getTimeSelector() {
				return timeSelector;
			}

			public HCategorySelector getCategorySelector() {
				return new HCategorySelector() {

					public Set<List<HCategory>> findCategories(HCategorySelection categorySelection) {
						return cubeStore.findCategories(config.getName(), categorySelection);
					}

				};
			}
		};
	}

	/** {@inheritDoc} */
	public HSelector getSelector() {
		return selector;
	}

	/** {@inheritDoc} */
	public HResult execute(final HQuery query) {
		return new HResult(query, selector.getCategorySelector().findCategories(query.getCategorySelection()), cubeStore.execute(config.getName(), query, selector));
	}

	/** {@inheritDoc} */
	public void push(HKey key, HCube cube) {
		cubeStore.push(config.getName(), key, cube);
	}

	/** {@inheritDoc} */
	public long size() {
		return cubeStore.size(config.getName());
	}

	public HAppConfig getConfig() {
		// TODO Auto-generated method stub
		return null;
	}
}
