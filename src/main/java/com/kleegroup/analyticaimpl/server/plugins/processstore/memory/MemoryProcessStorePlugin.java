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
package com.kleegroup.analyticaimpl.server.plugins.processstore.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analyticaimpl.server.Identified;
import com.kleegroup.analyticaimpl.server.ProcessStorePlugin;

/**
 * Impl�mentation m�moire du stockage des Process.
 * @author npiedeloup
 * @version $Id: MemoryProcessStorePlugin.java,v 1.4 2012/04/06 16:09:35 npiedeloup Exp $
 */
public final class MemoryProcessStorePlugin implements ProcessStorePlugin {
	private final Queue<Identified<KProcess>> processQueue = new ConcurrentLinkedQueue<Identified<KProcess>>();
	private long sequence = 0;

	/**
	 * Constructeur.
	 */
	public MemoryProcessStorePlugin() {
		super();
	}

	/** {@inheritDoc} */
	public void add(final KProcess process) {
		processQueue.add(new Identified<KProcess>(String.valueOf(sequence++), process));
	}

	/** {@inheritDoc} */
	public List<Identified<KProcess>> getProcess(final String lastKey, final Integer maxRow) {
		Assertion.notNull(maxRow);
		Assertion.precondition(maxRow >= 1, "MaxRow doit �tre strictement positif");
		//---------------------------------------------------------------------
		final List<Identified<KProcess>> processes = new ArrayList<Identified<KProcess>>();
		Identified<KProcess> process = processQueue.poll();
		while (process != null) {
			processes.add(process);
			if (processes.size() >= maxRow) {
				break;
			}
			process = processQueue.poll();
		}
		return processes;
	}
}
