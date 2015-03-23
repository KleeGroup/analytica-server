package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HLocation;
import io.analytica.hcube.dimension.HTime;

import java.util.List;

public interface HSelector {
	/**
	 * Liste des catégories matchant la sélection
	 */
	List<HCategory> findCategories(final HCategorySelection categorySelection);

	List<HTime> findTimes(HTimeSelection timeSelection);

	List<HLocation> findLocations(HLocationSelection locationSelection);
}
