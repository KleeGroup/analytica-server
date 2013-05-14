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
	private final Set<HCategory> rootCategoryPositions;
	private final Map<HCategory, Set<HCategory>> categoryPositions;

	HCategoryDictionaryImpl() {
		rootCategoryPositions = new HashSet<HCategory>();
		categoryPositions = new HashMap<HCategory, Set<HCategory>>();
	}

	/** {@inheritDoc} */
	public synchronized Set<HCategory> getAllRootCategories() {
		return Collections.unmodifiableSet(rootCategoryPositions);

	}

	/** {@inheritDoc} */
	public synchronized Set<HCategory> getAllCategories(HCategory categoryPosition) {
		Assertion.notNull(categoryPosition);
		//---------------------------------------------------------------------
		Set<HCategory> set = categoryPositions.get(categoryPosition);
		return set == null ? Collections.<HCategory> emptySet() : Collections.unmodifiableSet(set);
	}

	/** {@inheritDoc} */
	public synchronized void add(HCategory categoryPosition) {
		Assertion.notNull(categoryPosition);
		//---------------------------------------------------------------------
		HCategory currentCategoryPosition = categoryPosition;
		HCategory parentCategoryPosition;
		while (currentCategoryPosition != null) {
			parentCategoryPosition = currentCategoryPosition.drillUp();
			doPut(parentCategoryPosition, currentCategoryPosition);
			currentCategoryPosition = parentCategoryPosition;
		}
	}

	private void doPut(HCategory parentCategoryPosition, HCategory categoryPosition) {
		Assertion.notNull(categoryPosition);
		//---------------------------------------------------------------------
		if (parentCategoryPosition == null) {
			//categoryPosition est une catégorie racine
			rootCategoryPositions.add(categoryPosition);
		} else {
			//categoryPosition n'est pas une catégorie racine
			Set<HCategory> set = categoryPositions.get(parentCategoryPosition);
			if (set == null) {
				set = new HashSet<HCategory>();
				categoryPositions.put(parentCategoryPosition, set);
			}
			set.add(categoryPosition);
		}
	}

}
