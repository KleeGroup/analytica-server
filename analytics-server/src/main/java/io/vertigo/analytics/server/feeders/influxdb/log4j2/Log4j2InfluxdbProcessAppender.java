package io.vertigo.analytics.server.feeders.influxdb.log4j2;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.influxdb.dto.Point;

import io.vertigo.analytics.server.events.process.AProcess;
import io.vertigo.analytics.server.feeders.influxdb.InfluxdbUtil;

@Plugin(name = "InfluxdbProcess", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class Log4j2InfluxdbProcessAppender extends AbstractLog4j2InfluxdbAppender<AProcess> {

	private Log4j2InfluxdbProcessAppender(
			final String name,
			final Filter filter,
			final Configuration config,
			final String serverUrl,
			final String login,
			final String password) {
		super(name, filter, config, serverUrl, login, password);
	}

	@Override
	protected List<Point> eventToPoints(final AProcess process, final String host) {
		return InfluxdbUtil.processToPoints(process, host);
	}

	@Override
	protected Type getEventType() {
		return AProcess.class;
	}

	@PluginFactory
	public static Log4j2InfluxdbProcessAppender createAppender(
			@PluginAttribute("name") final String name,
			@PluginConfiguration final Configuration config,
			@PluginElement("Filter") final Filter filter,
			@PluginAttribute("serverUrl") final String serverUrl,
			@PluginAttribute("login") final String login,
			@PluginAttribute("password") final String password) {
		if (name == null) {
			LOGGER.error("A name for the Appender must be specified");
			return null;
		}
		return new Log4j2InfluxdbProcessAppender(name, filter, config, serverUrl, login, password);
	}
}
