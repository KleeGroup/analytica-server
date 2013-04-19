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
public final class Cube implements VirtualCube {
	/**
	 * Identifiant du cube : un cube est localisé dans le temps et l'espace (axe fonctionnel).
	 */
	private final CubePosition cubePosition;
	private final Map<MetricKey, Metric> metrics;

	Cube(final CubePosition cubePosition, final Collection<Metric> metrics) {
		Assertion.notNull(cubePosition);
		Assertion.notNull(metrics);
		//---------------------------------------------------------------------
		this.cubePosition = cubePosition;
		this.metrics = new LinkedHashMap<MetricKey, Metric>(metrics.size());
		for (final Metric metric : metrics) {
			final Object old = this.metrics.put(metric.getKey(), metric);
			Assertion.isNull(old, "La liste de Metric ne doit pas contenir de doublon");
		}
	}

	public CubePosition getPosition() {
		return cubePosition;
	}

	/** {@inheritDoc} */
	public Metric getMetric(final MetricKey metricKey) {
		Assertion.notNull(metricKey);
		//---------------------------------------------------------------------
		return metrics.get(metricKey);
	}

	/** {@inheritDoc} */
	public Collection<Metric> getMetrics() {
		return metrics.values();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder()//
				.append(cubePosition.id()).append("\n\tmetrics:{");
		for (final Metric metric : getMetrics()) {
			sb.append("\n\t\t ").append(metric);
		}
		if (!getMetrics().isEmpty()) {
			sb.append("\n\t");
		}
		sb.append("}");
		return sb.toString();
	}
}
