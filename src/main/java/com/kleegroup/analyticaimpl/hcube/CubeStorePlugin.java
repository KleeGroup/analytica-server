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
package com.kleegroup.analyticaimpl.hcube;

import java.util.Map;

import kasper.kernel.manager.Plugin;

import com.kleegroup.analytica.hcube.HCategoryDictionary;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.result.HSerie;

/**
 * Plugin gérant le stockage des cubes.
 * @author npiedeloup
 * @version $Id: CubeStorePlugin.java,v 1.1 2012/03/22 09:16:40 npiedeloup Exp $
 */
public interface CubeStorePlugin extends Plugin {
	/**
	 * Enregistre un cube.
	 * Celui-ci sera mergé avec les autres cubes déjà enregistrés.
	 * @param cube Cube.
	 */
	void merge(HCube cube);

	/**
	 * Liste des cubes, regroupés par série indexée par ma catégorie correspondant à une requête.
	 * @param query Requête
	 * @return Séries des cubes 
	 */
	Map<HCategory, HSerie> findAll(HQuery query, final HCategoryDictionary categoryDictionary);
}
