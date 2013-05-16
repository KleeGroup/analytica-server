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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTime;

/**
 * @author npiedeloup
 * @version $Id: AnalyticaContext.java,v 1.1 2012/03/15 18:05:42 npiedeloup Exp $
 */
@SessionScoped()
@ManagedBean(name = "analyticaContext")
public final class AnalyticaContext implements Serializable {
	private static final long serialVersionUID = 855858989288016205L;

	private boolean initialize = false;
	private boolean aggregateTime = true;
	private boolean aggregateWhat = true;
	//private final List<HTimeSelection> timeSelectionQueue = new LinkedList<HTimeSelection>();
	private final List<HTime> timeSelectionQueue = new LinkedList<HTime>();
	//	private final List<WhatSelection> whatSelectionQueue = new LinkedList<WhatSelection>();
	private final List<HCategory> whatSelectionQueue = new LinkedList<HCategory>();

	// ?==DataKey
	private SelectItemsAdapter<?> dataKeysAdapter;
	//	private List<DataKey> dataKeys;
	//	private List<DataKey> selectedDataKeys;
	//
	//	private List<String> metrics;
	//	private List<String> selectedMetrics;
	//	private List<String> metadatas;
	//	private List<String> selectedMetadatas;
	//private List<Data> datas;
	private List<?> datas;

	private List<Map<String, ?>> whatLineDatas;

	private List<Map<Date, ?>> timeLineDatas;

	public final boolean isInitialize() {
		return initialize;
	}

	public final void initialized() {
		initialize = true;
	}

	public final boolean isAggregateTime() {
		return aggregateTime;
	}

	public final void setAggregateTime(final boolean aggregateTime) {
		this.aggregateTime = aggregateTime;
	}

	public final boolean isAggregateWhat() {
		return aggregateWhat;
	}

	public final void setAggregateWhat(final boolean aggregateWhat) {
		this.aggregateWhat = aggregateWhat;
	}

	public HTime getSuperTimeSelection() {
		if (timeSelectionQueue.size() > 1) {
			return timeSelectionQueue.get(1);
		}
		return null;
	}

	public final HTime getTimeSelection() {
		return timeSelectionQueue.get(0);
	}

	public final void setTimeSelection(final HTime timeSelection) {
		Assertion.notNull(timeSelection);
		//---------------------------------------------------------------------
		final int indexOf = timeSelectionQueue.indexOf(timeSelection);
		if (indexOf == -1) {
			timeSelectionQueue.add(0, timeSelection);
		} else {
			for (int i = 0; i < indexOf; i++) {
				timeSelectionQueue.remove(0);
			}
		}
	}

	public HCategory getSuperWhatSelection() {
		if (whatSelectionQueue.size() > 1) {
			return whatSelectionQueue.get(1);
		}
		return null;
	}

	public final HCategory getWhatSelection() {
		return whatSelectionQueue.get(0);
	}

	public final void setWhatSelection(final HCategory whatSelection) {
		Assertion.notNull(whatSelection);
		//---------------------------------------------------------------------
		final int indexOf = whatSelectionQueue.indexOf(whatSelection);
		if (indexOf == -1) {
			whatSelectionQueue.add(0, whatSelection);
		} else {
			for (int i = 0; i < indexOf; i++) {
				whatSelectionQueue.remove(0);
			}
		}
	}

	public SelectItemsAdapter<?> getDataKeysAdapter() {
		return dataKeysAdapter;
	}
	// ?==DataKey
	public final void setDataKeysAdapter(final SelectItemsAdapter<?> dataKeysAdapter) {
		Assertion.notNull(dataKeysAdapter);
		//---------------------------------------------------------------------
		this.dataKeysAdapter = dataKeysAdapter;
	}

	//	public List<DataKey> getDataKeys() {
	//		return dataKeys;
	//	}
	//
	//	public final void setDataKeys(final List<DataKey> dataKeys) {
	//		Assertion.notNull(dataKeys);
	//		//---------------------------------------------------------------------
	//		this.dataKeys = new SelectedItemsAdapter<DataKey>(dataKeys);
	//	}
	//
	//	public List<DataKey> getSelectedDataKeys() {
	//		return dataKeys;
	//	}
	//
	//	public final void setSelectedDataKeys(final List<DataKey> dataKeys) {
	//		Assertion.notNull(dataKeys);
	//		//---------------------------------------------------------------------
	//		this.dataKeys = dataKeys;
	//	}
	//
	//	public final List<String> getMetrics() {
	//		return metrics;
	//	}
	//
	//	public final void setMetrics(final List<String> metrics) {
	//		Assertion.notNull(metrics);
	//		//---------------------------------------------------------------------
	//		this.metrics = metrics;
	//	}
	//
	//	public final List<String> getSelectedMetrics() {
	//		return selectedMetrics;
	//	}
	//
	//	public final void setSelectedMetrics(final List<String> selectedMetrics) {
	//		Assertion.notNull(selectedMetrics);
	//		//---------------------------------------------------------------------
	//		this.selectedMetrics = selectedMetrics;
	//	}

	public final List<?> getDatas() {
		return datas;
	}

	public final void setDatas(final List<?> datas) {
		Assertion.notNull(datas);
		//---------------------------------------------------------------------
		this.datas = datas;
	}

	public void resetDatas() {
		datas = null;
		whatLineDatas = null;
		timeLineDatas = null;
	}

	public final List<Map<String, ?>> getWhatLineDatas() {
		return whatLineDatas;
	}

	public final List<Map<Date, ?>> getTimeLineDatas() {
		return timeLineDatas;
	}

	public void setWhatLineDatas(final List<Map<String, ?>> whatLineDatas) {
		this.whatLineDatas = whatLineDatas;
	}

	public void setTimeLineDatas(final List<Map<Date, ?>> timeLineDatas) {
		this.timeLineDatas = timeLineDatas;
	}

}
