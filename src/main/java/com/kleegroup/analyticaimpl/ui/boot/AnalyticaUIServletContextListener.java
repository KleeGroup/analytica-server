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
package com.kleegroup.analyticaimpl.ui.boot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import kasper.kernel.Home;
import kasper.kernel.configurator.HomeConfig;
import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.PropertiesUtil;
import kasperimpl.ui.plugins.servlet.ServletResourceResolverPlugin;

import org.apache.log4j.Logger;

/**
 * @author prahmoune, npiedeloup
 * @version $Id: AnalyticaUIServletContextListener.java,v 1.1 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class AnalyticaUIServletContextListener implements ServletContextListener {
	private static final Logger LOG = Logger.getLogger(AnalyticaUIServletContextListener.class.getName());

	private static final String EXTERNAL_PROPERTIES_PARAM_NAME = "external-properties";

	/** {@inheritDoc} */
	public final void contextInitialized(final ServletContextEvent servletContextEvent) {
		final long start = System.currentTimeMillis();
		try {
			//Cette propriété est necessaire pour autocomplete. Sinon JSF fixe seul les Long a 0. 
			System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");

			// Initialisation du web context de l'application (porteur des singletons applicatifs)
			final ServletContext servletContext = servletContextEvent.getServletContext();
			ServletResourceResolverPlugin.setServletContext(servletContext);
			// Création de l'état de l'application
			// Lecture des paramètres de configuration
			final Properties conf = createProperties(servletContext);

			// Initialisation de l'état de l'application
			final HomeConfig homeConfig = PropertiesUtil.parse(conf, false);
			Home.start(homeConfig);

			//Home.getManager(WebManager.class).initServletContext(servletContext);

			LOG.info(">>>> START AnalyticaUIServlet");
		} catch (final Throwable t) {
			LOG.error(t.getMessage(), t);
			//			e.printStackTrace();
			throw new KRuntimeException("Problème d'initialisation de l'application", t);
		} finally {
			if (LOG.isInfoEnabled()) {
				LOG.info("Temps d'initialisation du listener " + (System.currentTimeMillis() - start));
			}
		}
	}

	/**
	 * Création des propriétés à partir des différents fichiers de configuration. - Web XML - Fichier externe défini par
	 * la valeur de la propriété système : external-properties
	 * 
	 * @return Properties
	 */
	private static Properties createProperties(final ServletContext servletContext) {
		// ======================================================================
		// ===Conversion en Properties du fichier de paramétrage de la servlet===
		// ======================================================================
		final Properties servletParams = new Properties();
		String name;

		/*
		 * On récupère les paramètres du context (web.xml ou fichier tomcat par exemple) Ces paramètres peuvent
		 * surcharger les paramètres de la servlet de façon à créer un paramétrage adhoc de développement par exemple.
		 */
		for (final Enumeration<String> enumeration = servletContext.getInitParameterNames(); enumeration.hasMoreElements();) {
			name = enumeration.nextElement();
			servletParams.put(name, servletContext.getInitParameter(name));
		}

		/*
		 * On récupère les paramètres du fichier de configuration externe (-Dexternal-properties). Ces paramètres
		 * peuvent surcharger les paramètres de la servlet de façon à créer un paramétrage adhoc de développement par
		 * exemple.
		 */
		final String externalPropertiesFileName = System.getProperty(EXTERNAL_PROPERTIES_PARAM_NAME);
		try {
			readFile(servletParams, externalPropertiesFileName);
		} catch (final IOException e) {
			throw new KRuntimeException("Erreur lors de la lecture du fichier", e);
		}

		return servletParams;
	}

	private static void readFile(final Properties servletParams, final String externalPropertiesFileName) throws IOException {
		if (externalPropertiesFileName != null) {
			final InputStream inputStream = new FileInputStream(externalPropertiesFileName);
			try {
				servletParams.load(inputStream);
			} finally {
				inputStream.close();
			}
		}
	}

	/** {@inheritDoc} */
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		try {
			Home.stop();
			LOG.info("<<<< STOP AnalyticaUIServlet");
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
