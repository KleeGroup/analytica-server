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

import kasper.kernel.util.Assertion;

/**
 * Configuration du dashboard.
 * @author npiedeloup
 * @version $Id: AnalyticaDashboardConf.java,v 1.1 2013/01/25 10:53:37 npiedeloup Exp $
 */
public final class AnalyticaDashboardConf {
	private final String dashboardName;
	private final String dashboardTitle;
	private final String dashboardIcon;
	private final List<String> panels;

	/**
	 * Constructeur.
	 * @param dashboardName Nom du dashboard
	 * @param dashboardTitle Titre dashboard
	 * @param dashboardIcon Icon dashboard
	 * @param panels Liste des panels
	 */
	public AnalyticaDashboardConf(final String dashboardName, final String dashboardTitle, final String dashboardIcon, final List<String> panels) {
		Assertion.notEmpty(dashboardName);
		Assertion.notEmpty(dashboardTitle);
		Assertion.notNull(dashboardIcon);
		Assertion.notNull(panels);
		//---------------------------------------------------------------------
		this.dashboardName = dashboardName;
		this.dashboardTitle = dashboardTitle;
		this.dashboardIcon = dashboardIcon;
		this.panels = panels;
	}

	/** {@inheritDoc} */
	public List<String> getPanels() {
		return panels;
	}

	/** {@inheritDoc} */
	public final String getDashboardName() {
		return dashboardName;
	}

	/** {@inheritDoc} */
	public final String getDashboardTitle() {
		return dashboardTitle;
	}

	/** {@inheritDoc} */
	public final String getDashboardIcon() {
		return dashboardIcon;
	}
}
