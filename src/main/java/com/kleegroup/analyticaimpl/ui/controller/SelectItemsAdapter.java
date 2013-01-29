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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectItemsAdapter<O extends Object> {

	private final List<SelectItemAdapter<O>> listAdapter;
	private List<String> selected;
	private final Map<String, O> index = new HashMap<String, O>();
	private final Map<O, String> reverseIndex = new HashMap<O, String>();

	public SelectItemsAdapter(final List<O> list) {
		this.listAdapter = new ArrayList<SelectItemAdapter<O>>(list.size());
		selected = new ArrayList<String>();
		long i = 0;
		for (final O object : list) {
			final String id = String.valueOf(i++);
			listAdapter.add(new SelectItemAdapter<O>(id, object));
			index.put(id, object);
			reverseIndex.put(object, id);
		}
	}

	public List<SelectItemAdapter<O>> getList() {
		return listAdapter;
	}

	public List<String> getSelected() {
		return selected;
	}

	public void setSelected(final List<String> selected) {
		this.selected = selected;
	}

	public List<O> getSelectedObject() {
		final List<O> selectedObject = new ArrayList<O>(selected.size());
		for (final String id : selected) {
			selectedObject.add(index.get(id));
		}
		return selectedObject;
	}

	public void setSelectedObjects(final List<O> selectedObjects) {
		selected.clear();
		for (final O object : selectedObjects) {
			selected.add(reverseIndex.get(object));
		}
	}

}
