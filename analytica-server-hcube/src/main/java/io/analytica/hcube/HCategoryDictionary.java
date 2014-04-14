/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>
 */
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
	 * @return Set des catégories racines
	 */
	Set<HCategory> getAllRootCategories(String appName);

	/**
	 * @return Liste des catégories filles
	 */
	Set<HCategory> getAllSubCategories(String appName, HCategory category);
}
