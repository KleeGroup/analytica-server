package com.kleegroup.analytica.hcube.query;

import kasper.kernel.util.Assertion;

public final class Query {
	private final TimeSelection timeSelection;
	private final WhatSelection whatSelection;

	public Query(TimeSelection timeSelection, WhatSelection whatSelection) {
		Assertion.notNull(timeSelection);
		Assertion.notNull(whatSelection);
		//---------------------------------------------------------------------
		this.timeSelection = timeSelection;
		this.whatSelection = whatSelection;
	}

	public TimeSelection getTimeSelection() {
		return timeSelection;
	}

	public WhatSelection getWhatSelection() {
		return whatSelection;
	}

}
