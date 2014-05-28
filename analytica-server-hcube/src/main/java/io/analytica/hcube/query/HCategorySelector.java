package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HCategory;

import java.util.List;
import java.util.Set;

public interface HCategorySelector {
	/**
	 * Liste des catégories matchant la sélection
	 */
	Set<List<HCategory>> findCategories(final String appName, final HCategorySelection categorySelection);
}
