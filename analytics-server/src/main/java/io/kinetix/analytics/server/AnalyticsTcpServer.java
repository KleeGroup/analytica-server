package io.kinetix.analytics.server;

import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AnalyticsTcpServer  {

	public static final Logger LOG = LogManager.getLogger(AnalyticsTcpServer.class);

	public void start(int port) {

		try (ServerSocket serverSocket = new ServerSocket(port)) {

			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new ServerHandler(socket)).start();
			}

		} catch(Exception e) {
			LOG.error("Caught Exception. Closing server.", e);
		}
	}

}