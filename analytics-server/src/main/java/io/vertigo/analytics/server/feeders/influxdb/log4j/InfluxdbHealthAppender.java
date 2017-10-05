package io.vertigo.analytics.server.feeders.influxdb.log4j;

import java.lang.reflect.Type;
import java.util.List;

import org.influxdb.dto.Point;

import io.vertigo.analytics.server.events.health.HealthCheck;
import io.vertigo.analytics.server.feeders.influxdb.InfluxdbUtil;

public class InfluxdbHealthAppender extends AbstractInfluxdbAppender<HealthCheck> {

	@Override
	protected List<Point> eventToPoints(final HealthCheck healthCheck, final String host) {
		return InfluxdbUtil.heathCheckToPoints(healthCheck, host);
	}

	@Override
	protected Type getEventType() {
		return HealthCheck.class;
	}

}
