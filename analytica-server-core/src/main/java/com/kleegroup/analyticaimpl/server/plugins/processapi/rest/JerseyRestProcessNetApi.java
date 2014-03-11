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
package com.kleegroup.analyticaimpl.server.plugins.processapi.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import vertigo.kernel.Home;
import vertigo.kernel.di.injector.Injector;

import com.google.gson.Gson;
import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.server.ServerManager;

/**
 * Api REST avec jersey de dépot des process.
 * @author npiedeloup
 * @version $Id: JerseyRestNetApi.java,v 1.3 2012/10/16 12:39:27 npiedeloup Exp $
 */
@Path("/process")
public final class JerseyRestProcessNetApi {
	private static final Logger LOG = Logger.getLogger(JerseyRestProcessNetApi.class);

	@Inject
	private ServerManager serverManager;

	/**
	 * Constructeur simple pour instanciation par jersey.
	 */
	public JerseyRestProcessNetApi() {
		new Injector().injectMembers(this, Home.getComponentSpace());
	}

	/**
	 * @param json Liste de process au format Json
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public void push(final String json) {
		final KProcess[] processes = new Gson().fromJson(json, KProcess[].class);
		LOG.info("PUSH " + processes.length + " processes.");
		//LOG.info("-> " + json);
		for (final KProcess process : processes) {
			serverManager.push(process);
		}
	}

	/**
	 * @return Version de l'api
	 */
	@GET
	@Path("/version")
	@Produces(MediaType.TEXT_PLAIN)
	public String getVersion() {
		return "1.0.0";
	}

	/**
	 * On n'utilise pas APPLICATION_JSON, car jackson est moins simple à mettre en place que Gson.
	 * @POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void add(final KProcess[] processes) {
		for (final KProcess process : processes) {
			serverManager.add(process);
		}
	}*/
}
