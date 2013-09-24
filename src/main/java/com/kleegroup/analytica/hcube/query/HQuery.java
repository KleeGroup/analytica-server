package com.kleegroup.analytica.hcube.query;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.HCategoryDictionary;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTime;

/**
 * Requête permettant de définir les zones de sélections sur les différents axes du cube.
 * Cette requête doit être construite avec QueryBuilder.
 * Cette Requête est descriptive, elle peut fonctionner avec des dates absolues ou relatives via la notion NOW. 
 * @author npiedeloup, pchretien
 */
public final class HQuery {
	private final HTimeSelection timeSelection;
	private final HCategorySelection categorySelection;

	HQuery(HTimeSelection timeSelection, HCategorySelection categorySelection) {
		Assertion.notNull(timeSelection);
		Assertion.notNull(categorySelection);
		//---------------------------------------------------------------------
		this.timeSelection = timeSelection;
		this.categorySelection = categorySelection;
	}

	//-----------------------What----------------------------------------------
	/**
	 * Liste triée par ordre alphabétique des catégories matchant la sélection
	 * @return
	 */
	public Set<HCategory> getAllCategories(final HCategoryDictionary categoryDictionary) {
		Assertion.notNull(categoryDictionary);
		// ---------------------------------------------------------------------
		if (categorySelection.hasChildren()) {
			return categoryDictionary.getAllSubCategories(categorySelection.getCategory());
		} else {
			return Collections.singleton(categorySelection.getCategory());
		}

	}

	//-----------------------When----------------------------------------------
	public List<HTime> getAllTimes() {
		return timeSelection.getAllTimes();
	}

	/** {@inheritDoc} */
	public String toString() {
		return ("query : {" + timeSelection + " with:" + categorySelection + "}");
	}
}
