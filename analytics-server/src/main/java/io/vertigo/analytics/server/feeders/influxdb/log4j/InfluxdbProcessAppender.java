package io.vertigo.analytics.server.feeders.influxdb.log4j;

import java.lang.reflect.Type;
import java.util.List;

import org.influxdb.dto.Point;

import io.vertigo.analytics.server.events.process.AProcess;
import io.vertigo.analytics.server.feeders.influxdb.InfluxdbUtil;

public class InfluxdbProcessAppender extends AbstractInfluxdbAppender<AProcess> {

	@Override
	protected List<Point> eventToPoints(final AProcess process, final String host) {
		return InfluxdbUtil.processToPoints(process, host);
	}

	@Override
	protected Type getEventType() {
		return AProcess.class;
	}

}
