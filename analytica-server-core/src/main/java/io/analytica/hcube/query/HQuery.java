package io.analytica.hcube.query;

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HTime;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Requête permettant de définir les zones de sélections sur les différents axes du cube.
 * Cette requête doit être construite avec QueryBuilder.
 * Cette Requête est descriptive, elle peut fonctionner avec des dates absolues ou relatives via la notion NOW. 
 * @author npiedeloup, pchretien
 */
public final class HQuery {
	private final HTimeSelection timeSelection;
	private final HCategorySelection categorySelection;

	HQuery(final HTimeSelection timeSelection, final HCategorySelection categorySelection) {
		Assertion.checkNotNull(timeSelection);
		Assertion.checkNotNull(categorySelection);
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
		Assertion.checkNotNull(categoryDictionary);
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
	@Override
	public String toString() {
		return "query : {" + timeSelection + " with:" + categorySelection + "}";
	}
}
