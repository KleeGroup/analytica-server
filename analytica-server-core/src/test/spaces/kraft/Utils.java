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
package kasperimpl.spaces.spaces.kraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kasper.kernel.util.Assertion;
import anomalies.performance.BollingerBand;
import anomalies.performance.PerformanceManager;
import anomalies.signal.Signal;

import io.analytica.hcube.cube.HCounterType;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HQueryBuilder;
import io.analytica.hcube.result.HResult;

/**
 * @author statchum
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public final class Utils {

	/**
	 * Charge Les données d'un graphe mono série.
	 * @param Les données à transformer.
	 * @return La liste de poins retravaillée.
	 */
	public static List<DataPoint> loadDataPointsMonoSerie(final HResult result, final String datas) {
		final PerformanceManager manager = new PerformanceManager(1000, 0, 2);
		final Signal signal = new Signal();
		Assertion.notNull(result);
		// ---------------------------------------------------------------------
		final HMetricKey metricKey = new HMetricKey("HMDURATION", true);
		final HCounterType counterType = HCounterType.mean;
		final List<DataPoint> dataPoints = new ArrayList<DataPoint>();

		for (final HCategory category : result.getAllCategories()) {
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final HMetric metric = cube.getMetric(metricKey);

				if (metric != null) {
					final double value = metric.get(counterType);
					final DataPoint dataPoint = new DataPoint(cube.getKey().getTime().getValue(), value);

					if (dataPoint.getValue() != null) {
						dataPoints.add(dataPoint);
						manager.checkMeasure(dataPoint, signal);
					}
				}
			}
		}

		return dataPoints;
	}

	public static Map<String, Double> getAggregatedValuesByCategory(final HResult result, final String datas) {
		Assertion.notNull(result);

		// ---------------------------------------------------------------------
		final String[] type = datas.split(":");
		final HMetricKey metricKey = new HMetricKey("HMDURATION", true);
		final HCounterType counterType = HCounterType.valueOf(type[1]);
		//		final HCounterType counterType = HCounterType.count;
		final Map<String, Double> valueByCategory = new HashMap<>();

		System.out.println(result.getAllCategories());

		for (final HCategory category : result.getAllCategories()) {
			System.out.println(category.drillUp());

			final HMetric metric = result.getSerie(category).getMetric(metricKey);

			if (metric != null) {
				final double value = metric.get(counterType);

				valueByCategory.put(category.toString(), value);
			}
		}

		return valueByCategory;
	}

	public static HQuery createQuery(final String from, final String to, final String timeDimension, final String categories, final boolean children) {
		final HQueryBuilder queryBuilder = new HQueryBuilder()//
				.on(timeDimension)//
				.from(from)//
				.to(to);
		if (children) {
			queryBuilder.withChildren(categories);
		} else {
			queryBuilder.with(categories);
		}
		// @formatter:on
		return queryBuilder.build();
	}

	/**
	 * Charge des données pour un graphe multi série.
	 * @param result
	 * @param datas
	 * @return
	 */
	public static Map<String, List<DataPoint>> loadDataPointsMuliSerie(final HResult result, final String datas) {
		Assertion.notNull(result);

		// ---------------------------------------------------------------------

		final HQuery query = result.getQuery();
		final List<String> dataKeys = Arrays.asList(datas.split(";"));
		List<DataPoint> dataPoints;
		final Map<String, List<DataPoint>> pointsMap = new HashMap<String, List<DataPoint>>();

		for (final String dataKey : dataKeys) {
			dataPoints = new ArrayList<DataPoint>();

			for (final HCategory category : result.getAllCategories()) { //Normalement une seule categorie
				for (final HCube cube : result.getSerie(category).getCubes()) {
					final String[] metricKey = dataKey.split(":");
					final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
					double val = 0;
					if (metricKey.length > 1) {
						final HCounterType counterType = HCounterType.valueOf(metricKey[1]);

						val = hMetric != null ? hMetric.get(counterType) : Double.NaN;
					} else {
						val = hMetric != null ? hMetric.get(HCounterType.mean) : Double.NaN;
					}

					final DataPoint dPoint = new DataPoint(cube.getKey().getTime().getValue(), val);

					if (dPoint.getValue() != null) {
						dataPoints.add(dPoint);
					}
				}
				pointsMap.put(dataKey, dataPoints);
			}
		}
		return pointsMap;
	}

	public static Map<String, List<DataPoint>> loadDataPointsStackedByCategory(final HResult result, final String datas) {
		Assertion.notNull(result);

		// ---------------------------------------------------------------------

		final HQuery query = result.getQuery();
		final List<String> dataKeys = Arrays.asList(datas.split(";"));
		List<DataPoint> dataPoints;
		final Map<String, List<DataPoint>> pointsMap = new HashMap<String, List<DataPoint>>();

		final String dataKey = datas;
		for (final HCategory category : result.getAllCategories()) {
			dataPoints = new ArrayList<DataPoint>();
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final String[] metricKey = dataKey.split(":");
				final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
				double val = 0;

				if (metricKey.length > 1) {
					final HCounterType counterType = HCounterType.valueOf(metricKey[1]);

					val = hMetric != null ? hMetric.get(counterType) : Double.NaN;
				} else {
					val = hMetric != null ? hMetric.get(HCounterType.mean) : Double.NaN;
				}

				final DataPoint dPoint = new DataPoint(cube.getKey().getTime().getValue(), val);

				if (dPoint.getValue() != null) {
					dataPoints.add(dPoint);
				} else {
					dataPoints.add(dPoint);
				}
			}
			pointsMap.put(category.id(), dataPoints);
		}
		return pointsMap;
	}

	/**
	 * 
	 * @param result
	 * @param datas
	 * @return building a dataTbable
	 */
	public static Map<String, Collection<Object>> getSparklinesTableDatas(final HResult result, final String datas) {

		final Map<String, Collection<Object>> tableMap = new HashMap<String, Collection<Object>>();

		for (final HCategory category : result.getAllCategories()) {
			final Collection<Object> tableCollection = new ArrayList<>();
			tableCollection.add(result.getSerie(category).getMetrics());
			tableCollection.add(getStringList(category, result, "duration:mean"));
			tableCollection.add(getStringList(category, result, "duration:count"));
			tableMap.put(category.id(), tableCollection);
		}
		return tableMap;
	}

	/**
	 * @param category
	 * @param result
	 * @param string
	 * @return
	 */
	private static String getStringList(final HCategory category, final HResult result, final String dataKey) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (final HCube cube : result.getSerie(category).getCubes()) {
			final String[] metricKey = dataKey.split(":");
			final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
			double val = 0;

			if (metricKey.length > 1) {
				final HCounterType counterType = HCounterType.valueOf(metricKey[1]);

				val = hMetric != null ? hMetric.get(counterType) : Double.NaN;
			} else {
				val = hMetric != null ? hMetric.get(HCounterType.mean) : Double.NaN;
			}

			if (!Double.toString(val).equals("NaN")) {
				val = Math.ceil(100 * val) / 100;
				stringBuilder.append(val);
				stringBuilder.append(",");
			}
		}

		return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);

	}

	/**
	 * @param result
	 * @param datas
	 * @return a Matrix matching metrics values by days and hours. It will be used to build a punchcard  
	 */
	public static Map<String, Map<Long, Double>> getMetricByDayAndHour(final HResult result, final String dataKey) {

		for (final HCategory category : result.getAllCategories()) {
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final String[] metricKey = dataKey.split(":");
				final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
				double val = 0;

				if (metricKey.length > 1) {
					final HCounterType counterType = HCounterType.valueOf(metricKey[1]);

					val = hMetric != null ? hMetric.get(counterType) : Double.NaN;
				} else {
					val = hMetric != null ? hMetric.get(HCounterType.mean) : Double.NaN;
				}
			}
		}
		return null;
	}

	public static Map<String, Object> getPunchCardFakeDatas(final HResult result, final String dataKey) {
		final Map<String, Object> matrix = new LinkedHashMap<>();
		final String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

		final int length = days.length;
		for (int i = length; i > 0; i--) {
			final Double[] valByHours = new Double[24];
			for (int j = 0; j < 24; j++) {
				double val = 300 * Math.random();
				val = Math.ceil(100 * val) / 100;
				valByHours[j] = val;
			}
			matrix.put(days[i - 1], valByHours);
			System.out.println(days[i - 1]);
		}
		return matrix;
	}

	public static class Punchcard {
		public String[] days;
		public double[][] data;
	}

	public static Punchcard getPunchCardDatas(final HResult result, final String dataKey) {
		final String[] days = { "dimanche", "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi" };

		final Punchcard punchcard = new Punchcard();
		punchcard.days = days;
		punchcard.data = new double[7][24];

		final String[] metricKey = dataKey.split(":");

		for (final HCategory category : result.getAllCategories()) {
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
				final int h = cube.getKey().getTime().getValue().getHours();
				final int d = cube.getKey().getTime().getValue().getDay();
				punchcard.data[d][h] = hMetric == null ? 0d : hMetric.getCount();
			}
		}
		return punchcard;
	}

	/**
	 * @param result
	 * @param datas
	 * @return
	 */
	public static Map<String, List<DataPoint>> loadBollingerBands(final HResult result, final String datas) {

		final PerformanceManager manager = new PerformanceManager(1000, 0, 2);
		final Signal signal = new Signal();
		Assertion.notNull(result);
		// ---------------------------------------------------------------------
		final HMetricKey metricKey = new HMetricKey("HMDURATION", true);
		final HCounterType counterType = HCounterType.mean;
		final List<DataPoint> dataPoints = new ArrayList<DataPoint>();

		for (final HCategory category : result.getAllCategories()) {
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final HMetric metric = cube.getMetric(metricKey);

				if (metric != null) {
					final double value = metric.get(counterType);
					final DataPoint dataPoint = new DataPoint(cube.getKey().getTime().getValue(), value);

					if (dataPoint.getValue() != null) {
						dataPoints.add(dataPoint);
						manager.checkMeasure(dataPoint, signal);
					}
				}
			}
		}

		final BollingerBand bollingerBands = new BollingerBand(signal);
		final Map<String, List<DataPoint>> pointsMap = new HashMap<String, List<DataPoint>>();
		pointsMap.put("upperBand", bollingerBands.getUpperBand().getPoints());
		pointsMap.put("lowerBand", bollingerBands.getLowerBand().getPoints());
		pointsMap.put("meanBand", bollingerBands.getMeanMiddleBand().getPoints());
		pointsMap.put("real", dataPoints);

		return pointsMap;
	}
}
