/**
 * 
 */
package com.kleegroup.analytica.hcube.dimension;

import java.util.List;


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
	 * @return Liste des cat�gories racines
	 */
	List<HCategoryPosition> getAllRootCategories();

	/**
	 * @return Liste des cat�gories filles
	 */
	List<HCategoryPosition> getAllCategories(HCategoryPosition categoryPosition);
}
