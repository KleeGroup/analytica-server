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
package io.analytica.server;

import io.analytica.api.KProcess;
import io.analytica.hcube.HCubeStoreException;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HResult;
import io.vertigo.lang.Component;

/**
 * Serveur de Analytica.
 * Réception des données collectées
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public interface ServerManager extends Component {
	/**
	 * Add a process.
	 * @param process Process to push
	 */
	void push(KProcess process);

	/**
	 * Execute une requête et fournit en retour un cube virtuel, constitué d'une liste de cubes.  
	 * @param query Paramètres de la requete
	 * @return cube virtuel, constitué d'une liste de cubes
	 * @throws HCubeStoreException 
	 */
	HResult execute(final String appName, final String type, HQuery query) throws HCubeStoreException;
}
