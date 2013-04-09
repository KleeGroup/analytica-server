package com.kleegroup.analytica.hcube.query;

import java.util.List;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.cube.DataKey;

public final class Query {
	private final TimeSelection timeSelection;
	private final WhatSelection whatSelection;
	private final List<DataKey> keys;

	public Query(TimeSelection timeSelection, WhatSelection whatSelection, List<DataKey> keys) {
		Assertion.notNull(timeSelection);
		Assertion.notNull(whatSelection);
		Assertion.notNull(keys);
		//---------------------------------------------------------------------
		this.timeSelection = timeSelection;
		this.whatSelection = whatSelection;
		this.keys = keys;
	}

	public TimeSelection getTimeSelection() {
		return timeSelection;
	}

	public WhatSelection getWhatSelection() {
		return whatSelection;
	}

	public List<DataKey> getKeys() {
		return keys;
	}
}
