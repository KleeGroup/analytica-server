package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HCategorySelection;
import io.vertigo.core.lang.Assertion;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class AppCategoryStore {
	private final Set<HCategory>[] categories;
	private final int dimensions = 1;

	AppCategoryStore(final String appName) {
		//---------------------------------------------------------------------
		//	this.appName = appName;
		categories = new Set[dimensions];
		for (int i = 0; i < dimensions; i++) {
			categories[i] = new HashSet<>();
		}
	}

	void addCategories(final HCategory[] addedCategories) {
		Assertion.checkNotNull(addedCategories);
		Assertion.checkArgument(addedCategories.length == dimensions, "count of categories must be {0} instead of {1}", dimensions, addedCategories.length);
		//---------------------------------------------------------------------
		for (int i = 0; i < dimensions; i++) {
			HCategory currentCategory = addedCategories[i];
			boolean go;
			do {
				//Optim :Si la catégorie existe déjà alors sa partie gauche aussi et on s'arrete !!
				//On dispose donc d'une info pour savoir si il faut remonter
				go = doPut(i, currentCategory);
				currentCategory = currentCategory.drillUp();
			} while (go && currentCategory != null);
		}
	}

	private boolean doPut(final int i, final HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		return categories[i].add(category);
	}

	/**
	 * Liste des Catégories
	 * @param categorySelection
	 * @return
	 */
	Set<List<HCategory>> findCategories(final HCategorySelection categorySelection) {
		Assertion.checkNotNull(categorySelection);
		//---------------------------------------------------------------------
		final Set<List<HCategory>> set = new HashSet<>();
		for (final HCategory category : categories[0]) {
			if (categorySelection.matches(category)) {
				set.add(Collections.singletonList(category));
			}
		}
		//		System.out.println(">>>>findCat>> " + set);
		return Collections.unmodifiableSet(set);
	}
}
