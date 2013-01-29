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
package com.kleegroup.analyticaimpl.server.plugins.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import kasper.kernel.Home;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.server.ServerManager;
import com.kleegroup.analyticaimpl.server.NetApiPlugin;

/**
 * Plugin gérant l'api reseau en REST avec jersey.
 * @author npiedeloup
 * @version $Id: RestNetApiPlugin.java,v 1.3 2012/10/16 12:39:27 npiedeloup Exp $
 */
@Path("/process")
public final class RestNetApiPlugin implements NetApiPlugin {
	private final ServerManager serverManager;
	private static final Logger LOG = Logger.getLogger(RestNetApiPlugin.class);

	/**
	 * Constructeur simple pour instanciation par jersey.
	 */
	public RestNetApiPlugin() {
		serverManager = getServerManager();
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void push(final String json) {
		final KProcess[] processes = new Gson().fromJson(json, KProcess[].class);
		LOG.info("PUSH " + processes.length + " processes.");
		for (final KProcess process : processes) {
			serverManager.push(process);
		}
	}

	@GET
	@Path("/version")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVersion() {
		return "1.0.0";
	}

	/*
	 * On n'utilise pas APPLICATION_JSON, car jackson est moins simple à mettre en place que Gson.
	 * @POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void add(final KProcess[] processes) {
		for (final KProcess process : processes) {
			serverManager.add(process);
		}
	}*/

	private static ServerManager getServerManager() {
		return Home.getContainer().getManager(ServerManager.class);
	}
}
