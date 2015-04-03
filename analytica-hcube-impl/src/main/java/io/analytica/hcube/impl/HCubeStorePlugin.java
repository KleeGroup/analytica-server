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

import io.analytica.hcube.HCubeStoreException;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HLocation;
import io.analytica.hcube.query.HCategorySelection;
import io.analytica.hcube.query.HLocationSelection;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.result.HSerie;
import io.vertigo.lang.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Plugin gérant le stockage des cubes.
 * Ce plugin met a plat toutes les fonctions.
 * @author npiedeloup
 */
public interface HCubeStorePlugin extends Plugin {
	static final String DELIMITER = ".";

	Set<String> getAppNames();

	List<HCategory> findCategories(String appName, final HCategorySelection categorySelection) throws HCubeStoreException;

	List<HLocation> findLocations(String appName, final HLocationSelection locationSelection) throws HCubeStoreException;

	/**
	 * Ajout d'un cube.
	 * @param cube HCube à ajouter
	 *
	 */
	void push(String appName, HKey key, HCube cube, String processKey) throws HCubeStoreException;

	/**
	 * Methode permetant d'inserer un ensemble des HCubes dans Lucene. 
	 * */
	void pushBulk(String appName, Map<HKey, HCube> data, String lasProcessKey) throws HCubeStoreException;

	/**
	 * Recuperation de la clé du dernier cube inseré. Attention!! Cette clé ne correspond pas à HKey mais
	 * a la clée donnée par le ProcessStorePlugin.
	 * */
	String getLastCubeKey(String appName) throws HCubeStoreException;

	/**
	 * Execute une requête et fournit en retour un cube virtuel, constitué d'une liste de cubes.
	 * @param query Paramètres de la requete
	 * @return cube virtuel, constitué d'une liste de cubes
	 * @throws HCubeStoreException 
	 */
	List<HSerie> execute(String appName, final String type, final HQuery query, final HSelector selector) throws HCubeStoreException;

	long size(String appName, final String type) throws HCubeStoreException;

}
