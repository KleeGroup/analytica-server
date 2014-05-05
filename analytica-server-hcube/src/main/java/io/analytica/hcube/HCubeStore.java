package io.analytica.hcube;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HResult;

import java.util.Set;

/**
 * Plugin gérant le stockage des cubes.
 * @author npiedeloup
 */
public interface HCubeStore {
	/**
	 * @return Set des catégories racines
	 */
	Set<HCategory> getAllRootCategories(String appName);

	/**
	 * @return Liste des catégories filles
	 */
	Set<HCategory> getAllSubCategories(String appName, HCategory category);

	//-------------------------------------------------------------------------
	/**
	 * Ajout d'un cube.
	 * @param cube HCube à ajouter 
	 * 
	 */
	void push(String appName, HCube cube);

	/**
	 * Execute une requête et fournit en retour un cube virtuel, constitué d'une liste de cubes.  
	 * @param query Paramètres de la requete
	 * @return cube virtuel, constitué d'une liste de cubes
	 */
	HResult execute(String appName, HQuery query);

	//	/**
	//	 * Liste des cubes, regroupés par série indexée par ma catégorie correspondant à une requête.
	//	 * @param query Requête
	//	 * @return Séries des cubes 
	//	 */
	//	Map<HCategory, HSerie> findAll(String appName, HQuery query);

	long count(String appName);
}
