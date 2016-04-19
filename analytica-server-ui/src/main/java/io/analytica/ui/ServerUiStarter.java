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
package io.analytica.ui;

import io.analytica.restserver.impl.RestServerManagerImpl;
import io.vertigo.app.App;
import io.vertigo.core.component.di.injector.Injector;
import io.vertigo.lang.Assertion;

import org.apache.log4j.Logger;

/**
 * Charge et démarre un environnement.
 * @author pchretien, npiedeloup
 */
public class ServerUiStarter {


	/**
	 * Lance l'environnement et attend indéfiniment.
	 * @param args "Usage: java kasper.kernel.Starter managers.xml <conf.properties>"
	 */
	public static void main(final String[] args) {
		final String usageMsg = "Usage: java io.analytica.ui.Starter <conf.properties>";
		Assertion.checkArgument(args.length == 1, usageMsg + " ( conf attendue : " + args.length + ")");
		Assertion.checkArgument(args[0].endsWith(".properties"), usageMsg + " ( .properties attendu : " + args[0] + ")");
		//---------------------------------------------------------------------
		try (App app = new App(AnalyticaServerUiConfigurator.config(args[0]))) {
			
			RestServerManagerImpl test = new RestServerManagerImpl("/test/",8080);
			//	AppShell.startShell(5222);
			System.in.read();
			//Thread.sleep(1000 * 1000);
		} catch (final Exception e) {
			e.printStackTrace();
			Logger.getLogger(ServerUiStarter.class).warn("an error occured when starting", e);
		}
	}

}
