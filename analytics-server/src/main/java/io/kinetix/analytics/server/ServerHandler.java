package io.kinetix.analytics.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.kinetix.analytics.server.log4net.LoggingEventData;

public class ServerHandler implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger(ServerHandler.class);
	private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
	
	private Socket socket;
	private LoggerContext context;
	
	
	public ServerHandler(Socket s) {
		socket = s;
		context = LoggerContext.getContext(false);
	}

	@Override
	public void run() {
		try (InputStream sis = socket.getInputStream();
			 InputStreamReader isr = new InputStreamReader(sis);
			 BufferedReader br = new BufferedReader(isr)) {
			
			String event = br.readLine();
			LoggingEventData logEvent = GSON.fromJson(event, LoggingEventData.class);
			
			Logger remoteLogger = context.getLogger(logEvent.getLoggerName());
	        remoteLogger.info(logEvent.getMessage());
	        
		} catch (IOException e) {
			LOGGER.error("Caught java.io.EOFException closing connection");
		}
	}
	
}