package io.analytica.hcube;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCubeKey;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HSerie;

import java.util.List;

/**
 * Plugin g�rant le stockage des cubes.
 * @author npiedeloup
 */
public interface HCubeStore {
	HSelector getSelector();

	/**
	 * Ajout d'un cube.
	 * @param cube HCube � ajouter 
	 * 
	 */
	void push(String appName, HCubeKey cubeKey, HCube cube);

	/**
	 * Execute une requ�te et fournit en retour un cube virtuel, constitu� d'une liste de cubes.  
	 * @param query Param�tres de la requete
	 * @return cube virtuel, constitu� d'une liste de cubes
	 */
	List<HSerie> execute(String appName, final HQuery query, final HTimeSelector timeSelector);

	long count(String appName);
}
