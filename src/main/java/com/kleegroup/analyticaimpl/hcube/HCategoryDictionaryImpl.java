/**
 * 
 */
package com.kleegroup.analyticaimpl.hcube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.HCategoryDictionary;
import com.kleegroup.analytica.hcube.dimension.HCategoryPosition;

/**
 * @author statchum
 */
final class HCategoryDictionaryImpl implements HCategoryDictionary {
	private final Set<HCategoryPosition> rootCategoryPositions;
	private final Map<HCategoryPosition,Set<HCategoryPosition>> categoryPositions;
	
	
	HCategoryDictionaryImpl() {
		rootCategoryPositions = new HashSet<HCategoryPosition>();
		categoryPositions = new HashMap<HCategoryPosition,Set<HCategoryPosition>>();
	}

	/** {@inheritDoc} */
	public synchronized  Set<HCategoryPosition> getAllRootCategories() {
		return Collections.unmodifiableSet(rootCategoryPositions);
		
	}

	/** {@inheritDoc} */
	public synchronized Set<HCategoryPosition> getAllCategories(HCategoryPosition categoryPosition) {
		Assertion.notNull(categoryPosition);
		//---------------------------------------------------------------------
		Set<HCategoryPosition> set = categoryPositions.get(categoryPosition);
		return set == null ? Collections.<HCategoryPosition>emptySet(): Collections.unmodifiableSet(set);
	}

	/** {@inheritDoc} */
	public synchronized void add(HCategoryPosition categoryPosition) {
		Assertion.notNull(categoryPosition);
		//---------------------------------------------------------------------
		HCategoryPosition currentCategoryPosition = categoryPosition;
		HCategoryPosition parentCategoryPosition; 
		while (currentCategoryPosition!= null){
			parentCategoryPosition = currentCategoryPosition.drillUp();
			doPut (parentCategoryPosition, currentCategoryPosition);
			currentCategoryPosition = parentCategoryPosition;
		}
	}
	
	private void doPut(HCategoryPosition parentCategoryPosition, HCategoryPosition categoryPosition) {
		Assertion.notNull(categoryPosition);
		//---------------------------------------------------------------------
		if (parentCategoryPosition == null){
			//categoryPosition est une catégorie racine
			rootCategoryPositions.add(categoryPosition);
		}else{
			//categoryPosition n'est pas une catégorie racine
			Set<HCategoryPosition> set =  categoryPositions.get(parentCategoryPosition);
			if (set == null){
				set = new HashSet<HCategoryPosition>();
				categoryPositions.put(parentCategoryPosition, set);
			}
			set.add(categoryPosition);
		}
	}

}
