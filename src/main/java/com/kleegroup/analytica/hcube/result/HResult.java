package com.kleegroup.analytica.hcube.result;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.cube.Cube;
import com.kleegroup.analytica.hcube.cube.Metric;
import com.kleegroup.analytica.hcube.cube.MetricBuilder;
import com.kleegroup.analytica.hcube.cube.MetricKey;
import com.kleegroup.analytica.hcube.cube.VirtualCube;
import com.kleegroup.analytica.hcube.query.Query;

/**
 * Résultat d'une requête.
 * Il s'agit d'un parallélépipède continu, qui peut se voir comme un cube virtuel.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public final class HResult implements VirtualCube {
	private final Query query;
	private final List<Cube> cubes;
	private Map<MetricKey, Metric> metrics; //lazy

	public HResult(final Query query, final List<Cube> cubes) {
		Assertion.notNull(query);
		Assertion.notNull(cubes);
		//---------------------------------------------------------------------
		this.query = query;
		this.cubes = cubes;
	}

	public Query getQuery() {
		return query;
	}

	public List<Cube> getCubes() {
		return Collections.unmodifiableList(cubes);
	}

	//-------------------------------------------------------------------------
	private Map<MetricKey, Metric> getLazyMetrics() {
		if (metrics == null) {
			Map<MetricKey, MetricBuilder> metricBuilders = new HashMap<MetricKey, MetricBuilder>();
			for (Cube cube : cubes) {
				for (Metric metric : cube.getMetrics()) {
					MetricBuilder metricBuilder = metricBuilders.get(metric.getKey());
					if (metricBuilder == null) {
						metricBuilder = new MetricBuilder(metric.getKey());
						metricBuilders.put(metric.getKey(), metricBuilder);
					}
					metricBuilder.withMetric(metric);
				}
			}
			metrics = new HashMap<MetricKey, Metric>();
			for (Entry<MetricKey, MetricBuilder> entry : metricBuilders.entrySet()) {
				metrics.put(entry.getKey(), entry.getValue().build());
			}
		}
		return metrics;
	}

	/** {@inheritDoc} */
	public Metric getMetric(final MetricKey metricKey) {
		Assertion.notNull(metricKey);
		//---------------------------------------------------------------------
		return getLazyMetrics().get(metricKey);
	}

	/** {@inheritDoc} */
	public Collection<Metric> getMetrics() {
		return getLazyMetrics().values();
	}
}
