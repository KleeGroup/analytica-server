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
package io.analytica.restserver;

import io.vertigo.kernel.component.Manager;

/**
 * Manager serveur HTTP REST.
 * @author npiedeloup
 * @version $Id: QueryNetApiPlugin.java,v 1.1 2012/05/11 17:12:17 npiedeloup Exp $
 */
public interface RestServerManager extends Manager {

	/**
	 * Ajoute une classe g�rant une ressource Web (pr�fix�e par un @Path).
	 * @param handlerClass class g�rant la resource Web.
	 */
	void addResourceHandler(Class<?> handlerClass);

	/**
	 * Ajoute un classPath comme racine pour des fichiers static.
	 * L'url d'acc�s sera pr�fix� par le context. 
	 * @param classPath ClassPath racine des fichiers � proposer en static
	 * @param context Pr�fix des urls de ces �l�ments
	 */
	void addStaticPath(final String classPath, String context);

}
