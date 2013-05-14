/**
 * 
 */
package com.kleegroup.analytica.hcube.dimension;

import java.util.List;
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
	 * @param categoryPosition
	 */
	void add(HCategoryPosition categoryPosition);
	
	/**
	 * @return Set des catégories racines
	 */
	Set<HCategoryPosition> getAllRootCategories();

	/**
	 * @return Liste des catégories filles
	 */
	Set<HCategoryPosition> getAllCategories(HCategoryPosition categoryPosition);
}
