package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HCategorySelection;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class AppCategoryStore {
	private final Set<HCategory> categories;

	//	private final Map<HCategory, Set<HCategory>> categories;

	//	private final String appName;

	AppCategoryStore(final String appName) {
		Assertion.checkArgNotEmpty(appName);
		//---------------------------------------------------------------------
		//	this.appName = appName;
		categories = new HashSet<>();
	}

	void addCategory(HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		HCategory currentCategory = category;
		boolean drillUp;
		do {
			//Optim :Si la catégorie existe déjà alors sa partie gauche aussi !!
			//On dispose donc d'une info pour savoir si il faut remonter 
			drillUp = doPut(currentCategory);
			currentCategory = currentCategory.drillUp();
		} while (drillUp && currentCategory != null);
	}

	private boolean doPut(HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		return categories.add(category);
	}

	Set<HCategory> findCategories(HCategorySelection categorySelection) {
		Assertion.checkNotNull(categorySelection);
		//---------------------------------------------------------------------
		Set<HCategory> set = new HashSet<>();
		for (HCategory category : categories) {
			if (categorySelection.matches(category)) {
				set.add(category);
			}
		}
		System.out.println(">>>>findCat>> " + set);
		return Collections.unmodifiableSet(set);
	}
}
