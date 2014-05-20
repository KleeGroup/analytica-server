package io.analytica.hcube;

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HCategorySelection;

import java.util.Set;

public interface HSelector {
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
