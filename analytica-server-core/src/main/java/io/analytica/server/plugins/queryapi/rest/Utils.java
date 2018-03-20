/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.server.plugins.queryapi.rest;

import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.ProcessAggregatorQueryBuilder;

/**
 * @author statchum, npiedeloup
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public final class Utils {

	public static ProcessAggregatorQuery createQuery(final String applicationName, final String from, final String to, final String timeDimension, final String type, final String subCategories, final boolean children, final String locations) {
		final ProcessAggregatorQueryBuilder queryBuilder = new ProcessAggregatorQueryBuilder(applicationName);
		queryBuilder.withLocations(locations).withCategories(type, subCategories).withDateRange(timeDimension, from, to);
		return queryBuilder.build();
	}

	//	public static List<TimedDataSerie> loadDataSeriesByTime(final HResult result, final List<String> dataKeys/*ex "{duration:mean","ERROR:mean"}*/) {
	//		Assertion.checkNotNull(result);
	//		// ---------------------------------------------------------------------
	//		final List<TimedDataSerie> dataSeries = new ArrayList<>();
	//
	//		for (final HCategory category : result.getAllCategories()) { //Normalement une seule categorie
	//			for (final Map.Entry<HTime, HCube> entry : result.getSerie(category).getCubes().entrySet()) {
	//				final HCube cube = entry.getValue();
	//				final HTime time = entry.getKey();
	//				final Map<String, String> values = new HashMap<>();
	//				for (final String dataKey : dataKeys) {
	//					final String[] metricKey = dataKey.split(":");
	//					final HMetric hMetric = cube.getMetric(metricKey[0]);
	//					if (hMetric != null) {
	//						final String val = getMetricValue(metricKey, hMetric, null);
	//						values.put(dataKey, val);
	//					} else {
	//						//pas de values.put(key, val); on laisse null
	//					}
	//				}
	//				final TimedDataSerie dataSerie = new TimedDataSerie(time.inMillis(), values);
	//				dataSeries.add(dataSerie);
	//			}
	//		}
	//		return dataSeries;
	//	}
}
