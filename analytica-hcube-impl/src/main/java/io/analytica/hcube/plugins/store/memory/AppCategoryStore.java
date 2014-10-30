package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HCategorySelection;
import io.vertigo.lang.Assertion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class AppCategoryStore {
	private final Set<HCategory> categories = new HashSet<>();

	//	private final int dimensions = 1;

	AppCategoryStore(final String appName) {
		//---------------------------------------------------------------------
		//	this.appName = appName;
	}

	void addCategory(final HCategory addedCategory) {
		Assertion.checkNotNull(addedCategory);
		//---------------------------------------------------------------------
		for (HCategory currentCategory = addedCategory; currentCategory != null; currentCategory = currentCategory.drillUp()) {
			categories.add(currentCategory);
		}
	}

	/**
	 * Liste des Catégories
	 */
	List<HCategory> findCategories(final HCategorySelection categorySelection) {
		Assertion.checkNotNull(categorySelection);
		//---------------------------------------------------------------------
		final List<HCategory> matchingCategories = new ArrayList<>();
		for (final HCategory category : categories) {
			if (categorySelection.matches(category)) {
				matchingCategories.add(category);
			}
		}
		//		System.out.println(">>>>findCat>> " + set);
		return Collections.unmodifiableList(matchingCategories);
	}
}
