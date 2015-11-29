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
package io.analytica.server.plugins.processapi.rest;

import io.analytica.restserver.RestServerManager;
import io.analytica.server.impl.ProcessNetApiPlugin;

import javax.inject.Inject;

/**
 * InfluxDBProcessAggregatorPlugin gérant l'api reseau en REST avec jersey.
 * @author npiedeloup
 * @version $Id: RestNetApiPlugin.java,v 1.3 2012/10/16 12:39:27 npiedeloup Exp $
 */
public final class RestProcessNetApiPlugin implements ProcessNetApiPlugin {

	/**
	 * Constructeur.
	 * @param restServerManager Manager de server Rest
	 */
	@Inject
	public RestProcessNetApiPlugin(final RestServerManager restServerManager) {
		restServerManager.addResourceHandler(JerseyRestProcessNetApi.class);
	}
}
