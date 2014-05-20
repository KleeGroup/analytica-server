package io.analytica.hcube;

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HCategorySelection;

import java.util.Set;

public interface HSelector {
	/**
	 * @return Set des catégories racines
	 */
	Set<HCategory> findAllRootCategories(String appName);

	/**
	 * @return Liste des catégories filles
	 */
	Set<HCategory> findAllSubCategories(String appName, HCategory category);

	/**
	 * Liste des catégories matchant la sélection
	 */
	Set<HCategory> findCategories(final String appName, final HCategorySelection categorySelection);
}
