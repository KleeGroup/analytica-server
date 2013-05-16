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

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import kasper.jsf.util.JSFConstants;
import kasper.jsf.util.JSFUtil;

import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTime;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;
import com.kleegroup.analytica.server.ServerManager;

/**
 * @author npiedeloup
 * @version $Id: AnalyticaController.java,v 1.3 2013/01/14 16:35:20 npiedeloup Exp $
 */
@RequestScoped
@ManagedBean(name = "analyticaController")
public final class AnalyticaController {

	private static final long DAY_TIME_MILLIS = 24 * 60 * 60 * 1000;

	@Inject
	private ServerManager serverManager;

	@ManagedProperty(value = "#{analyticaContext}")
	private AnalyticaContext analyticaContext;

	@PostConstruct
	public void init() {
		if (!analyticaContext.isInitialize()) {
			// ---------------------------------------------------------------------
			final HTime firstTimeSelection = new HTime(new Date(System.currentTimeMillis() - 366 * DAY_TIME_MILLIS), HTimeDimension.Year);
			final HCategory firstWhatSelection = new HCategory(""); //??

			analyticaContext.setTimeSelection(firstTimeSelection);
			analyticaContext.setWhatSelection(firstWhatSelection);
			final List<?> subDataKeys = serverManager.getSubDataKeys(firstTimeSelection, firstWhatSelection);
			final SelectItemsAdapter<?> dataKeysAdapter = new SelectItemsAdapter<?>(subDataKeys);
			dataKeysAdapter.setSelectedObjects(subDataKeys);
			analyticaContext.setDataKeysAdapter(dataKeysAdapter);
			analyticaContext.initialized();
		}
	}

	public List<HTime> getSubTimeSelections() {
		return serverManager.getSubTimeSelections(analyticaContext.getTimeSelection());
	}

	public List<WhatSelection> getSubWhatSelections() {
		return serverManager.getSubWhatSelections(analyticaContext.getTimeSelection(), analyticaContext.getWhatSelection());
	}

	/*public List<DataKey> getSubDataKeys() {
		return serverManager.getSubDataKeys(analyticaContext.getTimeSelection(), analyticaContext.getWhatSelection());
	}*/

	public String zoomOutTime() {
		analyticaContext.setTimeSelection(analyticaContext.getSuperTimeSelection());

		return refresh();
	}

	public String zoomInTime() {
		// @param newTimeMax date max
		final String newTimeMax = JSFUtil.getRequestParameter("timeMax");
		// @param newTimeMin date min
		final String newTimeMin = JSFUtil.getRequestParameter("timeMin");
		// @param newDimension dimension
		final String newDimension = JSFUtil.getRequestParameter("dimension");

		final TimeSelection newTimeSelection = new TimeSelection(new Date(Long.parseLong(newTimeMin)), new Date(Long.parseLong(newTimeMax)), TimeDimension.valueOf(newDimension));
		analyticaContext.setTimeSelection(newTimeSelection);
		final List<DataKey> subDataKeys = serverManager.getSubDataKeys(analyticaContext.getTimeSelection(), analyticaContext.getWhatSelection());
		final SelectItemsAdapter<DataKey> dataKeysAdapter = new SelectItemsAdapter<DataKey>(subDataKeys);
		dataKeysAdapter.setSelectedObjects(subDataKeys);

		return refresh();
	}

	public String zoomOutWhat() {
		analyticaContext.setWhatSelection(analyticaContext.getSuperWhatSelection());

		return refresh();
	}

	public String zoomInWhat() {
		final String newWhatValue = JSFUtil.getRequestParameter("whatSelection");
		// @param newDimension dimension
		final String newDimension = JSFUtil.getRequestParameter("dimension");

		final WhatSelection newWhatSelection = new WhatSelection(WhatDimension.valueOf(newDimension), newWhatValue);
		analyticaContext.setWhatSelection(newWhatSelection);
		final List<?> subDataKeys = serverManager.getSubDataKeys(analyticaContext.getTimeSelection(), analyticaContext.getWhatSelection());
		final SelectItemsAdapter<?> dataKeysAdapter = new SelectItemsAdapter<?>(subDataKeys);
		dataKeysAdapter.setSelectedObjects(subDataKeys);
		analyticaContext.setDataKeysAdapter(dataKeysAdapter);

		return refresh();
	}

	public String refresh() {
		analyticaContext.resetDatas();
		if (!analyticaContext.isAggregateTime() && !analyticaContext.isAggregateWhat()) {
			final List<Data> datas = serverManager.getData(analyticaContext.getTimeSelection(), analyticaContext.getWhatSelection(), analyticaContext.getDataKeysAdapter().getSelectedObject());
			analyticaContext.setDatas(datas);
		} else if (analyticaContext.isAggregateTime()) {
			final List<DataSet<String, ?>> datas = serverManager.getDataWhatLine(analyticaContext.getTimeSelection(), analyticaContext.getWhatSelection(), analyticaContext.getDataKeysAdapter().getSelectedObject());
			analyticaContext.setWhatLineDatas(datas);
		} else {
			final List<DataSet<Date, ?>> datas = serverManager.getDataTimeLine(analyticaContext.getTimeSelection(), analyticaContext.getWhatSelection(), analyticaContext.getDataKeysAdapter().getSelectedObject());
			analyticaContext.setTimeLineDatas(datas);
		}

		return JSFConstants.SUCCESS;
	}

	public final AnalyticaContext getCtx() {
		return analyticaContext;
	}

	//=========================================================================
	//=================Getters et setters pour JSF=============================
	//=========================================================================

	public final ServerManager getServerManager() {
		return serverManager;
	}

	public final void setServerManager(final ServerManager serverManager) {
		this.serverManager = serverManager;
	}

	public final AnalyticaContext getAnalyticaContext() {
		return analyticaContext;
	}

	public final void setAnalyticaContext(final AnalyticaContext analyticaContext) {
		this.analyticaContext = analyticaContext;
	}

}
