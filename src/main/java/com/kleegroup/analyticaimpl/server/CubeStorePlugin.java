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

import com.kleegroup.analytica.server.data.DataKey;
import com.kleegroup.analytica.server.data.TimeSelection;
import com.kleegroup.analytica.server.data.WhatSelection;
import com.kleegroup.analyticaimpl.server.cube.Cube;
import com.kleegroup.analyticaimpl.server.cube.TimePosition;
import com.kleegroup.analyticaimpl.server.cube.WhatPosition;

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
	 * @param timeSelection Selection temporelle
	 * @param aggregateTime Si les donn�es sont aggr�g�s sur l'axe temporel
	 * @param whatSelection Selection fonctionnelle
	 * @param aggregateWhat Si les donn�es sont aggr�g�s sur l'axe fonctionnel
	 * @param metrics Liste des indicateurs attendus (MEASURE et META_DATA)
	 * @return Liste des cubes
	 */
	List<Cube> load(TimeSelection timeSelection, boolean aggregateTime, WhatSelection whatSelection, boolean aggregateWhat, List<DataKey> metrics);

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
	List<WhatPosition> loadSubWhatPositions(TimeSelection timeSelection, WhatSelection whatSelection);

	/**
	 * Liste des indicateurs disponibles dans une selection temporelle et fonctionnelle.
	 * @param timeSelection Selection temporelle
	 * @param whatSelection Selection fonctionelle
	 * @return Liste des indicateurs
	 */
	List<DataKey> loadDataKeys(TimeSelection timeSelection, WhatSelection whatSelection);

	/**
	 * @return Dernier id de process stock�
	 */
	String loadLastProcessIdStored();

	/**
	 * @param lastProcessIdStored Dernier id de process stock�
	 */
	void saveLastProcessIdStored(String lastProcessIdStored);
}
