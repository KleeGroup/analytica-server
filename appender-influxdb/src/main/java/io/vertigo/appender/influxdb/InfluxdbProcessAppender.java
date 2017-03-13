package io.vertigo.appender.influxdb;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class InfluxdbProcessAppender extends AppenderSkeleton {

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
				final LogMessage logMessage = GSON.fromJson((String) event.getMessage(), LogMessage.class);
				final InfluxDB db = getDB();
				if (!db.describeDatabases().contains(logMessage.appName)) {
					db.createDatabase(logMessage.appName);
				}
				db.write(logMessage.appName, "autogen", processToPoint(logMessage.event, logMessage.host));
			} catch (final JsonSyntaxException e) {
				// it wasn't a message for us so we do nothing
			}
		}

	}

	private Point processToPoint(final AProcess process, final String host) {
		final Map measures = process.getMeasures();
		final VisitState state = new VisitState();
		flatProcess(process, state);

		final Map<String, Object> countFields = state.getCountsByCategory().entrySet().stream()
				.collect(Collectors.toMap((entry) -> entry.getKey() + "_count", (entry) -> entry.getValue()));
		final Map<String, Object> durationFields = state.getDurationsByCategory().entrySet().stream()
				.collect(Collectors.toMap((entry) -> entry.getKey() + "_duration", (entry) -> entry.getValue()));

		return Point.measurement(process.getCategory())
				.time(process.getStart(), TimeUnit.MILLISECONDS)
				.tag(TAG_NAME, process.getName())
				.tag(TAG_LOCATION, host)
				.tag(process.getTags())
				.addField("duration", process.getDurationMillis())
				.addField("subprocesses", process.getSubProcesses().size())
				.addField("name", process.getName())
				.fields(countFields)
				.fields(durationFields)
				.build();
	}

	private void flatProcess(final AProcess process, final VisitState visitState) {
		process.getSubProcesses().stream()
				.forEach(subProcess -> {
					visitState.push(subProcess);
					//on descend => stack.push
					flatProcess(subProcess, visitState);
					//on remonte => stack.poll
					visitState.pop();
				});

	}

	class VisitState {
		private final Map<String, Integer> countsByCategory = new HashMap<>();
		private final Map<String, Long> durationsByCategory = new HashMap<>();
		private final Stack<String> stack = new Stack<>();

		void push(final AProcess process) {
			if (!stack.contains(process.getCategory())) {
				incDurations(process.getCategory(), process.getDurationMillis());
			}
			incCounts(process.getCategory());
			stack.push(process.getCategory());

		}

		void pop() {
			stack.pop();
		}

		private void incDurations(final String category, final Long duration) {
			final Long existing = durationsByCategory.get(category);
			if (existing == null) {
				durationsByCategory.put(category, duration);
			} else {
				durationsByCategory.put(category, existing + duration);
			}
		}

		private void incCounts(final String category) {
			final Integer existing = countsByCategory.get(category);
			if (existing == null) {
				countsByCategory.put(category, 1);
			} else {
				countsByCategory.put(category, existing + 1);
			}
		}

		Map<String, Integer> getCountsByCategory() {
			return countsByCategory;
		}

		Map<String, Long> getDurationsByCategory() {
			return durationsByCategory;
		}

	}

	private InfluxDB getDB() {
		if (influxDB == null) {
			influxDB = InfluxDBFactory.connect(getServerUrl(), getLogin(), getPassword());
		}
		return influxDB;
	}

	private static boolean ping(final String host) {
		try {
			final InetAddress inet = InetAddress.getByName(host);
			return inet.getAddress() != null;
		} catch (final IOException e) {
			return false;
		}
	}

	private final class LogMessage {
		String appName;
		String host;
		AProcess event;

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
