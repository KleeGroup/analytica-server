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

import com.google.gson.JsonElement;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.result.HResult;
import com.kleegroup.analytica.hcube.result.HSerie;
import com.kleegroup.analytica.server.ServerManager;

/**
 * @author statchum
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public class Utils {
	private final ServerManager serverManager;

	private Utils(final ServerManager serverManager) {
		this.serverManager = serverManager;
	}

	public static Utils getInstance(final ServerManager serverManager) {
		return new Utils(serverManager);
	}

	/**
	 * Charge Les données d'un graphe mono série.
	 * @param Les données à transformer.
	 * @return La liste de poins retravaillée.
	 */
	public List<DataPoint> loadDataPointsMonoSerie(final HResult result) {
		final HQuery query = result.getQuery();
		final List<HSerie> series = new ArrayList<HSerie>();
		for (final HCategory category : query.getAllCategories()) {
			series.add(result.getSerie(category));
		}
		final List<DataPoint> points = new ArrayList<DataPoint>();
		for (final HCategory category : query.getAllCategories()) {
			for (final HCube cube : result.getSerie(category).getCubes()) {
				final HMetric metric = cube.getMetric(new HMetricKey("duration", true));
				final DataPoint dPoint = new DataPoint(cube.getKey().getTime().getValue(), metric != null ? metric.getMean() : Double.NaN);
				if (dPoint.getValue() != null) {
					points.add(dPoint);//Double.NaN
				}
			}
		}

		return points;
	}

	private List<String> readDataKeyList(final String datas) {
		final List<String> cles = new ArrayList<String>();
		final List<String> dataKeys = Arrays.asList(datas.split(";"));
		//final List<HMetricKey> metricKeys = new ArrayList<HMetricKey>();
		for (final String s : dataKeys) {
			final String[] list = s.split(":");
			//			if (list.length > 1) {
			//				metricKeys.add(new HMetricKey(list[0], true));
			//			} else if (list.length == 1) {
			//				metricKeys.add(new HMetricKey(s, true));
			//			}
			cles.add(list[0]);
		}
		return dataKeys;
		// return metricKeys;
	}

	/**
	 * 
	 * @param timeStr
	 *            : e.g: NOW+1h
	 * @param dimension
	 *            : Dimension temporelle : année/mois/jour/...
	 * @return Date obtenue à partir des deux indications précedentes
	 */
	private Date readDate(final String timeStr, final HTimeDimension dimension) {
		if (timeStr.equals("NOW")) {
			return new Date();
		} else if (timeStr.startsWith("NOW-")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW-".length()));
			return new Date(System.currentTimeMillis() - deltaMs);
		} else if (timeStr.startsWith("NOW+")) {
			final long deltaMs = readDeltaAsMs(timeStr.substring("NOW+".length()));
			return new Date(System.currentTimeMillis() + deltaMs);
		}
		final String datePattern;
		switch (dimension) {
			case Year:
				datePattern = "yyyy";
				break;
			case Month:
				datePattern = "MM/yyyy";
				break;
			case Day:
				datePattern = "dd/MM/yyyy";
				break;
			case Hour:
			case Minute:
			default:
				datePattern = "HH:mm dd/MM/yyyy";
		}
		final SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		try {
			return sdf.parse(timeStr);
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			throw new KRuntimeException("Erreur de format de date (" + timeStr + "). Format attendu :" + sdf.toPattern());
		}
	}

	/**
	 * 
	 * @param deltaAsString
	 * @return delta en millisecondes
	 */
	private long readDeltaAsMs(final String deltaAsString) {
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
		final HQuery query = serverManager.createQueryBuilder().on(timeDim).from(minValue).to(maxValue).with(categories).build();
		return serverManager.execute(query);
	}

	/**
	 * Charge des données pour un graphe multi série.
	 * @param result
	 * @param datas
	 * @return
	 */
	public Map<String, List<DataPoint>> loadDataPointsMuliSerie(final HResult result, final String datas) {
		final HQuery query = result.getQuery();
		final List<String> metricKeys = readDataKeyList(datas);
		List<DataPoint> points;
		final Map<String, List<DataPoint>> pointsMap = new HashMap<String, List<DataPoint>>();
		for (final String metricKeydata : metricKeys) {
			points = new ArrayList<DataPoint>();
			for (final HCategory category : query.getAllCategories()) {
				for (final HCube cube : result.getSerie(category).getCubes()) {
					final String[] metricKey = metricKeydata.split(":");
					final HMetric hMetric = cube.getMetric(new HMetricKey(metricKey[0], true));
					double val = 0;
					switch (metricKey[1]) {
						case "mean":
							val = hMetric != null ? hMetric.getMean() : Double.NaN;
							break;
						case "count":
							val = hMetric != null ? hMetric.getCount() : Double.NaN;
							break;
						case "sum":
							val = hMetric != null ? hMetric.getSum() : Double.NaN;
							break;

					}
					points.add(new DataPoint(cube.getKey().getTime().getValue(), val));
				}
				pointsMap.put(metricKeydata, points);
			}
		}
		return pointsMap;
	}

	public JsonElement getAggregatedValues(final HResult result) {
		// TODO Auto-generated method stub
		return null;
	}

}
