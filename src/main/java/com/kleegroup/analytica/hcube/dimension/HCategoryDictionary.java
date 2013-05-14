/**
 * 
 */
package com.kleegroup.analytica.hcube.dimension;

import java.util.List;
import java.util.Set;


/**
 * Dictionnaire des cat�gories.
 * Permet d'acc�der � 
 *  - Toutes les cat�gories racines (sql, pages, services..)
 *  - Liste des cat�gories sous une cat�gorie chapeau (exemple : toutes les cat�gories sous sql)
 * 
 * @author statchum, pchretien
 */
public interface HCategoryDictionary {
	/**
	 * Ajout d'une cat�gorie.
	 * @param categoryPosition
	 */
	void add(HCategoryPosition categoryPosition);
	
	/**
	 * @return Set des cat�gories racines
	 */
	Set<HCategoryPosition> getAllRootCategories();

	/**
	 * @return Liste des cat�gories filles
	 */
	Set<HCategoryPosition> getAllCategories(HCategoryPosition categoryPosition);
}
