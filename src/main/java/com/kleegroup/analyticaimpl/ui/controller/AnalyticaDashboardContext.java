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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup
 * @version $Id: AnalyticaDashboardContext.java,v 1.3 2013/01/25 10:53:37 npiedeloup Exp $
 */
@SessionScoped()
@ManagedBean(name = "analyticaDashboardContext")
public final class AnalyticaDashboardContext implements Serializable {
	private static final long serialVersionUID = 855858989288016205L;
	private boolean initialize;
	private final Map<String, AnalyticaDashboardConf> dashboardsMap = new LinkedHashMap<String, AnalyticaDashboardConf>();
	private final Map<String, AnalyticaPanelConf> panelsMap = new LinkedHashMap<String, AnalyticaPanelConf>();
	private String currentDashboard;

	public final boolean isInitialize() {
		return initialize;
	}

	public final void initialized() {
		initialize = true;
	}

	public void registerPanel(final String panelName, final AnalyticaPanelConf analyticaPanelConf) {
		Assertion.notEmpty(panelName);
		Assertion.notNull(analyticaPanelConf);
		Assertion.precondition(!panelsMap.containsKey(panelName), "Panel {0} déjà enregistré.", panelName);
		//---------------------------------------------------------------------
		panelsMap.put(panelName, analyticaPanelConf);
	}

	public void registerDashboard(final String dashboard, final AnalyticaDashboardConf analyticaDashboardConf) {
		Assertion.notEmpty(dashboard);
		Assertion.notNull(analyticaDashboardConf);
		Assertion.precondition(!dashboardsMap.containsKey(dashboard), "Dashboard {0} déjà enregistré.", dashboard);
		//---------------------------------------------------------------------
		dashboardsMap.put(dashboard, analyticaDashboardConf);
		if (currentDashboard == null) {
			currentDashboard = dashboard;
		}
	}

	public String getCurrentDashboard() {
		return currentDashboard;
	}

	public void setCurrentDashboard(final String currentDashboard) {
		this.currentDashboard = currentDashboard;
	}

	public List<AnalyticaDashboardConf> getDashboards() {
		return new ArrayList<AnalyticaDashboardConf>(dashboardsMap.values());
	}

	public Map<String, AnalyticaDashboardConf> getDashboard() {
		return dashboardsMap;
	}

	public Map<String, AnalyticaPanelConf> getPanel() {
		return panelsMap;
	}

}
