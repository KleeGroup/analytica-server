package io.vertigo.analytics.server.feeders.influxdb.log4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import io.vertigo.analytics.server.LogMessage;

abstract class AbstractInfluxdbAppender<O> extends AppenderSkeleton {

	//Appender params
	private String serverUrl;
	private String login;
	private String password;

	private static final Gson GSON = new GsonBuilder().create();

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
				final LogMessage<O> logMessage = GSON.fromJson((String) event.getMessage(), getLogMessageType());
				final InfluxDB db = getDB();
				if (!db.describeDatabases().contains(logMessage.getAppName())) {
					db.createDatabase(logMessage.getAppName());
				}
				final BatchPoints.Builder batchPointsBuilder = BatchPoints.database(logMessage.getAppName())
						.retentionPolicy("autogen");
				eventToPoints(logMessage.getEvent(), logMessage.getHost())
						.forEach(batchPointsBuilder::point);
				db.write(batchPointsBuilder.build());
				//db.write(logMessage.getAppName(), "autogen", eventToPoints(logMessage.getEvent(), logMessage.getHost()));
			} catch (final JsonSyntaxException e) {
				// it wasn't a message for us so we do nothing
			} catch (final Exception e) {
				// for now we do nothing
				//LogLog.error("error writing log to influxdb", e); // if we want to log all errors occuring in the appender (might cause flooding of the logs)
				getErrorHandler().error("error writing log to influxdb", e, ErrorCode.WRITE_FAILURE);//by default the logger log only one error on the appender to avoid flooding. (better than nothing)
			}
		}

	}

	protected abstract List<Point> eventToPoints(final O healthCheck, final String host);

	protected abstract Type getEventType();

	private Type getLogMessageType() {
		return new ParameterizedType() {

			@Override
			public Type getRawType() {
				return LogMessage.class;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}

			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] { getEventType() };
			}
		};
	}

	private InfluxDB getDB() {
		if (influxDB == null) {
			influxDB = InfluxDBFactory.connect(getServerUrl(), getLogin(), getPassword());
		}
		return influxDB;
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
