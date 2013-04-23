package com.kleegroup.analytica.hcube.query;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.WhatPosition;

/**
 * Requ�te permettant de d�finir les zones de s�lections sur les diff�rents axes du cube.
 * Cette requ�te doit �tre construite avec QueryBuilder.
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
	public TimeSelection getTimeSelection() {
		return timeSelection;
	}

	public String toString() {
		return ("query : {" + timeSelection + " with:" + getWhatPosition() + "}");
	}
}
