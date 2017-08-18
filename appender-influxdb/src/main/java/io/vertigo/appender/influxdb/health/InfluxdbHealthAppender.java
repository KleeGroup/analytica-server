package io.vertigo.appender.influxdb.health;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;

import io.vertigo.appender.influxdb.AbstractInfluxdbAppender;

public class InfluxdbHealthAppender extends AbstractInfluxdbAppender<HealthCheck> {

	@Override
	protected Point eventToPoint(final HealthCheck healthCheck, final String host) {

		return Point.measurement("healthcheck")
				.time(healthCheck.getCheckInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
				.addField("location", host)
				.addField("name", healthCheck.getName())
				.addField("checker", healthCheck.getChecker())
				.addField("feature", healthCheck.getFeature())
				.addField("topic", healthCheck.getTopic())
				.addField("status", healthCheck.getMeasure().getStatus().getNumericValue())
				.addField("message", healthCheck.getMeasure().getMessage())
				.tag("location", host)
				.tag("name", healthCheck.getName())
				.tag("checker", healthCheck.getChecker())
				.tag("feature", healthCheck.getFeature())
				.tag("topic", healthCheck.getTopic())
				.tag("status", String.valueOf(healthCheck.getMeasure().getStatus().getNumericValue()))
				.tag("message", healthCheck.getMeasure().getMessage())
				.build();
	}

	@Override
	protected Type getEventType() {
		return HealthCheck.class;
	}

}
