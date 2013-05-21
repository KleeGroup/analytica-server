package com.kleegroup.analytica.hcube.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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

/**
 * Résultat d'une série.
 * Une série est un parallélépipède continu pour une catégorie donnée, qui peut se voir comme autant un cube virtuel.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public final class HSerie implements HVirtualCube {
	private final HCategory category;
	private final List<HCube> cubes;
	private Map<HMetricKey, HMetric> metrics; //lazy

	/**
	 * Constructeur.
	 * @param category Catégorie de la série
	 * @param cubes Liste ordonnée des élements du parallélépipède
	 */
	public HSerie(final HCategory category, final List<HCube> cubes) {
		Assertion.notNull(category);
		Assertion.notNull(cubes);
		//---------------------------------------------------------------------
		this.category = category;
		this.cubes = cubes;
	}

	/**
	 * @return Category de la série
	 */
	public HCategory getCategory() {
		return category;
	}

	/**
	 * @return Liste ordonnée des élements du parallélépipède
	 */
	public List<HCube> getCubes() {
		Assertion.notNull(category);
		//-------------------------------------------------------------------------
		return Collections.unmodifiableList(cubes);
	}

	//-------------------------------------------------------------------------
	private Map<HMetricKey, HMetric> getLazyMetrics() {
		if (metrics == null) {
			final Map<HMetricKey, HMetricBuilder> metricBuilders = new HashMap<HMetricKey, HMetricBuilder>();
			for (final HCube cube : cubes) {
				for (final HMetric metric : cube.getMetrics()) {
					HMetricBuilder metricBuilder = metricBuilders.get(metric.getKey());
					if (metricBuilder == null) {
						metricBuilder = new HMetricBuilder(metric.getKey());
						metricBuilders.put(metric.getKey(), metricBuilder);
					}
					metricBuilder.withMetric(metric);
				}
			}
			metrics = new HashMap<HMetricKey, HMetric>();
			for (final Entry<HMetricKey, HMetricBuilder> entry : metricBuilders.entrySet()) {
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

	public List<HPoint> getPoints(final HMetricKey metricKey) {
		final List<HPoint> points = new ArrayList<HPoint>();
		for (final HCube cube : cubes){
			points.add(new HPoint() {
				/** {@inheritDoc} */
				public HMetric getMetric() {
					return cube.getMetric(metricKey);
				}

				/** {@inheritDoc} */
				public Date getDate() {
					return cube.getKey().getTime().getValue();
				}
			});
		}
		return Collections.unmodifiableList(points);
	}
}
