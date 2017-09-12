package io.vertigo.appender.influxdb.health;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;

import io.vertigo.appender.influxdb.AbstractInfluxdbAppender;

public class InfluxdbHealthAppender extends AbstractInfluxdbAppender<HealthCheck> {

	@Override
	protected List<Point> eventToPoints(final HealthCheck healthCheck, final String host) {

		final String message = healthCheck.getMeasure().getMessage();
		final String messageToStore = message != null ? message : "";

		return Collections.singletonList(Point.measurement("healthcheck")
				.time(healthCheck.getCheckInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
				.addField("location", host)
				.addField("name", healthCheck.getName())
				.addField("checker", healthCheck.getChecker())
				.addField("feature", healthCheck.getFeature())
				.addField("topic", healthCheck.getTopic())
				.addField("status", healthCheck.getMeasure().getStatus().getNumericValue())
				.addField("message", messageToStore)
				.tag("location", host)
				.tag("name", healthCheck.getName())
				.tag("checker", healthCheck.getChecker())
				.tag("feature", healthCheck.getFeature())
				.tag("topic", healthCheck.getTopic())
				.tag("status", String.valueOf(healthCheck.getMeasure().getStatus().getNumericValue()))
				.build());
	}

	@Override
	protected Type getEventType() {
		return HealthCheck.class;
	}

}
