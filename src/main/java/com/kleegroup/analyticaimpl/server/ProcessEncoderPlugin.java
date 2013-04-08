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

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.hcube.cube.Cube;

/**
 * Transformation d'un process en cubes.
 * Un process est �cras�, chaque sous process cr�e un cube 
 * et remonte certaines donn�es dans le cube parent. 
 *
 * @author npiedeloup, pchretien
 * @version $Id: ProcessEncoderPlugin.java,v 1.3 2012/10/16 13:45:02 pchretien Exp $
 */
public interface ProcessEncoderPlugin extends Plugin {

	/**
	 * Transforme un KProcess et ses sous process en cubes.
	 * @param process Process � convertir
	 * @return Liste des Cubes associ�s
	 */
	List<Cube> encode(final KProcess process);
}
