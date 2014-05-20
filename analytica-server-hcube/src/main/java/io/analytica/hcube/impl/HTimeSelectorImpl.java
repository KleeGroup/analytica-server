package io.analytica.hcube.impl;

import io.analytica.hcube.HTimeSelector;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.query.HTimeSelection;
import io.vertigo.kernel.lang.Assertion;

import java.util.ArrayList;
import java.util.List;

final class HTimeSelectorImpl implements HTimeSelector {
	/** {@inheritDoc} */
	public List<HTime> findTimes(HTimeSelection timeSelection) {
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

}
