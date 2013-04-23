package com.kleegroup.analytica.hcube.query;

import java.util.List;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.HTimePosition;
import com.kleegroup.analytica.hcube.dimension.HCategoryPosition;

/**
 * Requ�te permettant de d�finir les zones de s�lections sur les diff�rents axes du cube.
 * Cette requ�te doit �tre construite avec QueryBuilder.
 * @author npiedeloup, pchretien
 */
public final class HQuery {
	private final HTimeSelection timeSelection;
	private final HCategoryPosition categoryPosition;

	HQuery(HTimeSelection timeSelection, HCategoryPosition categoryPosition) {
		Assertion.notNull(timeSelection);
		Assertion.notNull(categoryPosition);
		//---------------------------------------------------------------------
		this.timeSelection = timeSelection;
		this.categoryPosition = categoryPosition;
	}

	//-----------------------What----------------------------------------------
	public HCategoryPosition getCategoryPosition() {
		return categoryPosition;
	}

	//-----------------------When----------------------------------------------
	public List<HTimePosition> getAllTimePositions() {
		return timeSelection.getAllTimePositions();
	}

	public String toString() {
		return ("query : {" + timeSelection + " with:" + getCategoryPosition() + "}");
	}
}
