package io.analytica.hcube;

import io.analytica.hcube.dimension.HCategory;

import java.util.Set;

public interface HSelector {
	/**
	 * @return Set des cat�gories racines
	 */
	Set<HCategory> getAllRootCategories(String appName);

	/**
	 * @return Liste des cat�gories filles
	 */
	Set<HCategory> getAllSubCategories(String appName, HCategory category);
}
