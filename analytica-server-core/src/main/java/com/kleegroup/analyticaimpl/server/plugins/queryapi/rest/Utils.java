package com.kleegroup.analyticaimpl.server.plugins.queryapi.rest;

import io.vertigo.kernel.lang.Assertion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kleegroup.analytica.hcube.cube.HCounterType;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.query.HQueryBuilder;
import com.kleegroup.analytica.hcube.result.HResult;
import com.kleegroup.analytica.hcube.result.HSerie;

/**
 * @author statchum, npiedeloup
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public final class Utils {
	private static final String METRIC_KEY_HISTO = "histo";
	private static final String METRIC_KEY_CLUSTERED = "clustered";
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * Charge Les données d'un graphe mono série.
	 * @param Les données à transformer.
	 * @return La liste de poins retravaillée.
	 */
	public static List<DataPoint> loadDataPointsMonoSerie(final HResult result, final String datas) {
		Assertion.checkNotNull(result);
		// ---------------------------------------------------------------------
		final HMetricKey metricKey = new HMetricKey("duration", true);
		final HCounterType counterType = HCounterType.mean;
		final List<DataPoint> dataPoints = new ArrayList<DataPoint>();

		for (final HCategory category : result.getAllCategories()) {
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final HMetric metric = cube.getMetric(metricKey);

				if (metric != null) {
					final double val = metric.get(counterType);
					final String value = Double.isNaN(val) ? null : String.valueOf(val);
					final DataPoint dataPoint = new DataPoint(cube.getKey().getTime().getValue(), value);

					if (dataPoint.getValue() != null) {
						dataPoints.add(dataPoint);
					}
				}
			}
		}
		return dataPoints;
	}

	public static List<TimedDataSerie> loadDataSeriesByTime(final HResult result, final List<String> dataKeys) {
		Assertion.checkNotNull(result);
		// ---------------------------------------------------------------------
		final List<TimedDataSerie> dataSeries = new ArrayList<TimedDataSerie>();
		for (final HCategory category : result.getAllCategories()) { //Normalement une seule categorie
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final Map<String, String> values = new HashMap<String, String>();
				for (final String dataKey : dataKeys) {
					final String[] metricKey = dataKey.split(":");
					final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
					if (hMetric != null) {
						final String val = getMetricValue(metricKey, hMetric, null);
						values.put(dataKey, val);
					} else {
						//pas de values.put(key, val); on laisse null
					}
				}
				final TimedDataSerie dataSerie = new TimedDataSerie(cube.getKey().getTime().getValue(), values);
				dataSeries.add(dataSerie);
			}
		}
		return dataSeries;
	}

	public static List<DataSerie> loadDataSeriesByCategory(final HResult result, final List<String> dataKeys) {
		Assertion.checkNotNull(result);
		// ---------------------------------------------------------------------
		final List<DataSerie> dataSeries = new ArrayList<DataSerie>();
		for (final HCategory category : result.getAllCategories()) {
			final HSerie serie = result.getSerie(category);
			final Map<String, String> values = new HashMap<String, String>();
			for (final String dataKey : dataKeys) {
				final String[] metricKey = dataKey.split(":");
				if (isMetricHistory(metricKey)) {
					String sep = "";
					final StringBuilder sb = new StringBuilder();
					for (final HCube cube : serie.getCubes()) {
						final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
						final String val = getMetricValue(metricKey, hMetric, "null");
						sb.append(sep).append(val);
						sep = ",";
					}
					values.put(dataKey, sb.toString());
				} else {
					final HMetric hMetric = serie.getMetric(new HMetricKey(metricKey[0], true));
					final String val = getMetricValue(metricKey, hMetric, null);
					if (val != null) {
						values.put(dataKey, val);
					} else {
						//pas de values.put(key, val); on laisse null
					}
				}
			}
			if (!values.isEmpty()) {
				final String[] subCategories = category.getValue();
				final DataSerie dataSerie = new DataSerie(subCategories[subCategories.length - 1], values);
				dataSeries.add(dataSerie);
			}
		}
		return dataSeries;
	}

	public static List<DataSerie> loadDataSeries(final HResult result, final List<String> dataKeys) {
		Assertion.checkNotNull(result);
		// ---------------------------------------------------------------------
		final List<DataSerie> dataSeries = new ArrayList<DataSerie>();
		for (final HCategory category : result.getAllCategories()) {
			final HSerie serie = result.getSerie(category);
			final Map<String, String> values = new HashMap<String, String>();
			for (final String dataKey : dataKeys) {
				final String[] metricKey = dataKey.split(":");
				if (isMetricHistory(metricKey)) {
					String sep = "";
					final StringBuilder sb = new StringBuilder();
					for (final HCube cube : serie.getCubes()) {
						final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
						final String val = getMetricValue(metricKey, hMetric, "null");
						sb.append(sep).append(val);
						sep = ",";
					}
					values.put(dataKey, sb.toString());
				} else {
					final HMetric hMetric = serie.getMetric(new HMetricKey(metricKey[0], true));
					final String val = getMetricValue(metricKey, hMetric, null);
					if (val != null) {
						values.put(dataKey, val);
					} else {
						//pas de values.put(key, val); on laisse null
					}
				}
			}
			if (!values.isEmpty()) {
				final String[] subCategories = category.getValue();
				final DataSerie dataSerie = new DataSerie(subCategories[subCategories.length - 1], values);
				dataSeries.add(dataSerie);
			}
		}
		return dataSeries;
	}

	private static boolean isMetricHistory(final String[] metricKey) {
		return metricKey.length > 2 && METRIC_KEY_HISTO.equals(metricKey[metricKey.length - 1]);
	}

	public static HQuery createQuery(final String from, final String to, final String timeDimension, final String type, final String subCategories, final boolean children) {
		final HQueryBuilder queryBuilder = new HQueryBuilder()//
				.on(timeDimension)//
				.from(from)//
				.to(to);
		final String[] subCategoriesArray;
		if (subCategories.startsWith("/")) {
			subCategoriesArray = subCategories.substring(1).split("/"); //remove the first / before split
		} else {
			subCategoriesArray = EMPTY_STRING_ARRAY;
		}
		if (children) {
			queryBuilder.withChildren(type, subCategoriesArray);
		} else {
			queryBuilder.with(type, subCategoriesArray);
		}
		// @formatter:on
		return queryBuilder.build();
	}

	/**
	 * Charge des données pour un graphe multi série.
	 * @param result
	 * @param dataKeys
	 * @return
	 */
	@Deprecated
	public static Map<String, List<DataPoint>> loadDataPointsMultiSerie(final HResult result, final List<String> dataKeys) {
		Assertion.checkNotNull(result);
		// ---------------------------------------------------------------------
		List<DataPoint> dataPoints;
		final Map<String, List<DataPoint>> pointsMap = new LinkedHashMap<String, List<DataPoint>>();

		for (final String dataKey : dataKeys) {
			dataPoints = new ArrayList<DataPoint>();
			final String[] metricKey = dataKey.split(":");
			for (final HCategory category : result.getAllCategories()) { //Normalement une seule categorie
				for (final HCube cube : result.getSerie(category).getCubes()) {
					final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
					final String val = getMetricValue(metricKey, hMetric, null);
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

	private static String getMetricValue(final String[] metricKey, final HMetric hMetric, final String nullString) {
		if (hMetric == null) {
			return nullString;
		}
		final double val;
		final boolean isLongValue; //used for rounding : with or without decimals
		if (metricKey.length > 1) {
			if (METRIC_KEY_CLUSTERED.equals(metricKey[1])) {
				isLongValue = true;
				Assertion.checkNotNull(hMetric.getClusteredValues(), "Metric ''{0}'' isn''t clustered)", Arrays.asList(metricKey));
				Assertion.checkArgument(metricKey.length == 3, "Clustered metric ''{0}'' must include its threshold value (exemple : ''duration:clustered:200'', you can add + or - for min and max value )", Arrays.asList(metricKey));
				final String threshold = metricKey[2];
				final boolean isMax = threshold.endsWith("+");
				final boolean isMin = threshold.endsWith("-");
				final Double thresholdValue = Double.parseDouble(isMax || isMin ? threshold.substring(0, threshold.length() - 1) : threshold);
				final Map<Double, Long> clusteredValues = hMetric.getClusteredValues();
				double sum = 0;
				for (final Map.Entry<Double, Long> entry : clusteredValues.entrySet()) {
					if (entry.getKey().equals(thresholdValue)) {
						sum += entry.getValue();
					}
					if (isMin && entry.getKey().compareTo(thresholdValue) < 0) {
						sum += entry.getValue();
					} else if (isMax && entry.getKey().compareTo(thresholdValue) > 0) {
						sum += entry.getValue();
					}
				}
				val = sum;
			} else {
				final HCounterType counterType = HCounterType.valueOf(metricKey[1]);
				isLongValue = counterType == HCounterType.count;
				val = hMetric.get(counterType);
			}
		} else {
			isLongValue = false;
			val = hMetric.get(HCounterType.mean);
		}

		if (Double.isNaN(val)) {
			return nullString;
		}
		if (isLongValue) {
			return String.valueOf(Math.round(val));
		}
		final String value = String.valueOf(Math.round(val * 100) / 100d);
		if (value.indexOf('.') > value.length() - 3) {
			return value + "0"; //it miss a 0 
		}
		return value;

	}

	public static Map<String, List<DataPoint>> loadDataPointsStackedByCategory(final HResult result, final String datas) {
		Assertion.checkNotNull(result);

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
				final DataPoint dPoint = new DataPoint(cube.getKey().getTime().getValue(), Double.isNaN(val) ? null : String.valueOf(val));
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
	//	public static Map<String, List<DataPoint>> loadBollingerBands(final HResult result, final String datas) {
	//
	//		final PerformanceManager manager = new PerformanceManager(1000, 0, 2);
	//		final Signal signal = new Signal();
	//		Assertion.checkNotNull(result);
	//		// ---------------------------------------------------------------------
	//		final HMetricKey metricKey = new HMetricKey("duration", true);
	//		final HCounterType counterType = HCounterType.mean;
	//		final List<DataPoint> dataPoints = new ArrayList<DataPoint>();
	//
	//		for (final HCategory category : result.getAllCategories()) {
	//			for (final HCube cube : result.getSerie(category).getCubes()) {
	//				final HMetric metric = cube.getMetric(metricKey);
	//
	//				if (metric != null) {
	//					final double value = metric.get(counterType);
	//					final DataPoint dataPoint = new DataPoint(cube.getKey().getTime().getValue(), value);
	//
	//					if (dataPoint.getValue() != null) {
	//						dataPoints.add(dataPoint);
	//						manager.checkMeasure(dataPoint, signal);
	//					}
	//				}
	//			}
	//		}
	//
	//		final BollingerBand bollingerBands = new BollingerBand(signal);
	//		final Map<String, List<DataPoint>> pointsMap = new HashMap<String, List<DataPoint>>();
	//		pointsMap.put("upperBand", bollingerBands.getUpperBand().getPoints());
	//		pointsMap.put("lowerBand", bollingerBands.getLowerBand().getPoints());
	//		pointsMap.put("meanBand", bollingerBands.getMeanMiddleBand().getPoints());
	//		pointsMap.put("real", dataPoints);
	//
	//		return pointsMap;
	//	}
}
