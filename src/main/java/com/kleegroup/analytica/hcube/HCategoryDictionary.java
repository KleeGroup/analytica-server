/**
 * 
 */
package com.kleegroup.analytica.hcube;

import java.util.Set;

import com.kleegroup.analytica.hcube.dimension.HCategory;

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
	Set<HCategory> getAllCategories(HCategory category);
}
