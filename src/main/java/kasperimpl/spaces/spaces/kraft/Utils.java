package kasperimpl.spaces.spaces.kraft;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.cube.HCounterType;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.query.HQueryBuilder;
import com.kleegroup.analytica.hcube.result.HResult;
import com.kleegroup.analytica.server.ServerManager;

/**
 * @author statchum
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public final class Utils {
	private final ServerManager serverManager;

	Utils(final ServerManager serverManager) {
		Assertion.notNull(serverManager);
		// ---------------------------------------------------------------------
		this.serverManager = serverManager;
	}

	/**
	 * Charge Les données d'un graphe mono série.
	 * @param Les données à transformer.
	 * @return La liste de poins retravaillée.
	 */
	public List<DataPoint> loadDataPointsMonoSerie(final HResult result, final String datas) {
		Assertion.notNull(result);
		// ---------------------------------------------------------------------
		final HMetricKey metricKey = new HMetricKey("duration", true);
		final HCounterType counterType = HCounterType.mean;
		final List<DataPoint> dataPoints = new ArrayList<DataPoint>();

		for (final HCategory category : result.getQuery().getAllCategories()) {
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final HMetric metric = cube.getMetric(metricKey);

				if (metric != null) {
					final double value = metric.get(counterType);
					final DataPoint dataPoint = new DataPoint(cube.getKey().getTime().getValue(), value);

					if (dataPoint.getValue() != null) {
						dataPoints.add(dataPoint);
					}
				}
			}
		}

		return dataPoints;
	}

	public Map<String, Double> getAggregatedValuesByCategory(final HResult result, final String datas) {
		Assertion.notNull(result);

		// ---------------------------------------------------------------------
		final HMetricKey metricKey = new HMetricKey("duration", true);
		final HCounterType counterType = HCounterType.count;
		final Map<String, Double> valueByCategory = new HashMap<>();

		System.out.println(result.getQuery().getAllCategories());

		for (final HCategory category : result.getQuery().getAllCategories()) {
			System.out.println(category.drillUp());

			final HMetric metric = result.getSerie(category).getMetric(metricKey);

			if (metric != null) {
				final double value = metric.get(counterType);

				valueByCategory.put(category.toString(), value);
			}
		}

		return valueByCategory;
	}

	/**
	 *
	 * @param result
	 * @param datas
	 * @return a Collections of metrics per category
	 */
	public Map<String, Collection<HMetric>> getDataTable(final HResult result, final String datas) {
		final Map<String, Collection<HMetric>> tableMap = new HashMap<String, Collection<HMetric>>();

		for (final HCategory category : result.getQuery().getAllCategories()) {
			tableMap.put(category.id(), result.getSerie(category).getMetrics());
		}

		return tableMap;
	}

	/**
	 *
	 * @param timeStr : e.g: NOW+1h
	 * @param dimension : Dimension temporelle : année/mois/jour/...
	 * @return Date obtenue à partir des deux indications précedentes
	 */
	private static Date readDate(final String timeStr, final HTimeDimension dimension) {
		Assertion.notEmpty(timeStr);

		// ---------------------------------------------------------------------
		if ("NOW".equals(timeStr)) {
			return new Date();
		} else if (timeStr.startsWith("NOW-")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW-".length()));
			return new Date(System.currentTimeMillis() - deltaMs);
		} else if (timeStr.startsWith("NOW+")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW+".length()));
			return new Date(System.currentTimeMillis() + deltaMs);
		}

		final SimpleDateFormat sdf = new SimpleDateFormat(dimension.getPattern());

		try {
			return sdf.parse(timeStr);
		} catch (final ParseException e) {
			throw new KRuntimeException("Erreur de format de date (" + timeStr + "). Format attendu :" + sdf.toPattern());
		}
	}

	/**
	 *
	 * @param deltaAsString
	 * @return delta en millisecondes
	 */
	private static long readDeltaAsMs(final String deltaAsString) {
		final Long delta;
		char unit = deltaAsString.charAt(deltaAsString.length() - 1);

		if (unit >= '0' && unit <= '9') {
			unit = 'd';
			delta = Long.valueOf(deltaAsString);
		} else {
			delta = Long.valueOf(deltaAsString.substring(0, deltaAsString.length() - 1));
		}

		switch (unit) {
			case 'd':
				return delta * 24 * 60 * 60 * 1000L;

			case 'h':
				return delta * 60 * 60 * 1000L;

			case 'm':
				return delta * 60 * 1000L;

			default:
				throw new KRuntimeException("La durée doit préciser l'unité de temps utilisée : d=jour, h=heure, m=minute");
		}
	}

	/**
	 * @param timeFrom
	 * @param timeTo
	 * @param timeDim
	 * @param categories
	 * @return Construit une Hresult à partir des infos fournies
	 */
	public HResult resolveQuery(final String timeFrom, final String timeTo, final String timeDimension, final String categories, final boolean children) {
		final HQuery query = createQuery(timeFrom, timeTo, timeDimension, categories, children);

		return serverManager.execute(query);
	}

	private HQuery createQuery(final String timeFrom, final String timeTo, final String timeDimension, final String categories, final boolean children) {
		final HTimeDimension timeDim = HTimeDimension.valueOf(timeDimension);
		final Date minValue = readDate(timeFrom, timeDim);
		final Date maxValue = readDate(timeTo, timeDim);
		final HQueryBuilder queryBuilder = serverManager.createQueryBuilder().on(timeDim).from(minValue).to(maxValue);
		// @formatter:off
		if(children){
			queryBuilder.withChildren(categories);
		}else{
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
	public Map<String, List<DataPoint>> loadDataPointsMuliSerie(final HResult result, final String datas) {
		Assertion.notNull(result);

		// ---------------------------------------------------------------------

		final HQuery query = result.getQuery();
		final List<String> dataKeys = Arrays.asList(datas.split(";"));
		List<DataPoint> dataPoints;
		final Map<String, List<DataPoint>> pointsMap = new HashMap<String, List<DataPoint>>();

		for (final String dataKey : dataKeys) {
			dataPoints = new ArrayList<DataPoint>();

			for (final HCategory category : query.getAllCategories()) { //Normalement une seule categorie
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

	public Map<String, List<DataPoint>> loadDataPointsStackedByCategory(final HResult result, final String datas) {
		Assertion.notNull(result);

		// ---------------------------------------------------------------------

		final HQuery query = result.getQuery();
		final List<String> dataKeys = Arrays.asList(datas.split(";"));
		List<DataPoint> dataPoints;
		final Map<String, List<DataPoint>> pointsMap = new HashMap<String, List<DataPoint>>();

		final String dataKey = datas;
		for (final HCategory category : query.getAllCategories()) {
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
	 * @return Map for building a dataTable with ..............
	 */
	public Map<String, Collection<Object>> getComplexTableDatas(final HResult result, final String datas) {

		final Map<String, Collection<Object>> tableMap = new HashMap<String, Collection<Object>>();
		final String dataKey = datas;

		for (final HCategory category : result.getQuery().getAllCategories()) {
			final Collection<Object> tableCollection = new ArrayList<>();
			tableCollection.add(result.getSerie(category).getMetrics());
			tableCollection.add(getDataPoints(category, result, "duration:mean"));
			tableCollection.add(getDataPoints(category, result, "duration:count"));
			tableMap.put(category.id(), tableCollection);
		}
		return tableMap;
	}

	/**
	 * 
	 * @param result
	 * @param datas
	 * @return building a dataTbable
	 */
	public Map<String, Collection<Object>> getSparklinesTableDatas(final HResult result, final String datas) {

		final Map<String, Collection<Object>> tableMap = new HashMap<String, Collection<Object>>();

		for (final HCategory category : result.getQuery().getAllCategories()) {
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
	private String getStringList(final HCategory category, final HResult result, final String dataKey) {
		final StringBuilder stringBuilder = new StringBuilder();
		int compt = 0;
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
				if (compt < result.getSerie(category).getCubes().size() - 1) {
					stringBuilder.append(",");
				}
			}
			compt++;
		}
		return stringBuilder.toString();
	}

	private Collection<DataPoint> getDataPoints(final HCategory category, final HResult result, final String dataKey) {
		final Collection<DataPoint> dataPoints = new ArrayList<DataPoint>();
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
			}/* else {
				dataPoints.add(dPoint);
				}*/
		}
		return dataPoints;
	}

	/**
	 * @param timeFrom
	 * @param timeTo
	 * @param timeDim
	 * @param category
	 * @param datas
	 * @return
	 */
	public Map<String, Double> testgetAggregatedValuesByCategory(final String timeFrom, final String timeTo, final String timeDim, final String categories, final String datas) {

		// ---------------------------------------------------------------------
		final List<String> categoriesList = Arrays.asList(categories.split(";"));
		final HMetricKey metricKey = new HMetricKey("duration", true);
		final HCounterType counterType = HCounterType.mean;
		final Map<String, Double> valueByCategory = new HashMap<>();

		for (final String category : categoriesList) {
			final HResult result = resolveQuery(timeFrom, timeTo, timeDim, category, true);

			System.out.println(result.getQuery().getAllCategories());

			for (final HCategory hCategory : result.getQuery().getAllCategories()) {
				System.out.println(hCategory.drillUp());

				final HMetric metric = result.getSerie(hCategory).getMetric(metricKey);

				if (metric != null) {
					final double value = metric.get(counterType);

					valueByCategory.put(category.toString(), value);
				}
			}
		}

		return valueByCategory;
	}

	/**
	 * @param result
	 * @param datas
	 * @return a Matrix matching metrics values by days and hours. It will be used to build a punchcard  
	 */
	public Map<String, Map<Long, Double>> getMetricByDayAndHour(final HResult result, final String dataKey) {

		for (final HCategory category : result.getQuery().getAllCategories()) {
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

	public Map<String, Object> getPunchCardFakeDatas(final HResult result, final String dataKey) {
		final Map<String, Object> matrix = new HashMap<>();
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
		}
		return matrix;
	}
}
