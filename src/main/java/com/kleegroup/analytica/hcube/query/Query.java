package com.kleegroup.analytica.hcube.query;

import java.util.List;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.cube.DataKey;
import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.dimension.TimePosition;
import com.kleegroup.analytica.hcube.dimension.WhatDimension;

/**
 * Requ�te permettant de d�finir les zones de s�lections sur les diff�rents axes du cube.
 * Cette requ�te doit �tre construite avec QueryBuilder.
 * @author npiedeloup, pchretien
 */
public final class Query {
	private final TimeSelection timeSelection;
	private final WhatSelection whatSelection;
	private final List<DataKey> keys;

	Query(TimeSelection timeSelection, WhatSelection whatSelection, List<DataKey> keys) {
		Assertion.notNull(timeSelection);
		Assertion.notNull(whatSelection);
		Assertion.notNull(keys);
		//---------------------------------------------------------------------
		this.timeSelection = timeSelection;
		this.whatSelection = whatSelection;
		this.keys = keys;
	}

	public List<DataKey> getKeys() {
		return keys;
	}

	//-----------------------What----------------------------------------------
	public List<String> getWhatValues() {
		return whatSelection.getWhatValues();
	}

	public WhatDimension getWhatDimension() {
		return whatSelection.getDimension();
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
}
