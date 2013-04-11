package com.kleegroup.analytica.hcube.query;

import java.util.List;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.cube.DataKey;
import com.kleegroup.analytica.hcube.dimension.TimeDimension;
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
	private final List<DataKey> keys;

	Query(TimeSelection timeSelection, WhatPosition whatPosition, List<DataKey> keys) {
		Assertion.notNull(timeSelection);
		Assertion.notNull(whatPosition);
		Assertion.notNull(keys);
		//---------------------------------------------------------------------
		this.timeSelection = timeSelection;
		this.whatPosition = whatPosition;
		this.keys = keys;
	}

	public List<DataKey> getKeys() {
		return keys;
	}

	//-----------------------What----------------------------------------------
	public WhatPosition getWhatPosition() {
		return whatPosition;
	}

	//-----------------------When----------------------------------------------
	public TimeDimension getTimeDimension() {
		return timeSelection.getDimension();
	}

	public TimePosition getMinTimePosition() {
		return timeSelection.getMinTimePosition();
	}

	public TimePosition getMaxTimePosition() {
		return timeSelection.getMaxTimePosition();
	}

	public String toString() {
		return ("query : {from:" + getMinTimePosition() + " to:" + getMaxTimePosition() + " with:" + getWhatPosition() + "}");
	}
}
