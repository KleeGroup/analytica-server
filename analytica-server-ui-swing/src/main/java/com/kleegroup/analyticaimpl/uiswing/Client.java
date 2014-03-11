/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Client.java - JMX client that interacts with the JMX agent. It gets
 * attributes and performs operations on the Hello MBean and the QueueSampler
 * MXBean example. It also listens for Hello MBean notifications.
 */

package com.kleegroup.analyticaimpl.uiswing;

import java.io.IOException;

import javax.management.AttributeChangeNotification;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.kleegroup.analyticaimpl.uiswing.collector.PerfCollector;

public class Client {

	/**
	 * Inner class that will handle the notifications.
	 */
	public static class ClientListener implements NotificationListener {
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

	private static void sleep(final int millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void waitForEnterPressed() {
		try {
			echo("\nPress <Enter> to continue...");
			System.in.read();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
