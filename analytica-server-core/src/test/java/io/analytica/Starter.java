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
package io.analytica;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

import io.vertigo.app.App;
import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.xml.XMLAppConfigBuilder;
import io.vertigo.lang.Assertion;

/**
 * Charge et démarre un environnement.
 * @author pchretien, npiedeloup
 */
public final class Starter implements Runnable {
	//	private static boolean SILENCE = true;
	private final Class<?> relativeRootClass;
	private final String managersXmlFileName;
	//	private final Option<String> propertiesFileName;
	private final Optional<Properties> defaultProperties;
	private final long timeToWait;
	private boolean started;
	private App app;

	/**
	 * @param managersXmlFileName Fichier managers.xml
	 * @param propertiesFileName Fichier de propriétés
	 * @param relativeRootClass Racine du chemin relatif, le cas echéant
	 * @param defaultProperties Propriétés par défaut (pouvant étre récupéré de la ligne de commande par exemple)
	 * @param timeToWait Temps d'attente, 0 signifie illimité
	 */
	public Starter(final String managersXmlFileName, final Optional<String> propertiesFileName, final Class<?> relativeRootClass, final Option<Properties> defaultProperties, final long timeToWait) {
		Assertion.checkNotNull(managersXmlFileName);
		Assertion.checkNotNull(propertiesFileName);
		Assertion.checkNotNull(defaultProperties);
		//---------------------------------------------------------------------
		this.managersXmlFileName = managersXmlFileName;
		//	this.propertiesFileName = propertiesFileName;
		this.defaultProperties = defaultProperties;
		this.timeToWait = timeToWait;
		this.relativeRootClass = relativeRootClass;

	}

	/**
	 * Lance l'environnement et attend indéfiniment.
	 * @param args "Usage: java kasper.kernel.Starter managers.xml <conf.properties>"
	 */
	public static void main(final String[] args) {
		final String usageMsg = "Usage: java " + Starter.class.getCanonicalName() + " managers.xml <conf.properties>";
		Assertion.checkArgument(args.length >= 1 && args.length <= 2, usageMsg + " (" + args.length + ")");
		Assertion.checkArgument(args[0].endsWith(".xml"), usageMsg + " (" + args[0] + ")");
		Assertion.checkArgument(args.length == 1 || args[1].endsWith(".properties"), usageMsg + " (" + (args.length == 2 ? args[1] : "vide") + ")");
		//---------------------------------------------------------------------
		final String managersXmlFileName = args[0];
		final Optional<String> propertiesFileName = args.length == 2 ? Option.<String> some(args[1]) : Option.<String> none();
		final Starter starter = new Starter(managersXmlFileName, propertiesFileName, Starter.class, Option.<Properties> none(), 0);
		starter.run();
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		try {
			start();

			final Object lock = new Object();
			synchronized (lock) {
				lock.wait(timeToWait * 1000); //on attend le temps demandé et 0 => illimité
			}
		} catch (final InterruptedException e) {
			//rien arret normal
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			stop();
		}
	}

	/**
	 * Démarre l'application.
	 */
	public final void start() {
		// Création de l'état de l'application
		// Initialisation de l'état de l'application
		final URL xmlURL = createURL(managersXmlFileName, relativeRootClass);
		final Properties properties = new Properties();
		if (defaultProperties.isDefined()) {
			properties.putAll(defaultProperties.get());
		}

		final String[] managersXml;
		if (properties.containsKey("boot.applicationConfiguration")) {
			managersXml = properties.getProperty("boot.applicationConfiguration").split(";");
			properties.remove("boot.applicationConfiguration");
		} else {
			managersXml = new String[] { managersXmlFileName };
		}
		final AppConfig appConfig = new XMLAppConfigBuilder()
				.withModules(getClass(), properties, managersXmlFileName)
				.beginBoot().silently().endBoot().build();

		app = new App(appConfig);
		started = true;
	}

	/**
	 * Stop l'application.
	 */
	public final void stop() {
		if (started) {
			app.close();
			started = false;
		}
	}

	//	/**
	//	 * Charge le fichier properties.
	//	 * Par defaut vide, mais il peut-étre surchargé.
	//	 * @param relativeRootClass Racine du chemin relatif, le cas echéant
	//	 */
	//	private static final void appendFileProperties(final Properties properties, final Option<String> propertiesFileName, final Class<?> relativeRootClass) {
	//		//---------------------------------------------------------------------
	//		if (propertiesFileName.isDefined()) {
	//			final String fileName = translateFileName(propertiesFileName.get(), relativeRootClass);
	//			try {
	//				final InputStream in = createURL(fileName, relativeRootClass).openStream();
	//				try {
	//					properties.load(in);
	//				} finally {
	//					in.close();
	//				}
	//			} catch (final IOException e) {
	//				throw new IllegalArgumentException("Impossible de charger le fichier de configuration des tests : " + fileName, e);
	//			}
	//		}
	//	}

	/**
	 * Transforme le chemin vers un fichier local au test en une URL absolue.
	 * @param fileName Path du fichier : soit en absolu (commence par /), soit en relatif é la racine
	 * @param relativeRootClass Racine du chemin relatif, le cas echéant
	 * @return URL du fichier
	 */
	private static final URL createURL(final String fileName, final Class<?> relativeRootClass) {
		Assertion.checkArgNotEmpty(fileName);
		//---------------------------------------------------------------------
		final String absoluteFileName = translateFileName(fileName, relativeRootClass);
		try {
			return new URL(absoluteFileName);
		} catch (final MalformedURLException e) {
			//Si fileName non trouvé, on recherche dans le classPath
			final URL url = relativeRootClass.getResource(absoluteFileName);
			Assertion.checkNotNull(url, "Impossible de récupérer le fichier [" + absoluteFileName + "]");
			return url;
		}
	}

	private static final String translateFileName(final String fileName, final Class<?> relativeRootClass) {
		Assertion.checkArgNotEmpty(fileName);
		//---------------------------------------------------------------------
		if (fileName.startsWith(".")) {
			//soit en relatif
			return "/" + getRelativePath(relativeRootClass) + "/" + fileName.replace("./", "");
		}

		//soit en absolu
		if (fileName.startsWith("/")) {
			return fileName;
		}
		return "/" + fileName;
	}

	private static final String getRelativePath(final Class<?> relativeRootClass) {
		return relativeRootClass.getPackage().getName().replace('.', '/');
	}
}
