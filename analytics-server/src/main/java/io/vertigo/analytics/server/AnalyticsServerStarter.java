package io.vertigo.analytics.server;

import java.io.IOException;

import org.apache.log4j.net.SimpleSocketServer;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.server.TcpSocketServer;

import io.kinetix.analytics.server.AnalyticsTcpServer;

/**
 * @author mlaroche
 *
 */
public class AnalyticsServerStarter {

	/**
	 * Args are by group of 3 ( type of server; port ; configUrl)
	 * @param args
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static void main(final String[] args) throws NumberFormatException, IOException {
		if (args.length == 0 && args.length % 3 != 0) {
			throw new RuntimeException("You must provide three params");
		}
		// all good
		for (int i = 0; i < (int) Math.floor(args.length / 3); i++) {
			final String port = args[i * 3 + 1];
			final String configFile = args[i * 3 + 2];
			switch (args[i * 3]) {
				case "log4j":
					new Thread(() -> SimpleSocketServer.main(new String[] { port, configFile })).start();
					break;
				case "log4j2":
					final TcpSocketServer javaSerializedTcpSocketServer = TcpSocketServer.createSerializedSocketServer(Integer.parseInt(port));
					Configurator.initialize("definedLog4jContext", AnalyticsServerStarter.class.getClassLoader(), configFile);
					javaSerializedTcpSocketServer.startNewThread().run();
					break;
				case "log4net":
					/*final TcpSocketServer jsonTcpSocketServer = TcpSocketServer.createJsonSocketServer(Integer.parseInt(args[i * 3 + 1]));
					Configurator.initialize("definedLog4jContext", AnalyticsServerStarter.class.getClassLoader(), args[i * 3 + 2]);
					jsonTcpSocketServer.startNewThread().run();*/

					Configurator.initialize("definedLog4netContext", AnalyticsServerStarter.class.getClassLoader(), configFile);
					AnalyticsTcpServer ats = new AnalyticsTcpServer();
					ats.start(Integer.parseInt(port));
					
					break;
				default:
					break;
			}
		}

	}

}
