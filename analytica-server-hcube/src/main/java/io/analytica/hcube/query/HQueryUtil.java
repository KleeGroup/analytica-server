package io.analytica.hcube.query;

import io.analytica.hcube.HSelector;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HTime;
import io.vertigo.kernel.lang.Assertion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class HQueryUtil {

	public static List<HTime> findTimes(HTimeSelection timeSelection) {
		Assertion.checkNotNull(timeSelection);
		//---------------------------------------------------------------------
		final List<HTime> times = new ArrayList<>();
		//On prépare les bornes de temps
		int loops = 0;
		HTime currentTime = timeSelection.getMinTime();
		do {
			times.add(currentTime);
			//---------------
			currentTime = currentTime.next();
			loops++;
			if (loops > 1000) {
				throw new RuntimeException("time range is too large : more than 1000 positions");
			}
		} while (currentTime.inMillis() < timeSelection.getMaxTime().inMillis());

		return times;
	}

	/**
	 * Liste triée par ordre alphabétique des catégories matchant la sélection
	 */
	public static Set<HCategory> findCategories(final String appName, final HCategorySelection categorySelection, final HSelector selector) {
		Assertion.checkNotNull(appName);
		Assertion.checkNotNull(categorySelection);
		Assertion.checkNotNull(selector);
		// ---------------------------------------------------------------------
		if (categorySelection.hasChildren()) {
			return selector.getAllSubCategories(appName, categorySelection.getCategory());
		}
		return Collections.singleton(categorySelection.getCategory());
	}
}
