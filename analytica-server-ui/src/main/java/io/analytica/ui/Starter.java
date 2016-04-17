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

import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.app.config.ModuleConfigBuilder;
import io.vertigo.lang.Assertion;

import java.util.Properties;

/**
 * Charge et démarre un environnement.
 * @author pchretien, npiedeloup
 */
public class Starter extends io.analytica.server.Starter {

	/**
	 * @param propertiesFileName Fichier de propriétés
	 * @param relativeRootClass Racine du chemin relatif, le cas echéant
	 */
	public Starter(final String propertiesFileName, final Class<?> relativeRootClass) {
		super(propertiesFileName, relativeRootClass);
	}

	/**
	 * Lance l'environnement et attend indéfiniment.
	 * @param args "Usage: java kasper.kernel.Starter managers.xml <conf.properties>"
	 */
	public static void main(final String[] args) {
		final String usageMsg = "Usage: java io.analytica.ui.Starter <conf.properties>";
		Assertion.checkArgument(args.length == 1, usageMsg + " ( conf attendue : " + args.length + ")");
		Assertion.checkArgument(args[0].endsWith(".properties"), usageMsg + " ( .properties attendu : " + args[0] + ")");
		//---------------------------------------------------------------------
		final String propertiesFileName = args[0];
		final Starter starter = new Starter(propertiesFileName, Starter.class);
		starter.run();
	}

	/**
	 * Ajoute d'autre modules é la configuration de l'environnement.
	 * @param properties Propriétés de l'environnement.
	 * @param componentSpaceConfigBuilder Builder de la configuration de l'environnement
	 */
	@Override
	protected void appendOtherModules(final Properties properties, final AppConfigBuilder appConfigBuilder) {
		super.appendOtherModules(properties, appConfigBuilder);
		//---------------------------------------------------------------------
		final ModuleConfigBuilder moduleConfigBuilder = appConfigBuilder.beginModule("analytica-ui");
		moduleConfigBuilder.beginComponent(AnalyticaUiManager.class, AnalyticaUiManagerImpl.class) //
				.endComponent();
		moduleConfigBuilder.endModule();
	}
}
