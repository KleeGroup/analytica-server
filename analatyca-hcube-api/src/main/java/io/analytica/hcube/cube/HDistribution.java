package io.analytica.hcube.cube;

import io.vertigo.lang.Assertion;

import java.util.Map;

/**
 * Distribution d'un ensemble de valeurs.
 * @author npiedeloup, pchretien
 */
public final class HDistribution {
	//	private static final double[] RANGES = { 0d, 1d, 2d, 5d, 10d, 20d, 50d, 100d, 200d, 500d, 1000d, Double.MAX_VALUE };
	//	private final long[] data = new long[RANGES.length];
	//
	private final Map<Double, Long> data;

	HDistribution(final Map<Double, Long> data) {
		Assertion.checkNotNull(data);
		//---------------------------------------------------------------------
		this.data = data;
		//			for (int i = 0; i<RANGES.length i++){
		//				this.data[0] += entry.getValue();
		//			}
		//		for (Entry<Double, Long> entry : data.entrySet()) {
		//			if (entry.getKey() <= RANGES[0]) {
		//				this.data[0] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[1]) {
		//				this.data[1] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[2]) {
		//				this.data[2] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[3]) {
		//				this.data[3] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[4]) {
		//				this.data[4] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[5]) {
		//				this.data[5] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[6]) {
		//				this.data[6] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[7]) {
		//				this.data[7] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[8]) {
		//				this.data[8] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[9]) {
		//				this.data[9] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[10]) {
		//				this.data[10] += entry.getValue();
		//			} else if (entry.getKey() <= RANGES[11]) {
		//				this.data[11] += entry.getValue();
		//			}
		//		}

	}

	public Map<Double, Long> getData() {
		return data;
		//		Map<Double, Long> map = new HashMap<>();
		//		for (int i = 0; i < RANGES.length; i++) {
		//			map.put(RANGES[i], data[i]);
		//		}
		//		return map;
	}
}
