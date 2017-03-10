/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>
 */
/*
 * Client.java - JMX client that interacts with the JMX agent. It gets
 * attributes and performs operations on the Hello MBean and the QueueSampler
 * MXBean example. It also listens for Hello MBean notifications.
 */

package io.analytica.uiswing;

import io.analytica.uiswing.collector.PerfCollector;

import javax.management.AttributeChangeNotification;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Client {

	/**
	 * Inner class that will handle the notifications.
	 */
	public static class ClientListener implements NotificationListener {
		@Override
		public void handleNotification(final Notification notification, final Object handback) {
			echo("\nReceived notification:");
			echo("\tClassName: " + notification.getClass().getName());
			echo("\tSource: " + notification.getSource());
			echo("\tType: " + notification.getType());
			echo("\tMessage: " + notification.getMessage());
			if (notification instanceof AttributeChangeNotification) {
				final AttributeChangeNotification acn = (AttributeChangeNotification) notification;
				echo("\tAttributeName: " + acn.getAttributeName());
				echo("\tAttributeType: " + acn.getAttributeType());
				echo("\tNewValue: " + acn.getNewValue());
				echo("\tOldValue: " + acn.getOldValue());
			}
		}
	}

	/* For simplicity, we declare "throws Exception".
	   Real programs will usually want finer-grained exception handling. */
	public static void main(final String[] args) throws Exception {
		// Create an RMI connector client and
		// connect it to the RMI connector server
		//
		echo("\nCreate an RMI connector client and " + "connect it to the RMI connector server");
		final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:3334" + "/jmxrmi");
		final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

		final MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		final ObjectName mbeanName = new ObjectName("kanap:name=PerfCollector");
		final PerfCollector mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, PerfCollector.class, true);
		echo("\ngetResults = " + mbeanProxy.getResults());
		jmxc.close();
	}

	private static void echo(final String msg) {
		System.out.println(msg);
	}
	//
	//	private static void sleep(final int millis) {
	//		try {
	//			Thread.sleep(millis);
	//		} catch (final InterruptedException e) {
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	private static void waitForEnterPressed() {
	//		try {
	//			echo("\nPress <Enter> to continue...");
	//			System.in.read();
	//		} catch (final IOException e) {
	//			e.printStackTrace();
	//		}
	//	}
}
