package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HTime;

import java.util.List;
import java.util.Set;

public interface HSelector {
	/**
	 * Liste des catégories matchant la sélection
	 */
	Set<List<HCategory>> findCategories(final HCategorySelection categorySelection);

	List<HTime> findTimes(HTimeSelection timeSelection);
}
