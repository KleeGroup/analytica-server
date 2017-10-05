package io.vertigo.analytics.server.feeders.influxdb.log4j;

import java.lang.reflect.Type;
import java.util.List;

import org.influxdb.dto.Point;

import io.vertigo.analytics.server.events.metric.Metric;
import io.vertigo.analytics.server.feeders.influxdb.InfluxdbUtil;

public class InfluxdbMetricAppender extends AbstractInfluxdbAppender<Metric> {

	@Override
	protected List<Point> eventToPoints(final Metric metric, final String host) {
		return InfluxdbUtil.metricToPoints(metric, host);
	}

	@Override
	protected Type getEventType() {
		return Metric.class;
	}

}
