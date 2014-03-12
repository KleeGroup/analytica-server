package io.analytica.hcube.query;

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HTime;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Requ�te permettant de d�finir les zones de s�lections sur les diff�rents axes du cube.
 * Cette requ�te doit �tre construite avec QueryBuilder.
 * Cette Requ�te est descriptive, elle peut fonctionner avec des dates absolues ou relatives via la notion NOW. 
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
	 * Liste tri�e par ordre alphab�tique des cat�gories matchant la s�lection
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
