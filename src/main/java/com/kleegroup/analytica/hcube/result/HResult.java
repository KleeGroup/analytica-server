package com.kleegroup.analytica.hcube.result;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.cube.HMetric;
import com.kleegroup.analytica.hcube.cube.HMetricBuilder;
import com.kleegroup.analytica.hcube.cube.HMetricKey;
import com.kleegroup.analytica.hcube.cube.HVirtualCube;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.query.HQuery;

/**
 * Résultat d'une requête.
 * Il s'agit d'une série de parallélépipèdes continus, qui peut se voir comme autant un cube virtuel.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public final class HResult implements HVirtualCube {
	private final HQuery query;
	private final Map<HCategory, List<HCube>> cubeSeries;
	private Map<HMetricKey, HMetric> metrics; //lazy

	public HResult(final HQuery query, final Map<HCategory, List<HCube>> cubeSeries) {
		Assertion.notNull(query);
		Assertion.notNull(cubeSeries);
		//---------------------------------------------------------------------
		this.query = query;
		this.cubeSeries = cubeSeries;
	}

	public HQuery getQuery() {
		return query;
	}

	public List<HCube> getCubes(HCategory categoryPosition) {
		Assertion.notNull(categoryPosition);
		Assertion.precondition(cubeSeries.containsKey(categoryPosition), "{0} not in resultSet", categoryPosition);
		//-------------------------------------------------------------------------
		return Collections.unmodifiableList(cubeSeries.get(categoryPosition));
	}

	//-------------------------------------------------------------------------
	private Map<HMetricKey, HMetric> getLazyMetrics() {
		if (metrics == null) {
			Map<HMetricKey, HMetricBuilder> metricBuilders = new HashMap<HMetricKey, HMetricBuilder>();
			for (List<HCube> cubes : cubeSeries.values()) {
				for (HCube cube : cubes) {
					for (HMetric metric : cube.getMetrics()) {
						HMetricBuilder metricBuilder = metricBuilders.get(metric.getKey());
						if (metricBuilder == null) {
							metricBuilder = new HMetricBuilder(metric.getKey());
							metricBuilders.put(metric.getKey(), metricBuilder);
						}
						metricBuilder.withMetric(metric);
					}
				}
			}
			metrics = new HashMap<HMetricKey, HMetric>();
			for (Entry<HMetricKey, HMetricBuilder> entry : metricBuilders.entrySet()) {
				metrics.put(entry.getKey(), entry.getValue().build());
			}
		}
		return metrics;
	}

	/** {@inheritDoc} */
	public HMetric getMetric(final HMetricKey metricKey) {
		Assertion.notNull(metricKey);
		//---------------------------------------------------------------------
		return getLazyMetrics().get(metricKey);
	}

	/** {@inheritDoc} */
	public Collection<HMetric> getMetrics() {
		return getLazyMetrics().values();
	}
}
