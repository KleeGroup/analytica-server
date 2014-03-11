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
package com.kleegroup.analyticaimpl.server;

import io.vertigo.kernel.component.Plugin;

/**
 * Plugin gérant l'api reseau de reception des process.
 * Plugin proposant une API pour la méthode PUSH du ServerManager.
 * @author npiedeloup
 * @version $Id: ProcessNetApiPlugin.java,v 1.1 2012/05/11 17:12:17 npiedeloup Exp $
 */
public interface ProcessNetApiPlugin extends Plugin {

	// Wrappe cette méthode :  
	// /**
	//	 * Ajout d'un process.
	//	 * @param process Process à ajouter 
	//	 */
	//	void push(KProcess process);
}
