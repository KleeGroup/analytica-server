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
package io.analytica.server.plugins.processstore.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.analytica.api.AProcess;
import io.analytica.server.store.Identified;
import io.analytica.server.store.ProcessStorePlugin;
import io.vertigo.lang.Assertion;

/**
 * Implémentation mémoire du stockage des Process.
 * @author npiedeloup
 * @version $Id: MemoryProcessStorePlugin.java,v 1.4 2012/04/06 16:09:35 npiedeloup Exp $
 */
public final class MemoryProcessStorePlugin implements ProcessStorePlugin {
	private final Queue<Identified<AProcess>> processQueue = new ConcurrentLinkedQueue<>();
	private long sequence = 0;

	/**
	 * Constructeur.
	 */
	public MemoryProcessStorePlugin() {
		super();
	}

	/** {@inheritDoc} */
	@Override
	public void add(final AProcess process) {
		processQueue.add(new Identified<>(String.valueOf(sequence++), process));
	}

	/** {@inheritDoc} */
	@Override
	public List<Identified<AProcess>> getProcess(final String appName, final String lastKey, final Integer maxRow) {
		Assertion.checkNotNull(maxRow);
		Assertion.checkArgument(maxRow >= 1, "MaxRow doit étre strictement positif");
		//---------------------------------------------------------------------
		final List<Identified<AProcess>> processes = new ArrayList<>();
		Identified<AProcess> process = processQueue.poll();
		while (process != null) {
			processes.add(process);
			if (processes.size() >= maxRow) {
				break;
			}
			process = processQueue.poll();
		}
		return processes;
	}

	@Override
	public List<String> getApps() {
		// TODO Auto-generated method stub
		return null;
	}
}
