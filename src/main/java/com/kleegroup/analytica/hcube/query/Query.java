package com.kleegroup.analytica.hcube.query;

import java.util.List;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.TimePosition;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;

/**
 * Requête permettant de définir les zones de sélections sur les différents axes du cube.
 * Cette requête doit être construite avec QueryBuilder.
 * @author npiedeloup, pchretien
 */
public final class Query {
	private final TimeSelection timeSelection;
	private final WhatPosition whatPosition;

	Query(TimeSelection timeSelection, WhatPosition whatPosition) {
		Assertion.notNull(timeSelection);
		Assertion.notNull(whatPosition);
		//---------------------------------------------------------------------
		this.timeSelection = timeSelection;
		this.whatPosition = whatPosition;
	}

	//-----------------------What----------------------------------------------
	public WhatPosition getWhatPosition() {
		return whatPosition;
	}

	//-----------------------When----------------------------------------------
	public List<TimePosition> getAllTimePositions() {
		return timeSelection.getAllTimePositions();
	}

	public String toString() {
		return ("query : {" + timeSelection + " with:" + getWhatPosition() + "}");
	}
}
