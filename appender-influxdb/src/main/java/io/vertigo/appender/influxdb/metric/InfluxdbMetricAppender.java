package io.vertigo.appender.influxdb.metric;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;

import io.vertigo.appender.influxdb.AbstractInfluxdbAppender;

public class InfluxdbMetricAppender extends AbstractInfluxdbAppender<Metric> {

	@Override
	protected Point eventToPoint(final Metric metric, final String host) {

		return Point.measurement("metric")
				.time(metric.getMeasureInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
				.addField("location", host)
				.addField("name", metric.getName())
				.addField("topic", metric.getTopic())
				.addField("value", metric.getValue())
				.tag("location", host)
				.tag("name", metric.getName())
				.tag("topic", metric.getTopic())
				.tag("value", String.valueOf(metric.getValue()))
				.build();
	}

	@Override
	protected Type getEventType() {
		return Metric.class;
	}

}
