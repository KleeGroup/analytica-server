/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidi�re - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package com.kleegroup.analyticaimpl.hcube.plugins.memorystack;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import vertigo.kernel.lang.Activeable;

import com.google.gson.Gson;
import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analyticaimpl.hcube.ProcessStatsPlugin;

/**
 * Stockage des process, et conservation statistique de l'arbre.
 * 
 * Transformation d'un Process constitu� de sous-process.
 * Chaque Process (et donc sous process) est transform� en Cube avec :
 * - une agregation des mesures de ce process
 * - une agregation des mesures des sous process 
 * 
 * 
 * @author npiedeloup
 * @version $Id: StandardProcessEncoderPlugin.java,v 1.16 2012/10/16 17:27:12 pchretien Exp $
 */
public final class MemoryStackProcessStatsPlugin implements ProcessStatsPlugin, Activeable, LastProcessMXBean {
	private final LimitedDelayQueue processQueue = new LimitedDelayQueue(24 * 60); //24h

	public void merge(final KProcess process) {
		processQueue.add(process);
	}

	/** {@inheritDoc} */
	public void start() {
		try {
			// Get the Platform MBean Server
			final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			// Construct the ObjectName for the processStorePlugin MBean we will register
			final ObjectName mbeanName = new ObjectName("analytica:type=LastProcessMXBean");
			// Register the processStorePlugin MBean
			mbs.registerMBean(this, mbeanName);
			System.out.println(mbeanName.getCanonicalName() + " : " + mbs.isRegistered(mbeanName));
		} catch (final Throwable th) {
			throw new RuntimeException("Erreur de publication JMX du LastProcessMXBean", th);
		}
	}

	/** {@inheritDoc} */
	public void stop() {
		try {
			// Get the Platform MBean Server
			final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			// Construct the ObjectName for the processStorePlugin MBean we will register
			final ObjectName mbeanName = new ObjectName("analytica:type=LastProcessMXBean");
			// Unregister the processStorePlugin MBean
			mbs.unregisterMBean(mbeanName);
		} catch (final Throwable th) {
			throw new RuntimeException("Erreur de d�publication JMX du LastProcessMXBean", th);
		}
	}

	/** {@inheritDoc} */
	public String getLastProcessesJson() {
		final List<KProcess> processes = new ArrayList<KProcess>(processQueue);
		return new Gson().toJson(processes);
	}

	/**
	 * Liste de process limit� en dur�e, seul les plus r�cents sont gard�s. 
	 * @author npiedeloup
	 * @version $Id: $
	 */
	private static class LimitedDelayQueue extends LinkedList<KProcess> {

		private static final long serialVersionUID = -6085444623815188157L;
		private final int delayMinute;

		public LimitedDelayQueue(final int delayMinute) {
			this.delayMinute = delayMinute;
		}

		/** {@inheritDoc} */
		@Override
		public boolean add(final KProcess o) {
			return super.add(o);
			//			final Date limit = new Date(System.currentTimeMillis() - delayMinute * 60 * 1000);
			//			while (!isEmpty() && getFirst().getStartDate().before(limit)) {
			//				super.remove();
			//			}
			//			return true;
		}
	}
}
