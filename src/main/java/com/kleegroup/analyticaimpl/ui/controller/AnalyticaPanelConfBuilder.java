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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kasper.config.ConfigManager;
import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.server.data.DataKey;
import com.kleegroup.analytica.server.data.DataType;
import com.kleegroup.analytica.server.data.TimeDimension;
import com.kleegroup.analytica.server.data.TimeSelection;
import com.kleegroup.analytica.server.data.WhatDimension;
import com.kleegroup.analytica.server.data.WhatSelection;

/**
 * @author npiedeloup
 * @version $Id: AnalyticaPanelConfBuilder.java,v 1.9 2013/01/25 10:53:37 npiedeloup Exp $
 */
public final class AnalyticaPanelConfBuilder implements Builder<AnalyticaPanelConf> {
	private final ConfigManager configManager;
	private final String dashboardContext;

	private final String panelName;

	/**
	 * Constructeur.
	 * @param dashboardContext context du dashboard de la config
	 * @param panelName Nom du panel
	 * @param configManager Manager de config
	 */
	public AnalyticaPanelConfBuilder(final String dashboardContext, final String panelName, final ConfigManager configManager) {
		Assertion.notEmpty(dashboardContext);
		Assertion.notEmpty(panelName);
		Assertion.notNull(configManager);
		//---------------------------------------------------------------------
		this.dashboardContext = dashboardContext;
		this.panelName = panelName;
		this.configManager = configManager;
	}

	/** {@inheritDoc} */
	public AnalyticaPanelConf build() {
		final String panelContext = dashboardContext + "." + panelName;
		final TimeSelection panelTimeSelection = readTimeSelection(panelContext);
		final WhatSelection panelWhatSelection = readWhatSelection(panelContext);
		final List<DataKey> panelDataKeys = readDataKeyList(panelContext);
		final List<String> panelLabels = java.util.Arrays.asList(configManager.getStringValue(panelContext, "labels").split(";"));

		final boolean aggregateTime;
		final boolean aggregateWhat;
		final String dataLoadType = configManager.getStringValue(panelContext, "loadType");
		if (dataLoadType.equals("data")) {
			aggregateTime = true;
			aggregateWhat = true;
		} else if (dataLoadType.equals("whatLine")) {
			aggregateTime = true;
			aggregateWhat = false;
		} else if (dataLoadType.equals("timeLine")) {
			aggregateTime = false;
			aggregateWhat = true;
		} else {
			throw new IllegalArgumentException("Le type de chargement de données '" + dataLoadType + "' n'est pas reconnu. Types possible : data, whatLine, timeLine.");
		}
		final String panelTitle = configManager.getStringValue(panelContext, "title");
		final String panelIcon = configManager.getStringValue(panelContext, "icon");
		final String panelRenderer = configManager.getStringValue(panelContext, "renderer");
		final String panelSize = configManager.getStringValue(panelContext, "size");
		final String colors = configManager.getStringValue(panelContext, "colors");
		final int panelWidth = Integer.parseInt(panelSize.split("x")[0]);
		final int panelHeight = Integer.parseInt(panelSize.split("x")[1]);
		return new AnalyticaPanelConf(panelName, panelTimeSelection, panelWhatSelection, panelDataKeys, panelLabels, aggregateTime, aggregateWhat, panelTitle, panelIcon, panelRenderer, colors, panelWidth, panelHeight);

	}

	private List<DataKey> readDataKeyList(final String panelContext) {
		final List<DataKey> dataKeys = new ArrayList<DataKey>(); //attention l'ordre est important
		final String dataList = configManager.getStringValue(panelContext, "dataList");
		for (final String dataKeyStr : dataList.split(";")) {
			final int typeIndexOf = dataKeyStr.indexOf(':');
			Assertion.precondition(typeIndexOf > 0, "Le nom de la dataKey {0} est incorrect. Doit être : <MEASURE_NAME>:<DataType> avec <DataType>: MetaData, Count, Mean, Max, Min, StandardDeviation");
			final String dataKeyName = dataKeyStr.substring(0, typeIndexOf);
			final DataType dataKeyType = DataType.valueOf(dataKeyStr.substring(typeIndexOf + 1));
			final DataKey dataKey = new DataKey(dataKeyName, dataKeyType);
			dataKeys.add(dataKey);
		}
		return dataKeys;
	}

	private WhatSelection readWhatSelection(final String confContext) {
		final String whatDim = configManager.getStringValue(confContext, "whatDim");
		final String whatList = configManager.getStringValue(confContext, "whatList");

		final WhatDimension dimension = WhatDimension.valueOf(whatDim);
		return new WhatSelection(dimension, whatList.split(";"));
	}

	private TimeSelection readTimeSelection(final String confContext) {
		final String timeDim = configManager.getStringValue(confContext, "timeDim");
		final String timeFrom = configManager.getStringValue(confContext, "timeFrom");
		final String timeTo = configManager.getStringValue(confContext, "timeTo");

		final TimeDimension dimension = TimeDimension.valueOf(timeDim);
		final Date minValue = readDate(timeFrom, dimension);
		final Date maxValue = readDate(timeTo, dimension);
		return new TimeSelection(minValue, maxValue, dimension);
	}

	private Date readDate(final String timeStr, final TimeDimension dimension) {
		if (timeStr.equals("NOW")) {
			return new Date();
		} else if (timeStr.startsWith("NOW-")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW-".length()));
			return new Date(System.currentTimeMillis() - deltaMs);
		} else if (timeStr.startsWith("NOW+")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW+".length()));
			return new Date(System.currentTimeMillis() + deltaMs);
		}
		final String datePattern;
		switch (dimension) {
			case Year:
				datePattern = "yyyy";
				break;
			case Month:
				datePattern = "MM/yyyy";
				break;
			case Day:
				datePattern = "dd/MM/yyyy";
				break;
			case Hour:
			case Minute:
			default:
				datePattern = "HH:mm dd/MM/yyyy";
		}
		final SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		try {
			return sdf.parse(timeStr);
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			throw new KRuntimeException("Erreur de format de date (" + timeStr + "). Format attendu :" + sdf.toPattern());
		}
	}

	private long readDeltaAsMs(final String deltaAsString) {
		final Long delta;
		char unit = deltaAsString.charAt(deltaAsString.length() - 1);
		if (unit >= '0' && unit <= '9') {
			unit = 'd';
			delta = Long.valueOf(deltaAsString);
		} else {
			delta = Long.valueOf(deltaAsString.substring(0, deltaAsString.length() - 1));
		}
		switch (unit) {
			case 'd':
				return delta * 24 * 60 * 60 * 1000L;
			case 'h':
				return delta * 60 * 60 * 1000L;
			case 'm':
				return delta * 60 * 1000L;
			default:
				throw new KRuntimeException("La durée doit préciser l'unité de temps utilisée : d=jour, h=heure, m=minute");
		}
	}

}
