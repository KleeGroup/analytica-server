package io.vertigo.analytics.server.feeders.influxdb.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import io.vertigo.analytics.server.LogMessage;
import io.vertigo.analytics.server.events.process.AProcess;
import spark.Spark;

public class HttpProcessInflux {

	private static final Gson gson = new Gson();

	private static final Logger logger = LogManager.getLogger(HttpProcessInflux.class);

	public static void start() {
		Spark.port(8080);
		final Type logMessageType = new ParameterizedType() {

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
				return new Type[] { AProcess.class };
			}
		};

		Spark.post("/process/_send", (req, res) -> {
			try {
				gson.fromJson(req.body(), logMessageType);
			} catch (final Exception e) {
				res.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return e.getMessage();
			}
			logger.info(req.body()); // re route to existing appender
			res.status(HttpServletResponse.SC_NO_CONTENT);
			return "";
		});

	}

}
