package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HCategory;

import java.util.Set;

public interface HCategorySelector {
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
