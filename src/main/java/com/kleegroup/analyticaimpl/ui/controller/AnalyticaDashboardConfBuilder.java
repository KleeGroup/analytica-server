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
package com.kleegroup.analyticaimpl.ui.controller;

import java.util.List;

import kasper.config.ConfigManager;
import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup
 * @version $Id: AnalyticaDashboardConfBuilder.java,v 1.1 2013/01/25 10:53:37 npiedeloup Exp $
 */
public final class AnalyticaDashboardConfBuilder implements Builder<AnalyticaDashboardConf> {
	private final ConfigManager configManager;
	private final String analyticaContext;

	private final String dashboardName;

	/**
	 * Constructeur.
	 * @param analyticaContext context de la config
	 * @param dashboardName Nom du dashboard
	 * @param configManager Manager de config
	 */
	public AnalyticaDashboardConfBuilder(final String analyticaContext, final String dashboardName, final ConfigManager configManager) {
		Assertion.notEmpty(analyticaContext);
		Assertion.notEmpty(dashboardName);
		Assertion.notNull(configManager);
		//---------------------------------------------------------------------
		this.analyticaContext = analyticaContext;
		this.dashboardName = dashboardName;
		this.configManager = configManager;
	}

	/** {@inheritDoc} */
	public AnalyticaDashboardConf build() {
		final String panelContext = analyticaContext + "." + dashboardName;
		final String dashboardTitle = configManager.getStringValue(panelContext, "dashboardTitle");
		final String dashboardIcon = configManager.getStringValue(panelContext, "dashboardIcon");
		final List<String> dashboardPanels = java.util.Arrays.asList(configManager.getStringValue(panelContext, "panels").split(";"));
		return new AnalyticaDashboardConf(dashboardName, dashboardTitle, dashboardIcon, dashboardPanels);

	}
}
