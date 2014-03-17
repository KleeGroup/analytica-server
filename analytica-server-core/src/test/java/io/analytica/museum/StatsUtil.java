/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>
 */
package io.analytica.museum;

import io.vertigo.kernel.lang.Assertion;

import java.util.Random;

public final class StatsUtil {
	private static final Random RANDOM = new Random();

	private StatsUtil() {
		//
	}

	static int[] randoms(final double coef, final int... base) {
		final int[] result = new int[base.length];
		for (int i = 0; i < base.length; i++) {
			result[i] = (int) StatsUtil.random(base[i], coef);
		}
		return result;
	}

	static int sum(final int[] values, final int... indice) {
		int result = 0;
		for (int i = 0; i < indice.length; i++) {
			result += values[indice[i]];
		}
		return result;
	}

	/**
	 * Calcul la prochaine valeur aléatoire gaussienne entre (X +/- 20%) * coef.
	 * 
	 * @param value Valeur moyenne
	 * @param coef Coefficient
	 * @return prochaine valeur aléatoire suivant une gaussienne
	 */
	public static long random(final double value, final double coef) {
		final long result = Math.round(nextGaussian(value) * coef);
		return result > 0 ? result : 0;
	}

	/**
	 * Récupère une valeur aléatoire parmi une liste de valeur.
	 * Applique un poid au premier et dernier élément, les éléments intermédiaire on un poids déduis linéairement.
	 * 
	 * @param weightFirst poids de la première valeur
	 * @param weightLast poids de la dernière valeur
	 * @param values liste des valeurs
	 * @return prochaine valeur aléatoire suivant une gaussienne
	 */
	public static long randomValue(final double weightFirst, final double weightLast, final long... values) {
		double weightSum = 0;
		final double[] weightValues = new double[values.length];
		for (int i = 0; i < weightValues.length; i++) {
			weightValues[i] = weightFirst + i * (weightLast - weightFirst) / (weightValues.length - 1);
			weightSum += weightValues[i];
		}
		final double rand = RANDOM.nextDouble() * weightSum;
		double seuil = 0;
		for (int i = 0; i < weightValues.length; i++) {
			seuil += weightValues[i];
			if (rand <= seuil) {
				return values[i];
			}
		}
		return values[values.length - 1];
	}

	private static long nextGaussian(final double avg) {
		Assertion.checkArgument(avg >= 1, "La moyenne doit être supérieure ou égale à 1");
		//---------------------------------------------------------------------
		final long result = Math.round(RANDOM.nextGaussian() * avg / 8d + avg);
		/*if (result < 0 || result > maxValue) {
			result = nextGaussian(avg, maxValue);
		}*/
		return result;
	}
}
