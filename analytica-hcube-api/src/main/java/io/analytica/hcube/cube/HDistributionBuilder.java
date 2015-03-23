package io.analytica.hcube.cube;

import io.vertigo.lang.Builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Création d'une distribution d'un ensemble de valeurs.
 * @author npiedeloup, pchretien
 */
public final class HDistributionBuilder implements Builder<HDistribution> {
	private final Map<Double, Long> data;

	HDistributionBuilder() {
		data = new HashMap<>();
	}

	void withValue(final double value) {
		incTreshold(getMaxRange(value), 1);
	}

	void withDistribution(final HDistribution distribution) {
		for (final Entry<Double, Long> entry : distribution.getData().entrySet()) {
			incTreshold(entry.getKey(), entry.getValue());
		}
	}

	private void incTreshold(final double treshold, final long incBy) {
		final Long hcount = data.get(treshold);
		data.put(treshold, incBy + (hcount == null ? 0 : hcount));
	}

	private double getMaxRange(final double value) {
		//On crée une répartion : 1, 2, 5 - 10, 20, 50 - 100, 200, 500...
		//Optim
		/*	if (value <= 0)
				return 0;
			if (value <= 1)
				return 1;
			if (value <= 2)
				return 2;
			if (value <= 5)
				return 5;
			if (value <= 10)
				return 10;
			if (value <= 20)
				return 20;
			if (value <= 50)
				return 50;
			if (value <= 100)
				return 100;
			if (value <= 200)
				return 200;
			if (value <= 500)
				return 500;
			if (value <= 1000)
				return 1000;
			if (value <= 2000)
				return 2000;
			if (value <= 5000)
				return 5000;*/
		//Other cases
		final double index = Math.floor(Math.log10(value));
		final double treshold = Math.pow(10, index);
		if (value <= treshold) {
			return treshold;
		} else if (value <= 2 * treshold) {
			return 2 * treshold;
		} else if (value <= 5 * treshold) {
			return 5 * treshold;
		}
		return 10 * treshold;
	}

	public HDistribution build() {
		return new HDistribution(data);
	}

}
