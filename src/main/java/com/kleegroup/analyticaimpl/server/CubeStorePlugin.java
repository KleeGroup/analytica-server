/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidi�re - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package com.kleegroup.analyticaimpl.server;

import java.util.List;

import kasper.kernel.manager.Plugin;

import com.kleegroup.analytica.hcube.cube.Cube;
import com.kleegroup.analytica.hcube.cube.DataKey;
import com.kleegroup.analytica.hcube.dimension.TimePosition;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;
import com.kleegroup.analytica.hcube.query.Query;
import com.kleegroup.analytica.hcube.query.TimeSelection;

/**
 * Plugin g�rant le stockage des cubes.
 * @author npiedeloup
 * @version $Id: CubeStorePlugin.java,v 1.1 2012/03/22 09:16:40 npiedeloup Exp $
 */
public interface CubeStorePlugin extends Plugin {

	/**
	 * Enregistre un cube.
	 * Celui-ci sera merger avec les autres cubes d�j� enregistr�.
	 * @param cube Cube.
	 */
	void merge(Cube cube);

	/**
	 * Chargement des cubes.
	 * @param query Selection comprenant la liste des m�triques attendues
	 * @param aggregateTime Si les donn�es sont aggr�g�s sur l'axe temporel
	 * @param aggregateWhat Si les donn�es sont aggr�g�s sur l'axe fonctionnel
	 * @return Liste des cubes
	 */
	List<Cube> load(Query query, boolean aggregateTime, boolean aggregateWhat);

	/**
	 * Liste des selections temporelles de dimension inf�rieurs sous la s�l�ction pass�e en param�tre.
	 * @param timeSelection  Selection temporelle
	 * @return Liste des selections temporelles
	 */
	List<TimePosition> loadSubTimePositions(TimeSelection timeSelection);

	/**
	 * Liste des selections fonctionelles de dimension inf�rieurs sous la s�l�ction pass�e en param�tre dans une selection temporelle donn�e.
	 * @param timeSelection Selection temporelle
	 * @param whatSelection Selection fonctionelle
	 * @return Liste des selections fonctionelles
	 */
	List<WhatPosition> loadSubWhatPositions(Query query);

	/**
	 * Liste des indicateurs disponibles dans une selection temporelle et fonctionnelle.
	 * @param query Selection 
	 * @return Liste des indicateurs
	 */
	List<DataKey> loadDataKeys(Query query);

	/**
	 * @return Dernier id de process stock�
	 */
	String loadLastProcessIdStored();

	/**
	 * @param lastProcessIdStored Dernier id de process stock�
	 */
	void saveLastProcessIdStored(String lastProcessIdStored);
}
