package com.kleegroup.analytica.hcube.query;

import java.util.List;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.HCategoryPosition;
import com.kleegroup.analytica.hcube.dimension.HTimePosition;

/**
 * Requête permettant de définir les zones de sélections sur les différents axes du cube.
 * Cette requête doit être construite avec QueryBuilder.
 * @author npiedeloup, pchretien
 */
public final class HQuery {
	private final HTimeSelection timeSelection;
	private final HCategorySelection categorySelection;

	HQuery(HTimeSelection timeSelection, HCategorySelection  categorySelection) {
		Assertion.notNull(timeSelection);
		Assertion.notNull(categorySelection);
		//---------------------------------------------------------------------
		this.timeSelection = timeSelection;
		this.categorySelection = categorySelection;
	}

	//-----------------------What----------------------------------------------
	public List<HCategoryPosition> getAllCategoryPositions(){
		return categorySelection.getAllCategoryPositions();
	}
	
	
	//-----------------------When----------------------------------------------
	public List<HTimePosition> getAllTimePositions() {
		return timeSelection.getAllTimePositions();
	}
	
	
	public String toString() {
		return ("query : {" + timeSelection + " with:" + categorySelection + "}");
	}
}
