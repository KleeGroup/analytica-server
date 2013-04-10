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
package com.kleegroup.analytica.hcube.cube;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.CubePosition;

/**
 * Un cube contient :
 *  - des métriques nommées
 *  	exemple : temps réponse, nombre de mails envoyés
 *  - des métadonnées 
 *  	exemple : tags, users
 *  
 * @author npiedeloup, pchretien
 * @version $Id: Cube.java,v 1.6 2012/10/16 13:34:49 pchretien Exp $
 */
public final class Cube {
	/**
	 * Identifiant du cube : un cube est localisé dans le temps et l'espace (axe fonctionnel).
	 */
	private final CubePosition cubePosition;
	private final Map<MetricKey, Metric> metrics;

	//	private final Map<String, Collection<MetaData>> indexedMetadatas;
	//	private final Collection<MetaData> metaDatas;

	Cube(final CubePosition cubePosition, final Collection<Metric> metrics/*, final Collection<MetaData> metadatas*/) {
		Assertion.notNull(cubePosition);
		Assertion.notNull(metrics);
		//		Assertion.notNull(metadatas);
		//---------------------------------------------------------------------
		this.cubePosition = cubePosition;
		this.metrics = index(metrics);
		//		indexedMetadatas = indexMeta(metadatas);
		//		this.metaDatas = metadatas;
	}

	public CubePosition getPosition() {
		return cubePosition;
	}

	/**
	 * Accès d'une métrique par son nom
	 * @param name Nom de la métrique
	 * @return Métrique
	 */
	public Metric getMetric(final MetricKey metricKey) {
		return metrics.get(metricKey);
	}

	/**
	 * Liste de toutes les métriques
	 * @return Métriques du cube
	 */
	public Collection<Metric> getMetrics() {
		return metrics.values();
	}

	//	public Collection<MetaData> getMetaData(final String name) {
	//		return indexedMetadatas.get(name);
	//	}
	//
	//	public Collection<MetaData> getMetaDatas() {
	//		return metaDatas;
	//	}

	private static Map<MetricKey, Metric> index(final Collection<Metric> metrics) {
		final Map<MetricKey, Metric> indexedList = new LinkedHashMap<MetricKey, Metric>(metrics.size());
		for (final Metric metric : metrics) {
			final Object old = indexedList.put(metric.getKey(), metric);
			Assertion.isNull(old, "La liste de Metric ne doit pas contenir de doublon");
		}
		return indexedList;
	}

	//	private static Map<String, Collection<MetaData>> indexMeta(final Collection<MetaData> metaDatas) {
	//		final Map<String, Collection<MetaData>> indexedList = new LinkedHashMap<String, Collection<MetaData>>(metaDatas.size());
	//		for (final MetaData metaData : metaDatas) {
	//			Collection<MetaData> list = indexedList.get(metaData.getName());
	//			if (list == null) {
	//				list = new ArrayList<MetaData>();
	//				indexedList.put(metaData.getName(), list);
	//			}
	//			list.add(metaData);
	//		}
	//		return indexedList;
	//	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder()//
				.append(cubePosition.id()).append("{\n\tMetrics:");
		for (final Metric metric : getMetrics()) {
			sb.append("\n\t\t- ").append(metric.getKey()).append(" = ").append(metric.get(DataType.mean));
		}
		//		sb.append("\n\tMetaDatas:");
		//		for (final MetaData metadata : getMetaDatas()) {
		//			sb.append("\n\t\t- ").append(metadata.getName()).append(" = ").append(metadata.getValue());
		//		}
		sb.append("}");
		return sb.toString();
	}
}
