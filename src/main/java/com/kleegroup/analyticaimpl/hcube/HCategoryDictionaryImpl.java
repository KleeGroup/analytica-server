/**
 * 
 */
package com.kleegroup.analyticaimpl.hcube;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.HCategoryDictionary;
import com.kleegroup.analytica.hcube.dimension.HCategory;

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
	public synchronized Set<HCategory> getAllCategories(HCategory category) {
		Assertion.notNull(category);
		//---------------------------------------------------------------------
		Set<HCategory> set = categories.get(category);
		return set == null ? Collections.<HCategory> emptySet() : Collections.unmodifiableSet(set);
	}

	/** {@inheritDoc} */
	public synchronized void add(HCategory category) {
		Assertion.notNull(category);
		//---------------------------------------------------------------------
		HCategory currentCategory = category;
		HCategory parentCategory;
		while (currentCategory != null) {
			parentCategory = currentCategory.drillUp();
			doPut(parentCategory, currentCategory);
			currentCategory = parentCategory;
		}
	}

	private void doPut(HCategory parentCategory, HCategory category) {
		Assertion.notNull(category);
		//---------------------------------------------------------------------
		if (parentCategory == null) {
			//category est une catégorie racine
			rootCategories.add(category);
		} else {
			//category n'est pas une catégorie racine
			Set<HCategory> set = categories.get(parentCategory);
			if (set == null) {
				set = new HashSet<HCategory>();
				categories.put(parentCategory, set);
			}
			set.add(category);
		}
	}

}
