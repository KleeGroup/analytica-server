/**
 * 
 */
package kasperimpl.spaces.spaces.kraft;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
		//---------------------------------------------------------------------
		this.serverManager = serverManager;
	}

	/**
	 * Charge Les données d'un graphe mono série.
	 * @param Les données à transformer.
	 * @return La liste de poins retravaillée.
	 */
	public List<DataPoint> loadDataPointsMonoSerie(final HResult result, final String datas) {
		Assertion.notNull(result);
		//---------------------------------------------------------------------
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
		//---------------------------------------------------------------------

		final HMetricKey metricKey = new HMetricKey("duration", true);
		final HCounterType counterType = HCounterType.mean;

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

	//			final List<DataPoint> dataPoints = new ArrayList<DataPoint>();
	//
	//		final List<DataPoint> list = loadDataPointsMonoSerie(result, datas);
	////		DataPoint maxPoint = list.get(0);
	//		DataPoint minPoint = list.get(0);
	//		for (final DataPoint dPoint : list) {
	//			if (dPoint.getValue() > maxPoint.getValue()) {
	//				maxPoint = dPoint;
	//			}
	//			if (dPoint.getValue() < maxPoint.getValue()) {
	//				minPoint = dPoint;
	//			}
	//		}
	//
	////		for (final DataPoint dPoint : list) {
	//			if (dPoint.getValue() > maxPoint.getValue()) {
	//				maxPoint = dPoint;
	//			}
	//		}
	//Récupérer pt Max,
	//Récupérer pt Min,
	//Récupérer pt Moy.

	//		return null;

	//	private static List<String> readDataKeys(final String datas) {
	//		final List<String> key = new ArrayList<String>();
	//		final List<String> dataKeys = Arrays.asList(datas.split(";"));
	//		for (final String str : dataKeys) {
	//			final String[] list = str.split(":");
	//			key.add(list[0]);
	//		}
	//		return dataKeys;
	//	}

	/**
	 * 
	 * @param timeStr
	 *            : e.g: NOW+1h
	 * @param dimension
	 *            : Dimension temporelle : année/mois/jour/...
	 * @return Date obtenue à partir des deux indications précedentes
	 */
	private static Date readDate(final String timeStr, final HTimeDimension dimension) {
		Assertion.notEmpty(timeStr);
		//---------------------------------------------------------------------
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
	 * @return Construis une Hresult à partir des infos fournies
	 */

	public HResult resolveQuery(final String timeFrom, final String timeTo, final String timeDimension, final String categories) {
		final HTimeDimension timeDim = HTimeDimension.valueOf(timeDimension);
		final Date minValue = readDate(timeFrom, timeDim);
		final Date maxValue = readDate(timeTo, timeDim);
		//@formatter:off
		final HQuery query = serverManager.createQueryBuilder()
				.on(timeDim)
				.from(minValue)
				.to(maxValue)
				.with(categories)
				.build();
		//@formatter:on
		return serverManager.execute(query);
	}

	/**
	 * Charge des données pour un graphe multi série.
	 * @param result
	 * @param datas
	 * @return
	 */
	public Map<String, List<DataPoint>> loadDataPointsMuliSerie(final HResult result, final String datas) {
		Assertion.notNull(result);
		//---------------------------------------------------------------------
		//	result.getSerie(null).getMetric(metricKey)

		final HQuery query = result.getQuery();
		final List<String> dataKeys = Arrays.asList(datas.split(";"));
		List<DataPoint> dataPoints;
		final Map<String, List<DataPoint>> pointsMap = new HashMap<String, List<DataPoint>>();
		for (final String dataKey : dataKeys) {
			dataPoints = new ArrayList<DataPoint>();
			for (final HCategory category : query.getAllCategories()) {
				for (final HCube cube : result.getSerie(category).getCubes()) {
					final String[] metricKey = dataKey.split(":");
					final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
					double val = 0;
					if (metricKey.length > 1) {
						final HCounterType counterType = HCounterType.valueOf(metricKey[1]);
						val = hMetric != null ? hMetric.get(counterType) : Double.NaN;
						//						switch (metricKey[1]) {
						//							case "mean":
						//								val = hMetric != null ? hMetric.get(HCounterType.mean) : Double.NaN;
						//								break;
						//							case "count":
						//								val = hMetric != null ? hMetric.get(HCounterType.count) : Double.NaN;
						//								break;
						//							case "sum":
						//								val = hMetric != null ? hMetric.get(HCounterType.sum) : Double.NaN;
						//								break;
						//							case "max":
						//								val = hMetric != null ? hMetric.get(HCounterType.max) : Double.NaN;
						//								break;
						//							case "min":
						//								val = hMetric != null ? hMetric.get(HCounterType.min) : Double.NaN;
						//								break;
						//						}
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

	public Object getAggregatedValues(final HResult result, final String datas) {
		final Map map = loadDataPointsMuliSerie(result, datas);
		//Réupérer le pt Max par entrée de la map
		//récupérer le pt Min par entrée de la map
		//Récupérer un pt moyen par entrée de la map
		return null;
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
		//---------------------------------------------------------------------

		final List<String> categoriesList = Arrays.asList(categories.split(";"));
		final HMetricKey metricKey = new HMetricKey("duration", true);
		final HCounterType counterType = HCounterType.mean;
		final Map<String, Double> valueByCategory = new HashMap<>();
		for (final String category : categoriesList) {
			final HResult result = resolveQuery(timeFrom, timeTo, timeDim, category);
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
}
