package io.vertigo.appender.influxdb.health;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class InfluxdbHealthAppender extends AppenderSkeleton {

	//Appender params
	private String serverUrl;
	private String login;
	private String password;

	private static final Gson GSON = new GsonBuilder().create();

	public static final String TAG_NAME = "name";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_TIME = "time";
	public static final String NO_CATEGORY = null;

	private InfluxDB influxDB;

	@Override
	public void close() {
		if (influxDB != null) {
			influxDB.close();
		}

	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(final LoggingEvent event) {

		if (event.getMessage() instanceof String) {
			try {
				final LogMessageHealth logMessage = GSON.fromJson((String) event.getMessage(), LogMessageHealth.class);
				final InfluxDB db = getDB();
				if (!db.describeDatabases().contains(logMessage.appName)) {
					db.createDatabase(logMessage.appName);
				}
				db.write(logMessage.appName, "autogen", healthToPoint(logMessage.event, logMessage.host));
			} catch (final JsonSyntaxException e) {
				// it wasn't a message for us so we do nothing
			}
		}

	}

	private static Point healthToPoint(final HealthCheck healthCheck, final String host) {

		return Point.measurement("healthcheck")
				.time(healthCheck.getCheckInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
				.addField("location", host)
				.addField("name", healthCheck.getName())
				.addField("checker", healthCheck.getChecker())
				.addField("feature", healthCheck.getFeature())
				.addField("topic", healthCheck.getTopic())
				.addField("status", healthCheck.getMeasure().getStatus().name())
				.addField("message", healthCheck.getMeasure().getMessage())
				.tag("location", host)
				.tag("name", healthCheck.getName())
				.tag("checker", healthCheck.getChecker())
				.tag("feature", healthCheck.getFeature())
				.tag("topic", healthCheck.getTopic())
				.tag("status", healthCheck.getMeasure().getStatus().name())
				.tag("message", healthCheck.getMeasure().getMessage())
				.build();
	}

	private InfluxDB getDB() {
		if (influxDB == null) {
			influxDB = InfluxDBFactory.connect(getServerUrl(), getLogin(), getPassword());
		}
		return influxDB;
	}

	private final class LogMessageHealth {
		String appName;
		String host;
		HealthCheck event;

	}

	// getters setters (log4j way)

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(final String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

}
