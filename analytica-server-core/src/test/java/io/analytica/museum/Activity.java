package io.analytica.museum;

import java.util.Calendar;

public final class Activity {

	static double getCoefPerDay(final int dayOfWeek) {
		switch (dayOfWeek) {
			case Calendar.MONDAY:
				return 0.6;
			case Calendar.TUESDAY:
				return 0.8;
			case Calendar.WEDNESDAY:
				return 1;
			case Calendar.THURSDAY:
				return 0.9;
			case Calendar.FRIDAY:
				return 0.8;
			case Calendar.SATURDAY:
				return 1.6;
			case Calendar.SUNDAY:
				return 1.4;
			default:
				return 1;
		}
	}

	static double getCoefPerHour(final int hourOfDay) {
		if (hourOfDay <= 5) {
			return 0.01;
		} else if (hourOfDay <= 7) {
			return 0.1;
		} else if (hourOfDay <= 12) {
			return 0.5 + 0.5 * ((hourOfDay - 7) / 5d);
		} else if (hourOfDay <= 16) {
			return 0.9 - 0.4 * ((hourOfDay - 12) / 4d);
		} else if (hourOfDay <= 20) {
			return 0.5 + 0.3 * ((hourOfDay - 16) / 4d);
		} else {
			return 0.7 - 0.65 * ((hourOfDay - 20) / 3d);
		}
	}
}
