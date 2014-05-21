package io.analytica.hcube.plugins.store.memory;

import io.analytica.hcube.dimension.HCategory;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class AppCategoryStore {
	private final Set<HCategory> rootCategories;
	private final Map<HCategory, Set<HCategory>> categories;

	//	private final String appName;

	AppCategoryStore(final String appName) {
		Assertion.checkArgNotEmpty(appName);
		//---------------------------------------------------------------------
		//	this.appName = appName;
		rootCategories = new HashSet<>();
		categories = new HashMap<>();
	}

	void addCategory(HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		HCategory currentCategory = category;
		HCategory parentCategory;
		boolean drillUp;
		do {
			parentCategory = currentCategory.drillUp();
			//Optim :Si la catégorie existe déjà alors sa partie gauche aussi !!
			//On dispose donc d'une info pour savoir si il faut remonter 
			drillUp = doPut(parentCategory, currentCategory);
			currentCategory = parentCategory;
		} while (drillUp);
	}

	private boolean doPut(HCategory parentCategory, HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		if (parentCategory == null) {
			//category est une catégorie racine
			rootCategories.add(category);
			return false;
		}
		//category n'est pas une catégorie racine
		Set<HCategory> set = categories.get(parentCategory);
		if (set == null) {
			set = new HashSet<>();
			categories.put(parentCategory, set);
		}
		return set.add(category);
	}

	Set<HCategory> getAllSubCategories(HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		Set<HCategory> set = categories.get(category);
		return set == null ? Collections.<HCategory> emptySet() : Collections.unmodifiableSet(set);
	}

	Set<HCategory> getAllRootCategories() {
		return Collections.unmodifiableSet(rootCategories);
	}
}
