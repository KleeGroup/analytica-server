package com.kleegroup.analytica.museum;

import java.util.Random;

import vertigo.kernel.lang.Assertion;

final class StatsUtil {
	private static final Random RANDOM = new Random();

	private StatsUtil() {
		//
	}

	/**
	 * Calcul la prochaine valeur aléatoire gaussienne entre X +/- 20% + X*(coef-1).
	 * 
	 * @param value Valeur moyenne
	 * @param coef Coefficient
	 * @return prochaine valeur aléatoire suivant une gaussienne
	 */
	static long random(final double value, final double coef) {
		return Math.round(nextGaussian(value, Math.round(value * 1.20)) + coef * value);
	}

	private static long nextGaussian(final double avg, final double maxValue) {
		Assertion.checkArgument(avg >= 1, "La moyenne doit être supérieure ou égale à 1");
		Assertion.checkArgument(maxValue > avg, "La valeur max doit être supérieure à la moyenne");
		//---------------------------------------------------------------------
		long result = Math.round(RANDOM.nextGaussian() * maxValue / 5d + avg);
		if (result < 0 || result > maxValue) {
			result = nextGaussian(avg, maxValue);
		}
		return result;
	}
}
