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

import com.kleegroup.analytica.server.data.DataKey;
import com.kleegroup.analytica.server.data.TimeSelection;
import com.kleegroup.analytica.server.data.WhatSelection;

/**
 * @author npiedeloup
 * @version $Id: AnalyticaPanelConf.java,v 1.7 2013/01/25 10:53:37 npiedeloup Exp $
 */
public final class AnalyticaPanelConf {
	private final boolean aggregateTime;
	private final boolean aggregateWhat;
	private final TimeSelection timeSelection;
	private final WhatSelection whatSelection;
	private final List<DataKey> dataKeys;
	private final List<String> labels;
	private final String colors;
	private final String panelName;
	private final String panelTitle;
	private final String panelIcon;
	private final String panelRenderer;
	private final int panelWidth;
	private final int panelHeight;

	/**
	 * Constructeur.
	 * @param timeSelection Selection temporelle
	 * @param whatSelection Selection fonctionnelle
	 * @param dataKeys Liste des indicateurs
	 * @param aggregateTime Si aggregation temporelle
	 * @param aggregateWhat Si aggregation fonctionnelle
	 */
	public AnalyticaPanelConf(final String panelName, final TimeSelection timeSelection, final WhatSelection whatSelection, final List<DataKey> dataKeys, final List<String> labels, final boolean aggregateTime, final boolean aggregateWhat, final String panelTitle, final String panelIcon, final String panelRenderer, final String colors, final int panelWidth, final int panelHeight) {
		Assertion.notEmpty(panelName);
		Assertion.notNull(timeSelection);
		Assertion.notNull(whatSelection);
		Assertion.notNull(dataKeys);
		Assertion.notNull(labels);
		Assertion.notEmpty(panelRenderer);
		Assertion.notEmpty(panelTitle);
		Assertion.notEmpty(panelIcon);
		Assertion.notEmpty(colors);
		Assertion.precondition(dataKeys.size() == labels.size(), "Le nombre de labels ({1}) ne correspond pas aux indicateurs sélectionnées ({0}).", dataKeys.size(), labels.size());
		//---------------------------------------------------------------------
		this.panelName = panelName;
		this.aggregateTime = aggregateTime;
		this.aggregateWhat = aggregateWhat;
		this.timeSelection = timeSelection;
		this.whatSelection = whatSelection;
		this.dataKeys = dataKeys;
		this.labels = labels;
		this.panelTitle = panelTitle;
		this.panelIcon = panelIcon;
		this.panelRenderer = panelRenderer;
		this.colors = colors;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
	}

	/** {@inheritDoc} */
	public final boolean isAggregateTime() {
		return aggregateTime;
	}

	/** {@inheritDoc} */
	public final boolean isAggregateWhat() {
		return aggregateWhat;
	}

	/** {@inheritDoc} */
	public final TimeSelection getTimeSelection() {
		return timeSelection;
	}

	/** {@inheritDoc} */
	public final WhatSelection getWhatSelection() {
		return whatSelection;
	}

	/** {@inheritDoc} */
	public List<DataKey> getDataKeys() {
		return dataKeys;
	}

	/** {@inheritDoc} */
	public List<String> getLabels() {
		return labels;
	}

	/** {@inheritDoc} */
	public final String getPanelName() {
		return panelName;
	}

	/** {@inheritDoc} */
	public final String getPanelTitle() {
		return panelTitle;
	}

	/** {@inheritDoc} */
	public final String getPanelIcon() {
		return panelIcon;
	}

	/** {@inheritDoc} */
	public final String getPanelRenderer() {
		return panelRenderer;
	}

	/** {@inheritDoc} */
	public final int getPanelWidth() {
		return panelWidth;
	}

	/** {@inheritDoc} */
	public final int getPanelHeight() {
		return panelHeight;
	}

	/** {@inheritDoc} */
	public String getColors() {
		return colors;
	}
}
