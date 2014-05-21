package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HCategory;

import java.util.Set;

public interface HCategorySelector {
	/**
	 * @return Set des cat�gories racines
	 */
	Set<HCategory> findAllRootCategories(String appName);

	/**
	 * @return Liste des cat�gories filles
	 */
	Set<HCategory> findAllSubCategories(String appName, HCategory category);

	/**
	 * Liste des cat�gories matchant la s�lection
	 */
	Set<HCategory> findCategories(final String appName, final HCategorySelection categorySelection);
}
