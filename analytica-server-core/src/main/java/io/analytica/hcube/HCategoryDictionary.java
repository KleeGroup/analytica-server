/**
 * 
 */
package io.analytica.hcube;

import io.analytica.hcube.dimension.HCategory;

import java.util.Set;

/**
 * Dictionnaire des catégories.
 * Permet d'accéder à 
 *  - Toutes les catégories racines (sql, pages, services..)
 *  - Liste des catégories sous une catégorie chapeau (exemple : toutes les catégories sous sql)
 * 
 * @author statchum, pchretien
 */
public interface HCategoryDictionary {
	/**
	 * Ajout d'une catégorie.
	 */
	void add(HCategory category);

	/**
	 * @return Set des catégories racines
	 */
	Set<HCategory> getAllRootCategories();

	/**
	 * @return Liste des catégories filles
	 */
	Set<HCategory> getAllSubCategories(HCategory category);
}
