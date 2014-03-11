package com.kleegroup.analyticaimpl.ui;

import io.vertigo.kernel.di.configurator.ComponentSpaceConfigBuilder;
import io.vertigo.kernel.di.configurator.ModuleConfigBuilder;
import io.vertigo.kernel.lang.Assertion;

import java.util.Properties;

/**
 * Charge et d�marre un environnement.
 * @author pchretien, npiedeloup
 */
public class Starter extends com.kleegroup.analyticaimpl.Starter {

	/**
	 * @param propertiesFileName Fichier de propri�t�s
	 * @param relativeRootClass Racine du chemin relatif, le cas ech�ant
	 */
	public Starter(final String propertiesFileName, final Class<?> relativeRootClass) {
		super(propertiesFileName, relativeRootClass);
	}

	/**
	 * Lance l'environnement et attend ind�finiment.
	 * @param args "Usage: java kasper.kernel.Starter managers.xml <conf.properties>"
	 */
	public static void main(final String[] args) {
		final String usageMsg = "Usage: java com.kleegroup.analytica.Starter <conf.properties>";
		Assertion.checkArgument(args.length == 1, usageMsg + " ( conf attendue : " + args.length + ")");
		Assertion.checkArgument(args[0].endsWith(".properties"), usageMsg + " ( .properties attendu : " + args[0] + ")");
		//---------------------------------------------------------------------
		final String propertiesFileName = args[0];
		final Starter starter = new Starter(propertiesFileName, Starter.class);
		starter.run();
	}

	/**
	 * Ajoute d'autre modules � la configuration de l'environnement.
	 * @param properties Propri�t�s de l'environnement.
	 * @param componentSpaceConfigBuilder Builder de la configuration de l'environnement
	 */
	@Override
	protected void appendOtherModules(final Properties properties, final ComponentSpaceConfigBuilder componentSpaceConfigBuilder) {
		super.appendOtherModules(properties, componentSpaceConfigBuilder);
		//---------------------------------------------------------------------
		final ModuleConfigBuilder moduleConfigBuilder = componentSpaceConfigBuilder.beginModule("analytica-ui");
		moduleConfigBuilder.beginComponent(AnalyticaUiManager.class, AnalyticaUiManagerImpl.class) //
				.endComponent();
		moduleConfigBuilder.endModule();
	}
}
