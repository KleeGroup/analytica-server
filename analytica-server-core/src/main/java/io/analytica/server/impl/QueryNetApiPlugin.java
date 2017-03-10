/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.server.impl;

import io.vertigo.lang.Plugin;

/**
 * InfluxDBProcessAggregatorPlugin gérant l'api reseau de requetage des cubes.
 * InfluxDBProcessAggregatorPlugin proposant une API pour la méthode execute(HQuery) du ServerManager.
 * @author npiedeloup
 * @version $Id: QueryNetApiPlugin.java,v 1.1 2012/05/11 17:12:17 npiedeloup Exp $
 */
public interface QueryNetApiPlugin extends Plugin {
	// Wrappe cette méthode :  
	// /**
	//	 * Execute une requéte et fournit en retour un cube virtuel, constitué d'une liste de cubes.  
	//	 * @param query Paramétres de la requete
	//	 * @return cube virtuel, constitué d'une liste de cubes
	//	 */
	//	HResult execute(HQuery query);
}
