/**
 * 
 */
package io.analytica.hcube.impl;

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.dimension.HCategory;
import io.vertigo.kernel.lang.Assertion;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author statchum 
 */
final class HCategoryDictionaryImpl implements HCategoryDictionary {
	private final Set<HCategory> rootCategories;
	private final Map<HCategory, Set<HCategory>> categories;

	HCategoryDictionaryImpl() {
		rootCategories = new HashSet<HCategory>();
		categories = new HashMap<HCategory, Set<HCategory>>();
	}

	/** {@inheritDoc} */
	public synchronized Set<HCategory> getAllRootCategories() {
		return Collections.unmodifiableSet(rootCategories);

	}

	/** {@inheritDoc} */
	public synchronized Set<HCategory> getAllSubCategories(HCategory category) {
		Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		Set<HCategory> set = categories.get(category);
		return set == null ? Collections.<HCategory> emptySet() : Collections.unmodifiableSet(set);
	}

	/** {@inheritDoc} */
	public synchronized void add(HCategory category) {
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
		} else {
			//category n'est pas une catégorie racine
			Set<HCategory> set = categories.get(parentCategory);
			if (set == null) {
				set = new HashSet<HCategory>();
				categories.put(parentCategory, set);
			}
			return set.add(category);
		}
	}
}
