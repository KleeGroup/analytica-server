package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HTime;

import java.util.ArrayList;
import java.util.List;

public final class HQueryUtil {

	public static List<HTime> getAllTimes(HTime minTime, HTime maxTime) {
		final List<HTime> times = new ArrayList<>();
		//On prépare les bornes de temps
		int loops = 0;
		HTime currentTime = minTime;
		do {
			times.add(currentTime);
			//---------------
			currentTime = currentTime.next();
			loops++;
			if (loops > 1000) {
				throw new RuntimeException("time range is too large : more than 1000 positions");
			}
		} while (currentTime.inMillis() < maxTime.inMillis());

		return times;
	}
}
