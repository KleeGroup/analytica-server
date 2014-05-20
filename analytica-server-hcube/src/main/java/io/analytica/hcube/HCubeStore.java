package io.analytica.hcube;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCubeKey;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HSerie;

import java.util.List;

/**
 * Plugin gérant le stockage des cubes.
 * @author npiedeloup
 */
public interface HCubeStore {
	HSelector getSelector();

	/**
	 * Ajout d'un cube.
	 * @param cube HCube à ajouter 
	 * 
	 */
	void push(String appName, HCubeKey cubeKey, HCube cube);

	/**
	 * Execute une requête et fournit en retour un cube virtuel, constitué d'une liste de cubes.  
	 * @param query Paramètres de la requete
	 * @return cube virtuel, constitué d'une liste de cubes
	 */
	List<HSerie> execute(String appName, final HQuery query, final HTimeSelector timeSelector);

	long count(String appName);
}
