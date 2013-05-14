/**
 * 
 */
package com.kleegroup.analytica.hcube;

import java.util.Set;

import com.kleegroup.analytica.hcube.dimension.HCategory;

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
	 */
	void add(HCategory category);

	/**
	 * @return Set des cat�gories racines
	 */
	Set<HCategory> getAllRootCategories();

	/**
	 * @return Liste des cat�gories filles
	 */
	Set<HCategory> getAllCategories(HCategory category);
}
