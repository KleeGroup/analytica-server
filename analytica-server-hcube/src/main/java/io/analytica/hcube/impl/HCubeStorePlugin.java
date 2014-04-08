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
package io.analytica.hcube.impl;

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.component.Plugin;

import java.util.Map;

/**
 * Plugin gérant le stockage des cubes.
 * @author npiedeloup
 */
public interface HCubeStorePlugin extends Plugin {
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
