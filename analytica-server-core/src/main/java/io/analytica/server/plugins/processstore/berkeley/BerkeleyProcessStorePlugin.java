/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.server.plugins.processstore.berkeley;

import io.analytica.api.KProcess;
import io.analytica.server.impl.Identified;
import io.analytica.server.impl.ProcessStorePlugin;
import io.vertigo.kernel.lang.Activeable;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.sleepycat.je.DatabaseEntry;

/**
 * Stockage des Process dans une base Berkeley.
 * @author npiedeloup
 * @version $Id: $
 */
public final class BerkeleyProcessStorePlugin implements ProcessStorePlugin, Activeable {
	private final File dbPath;
	private final Map<String, BerkeleyDatabase> databases;
	private final BerkeleyDatabaseWriter writer;

	/**
	 * @param dbPath Chemin de stockage de la base berkeley
	 */
	@Inject
	public BerkeleyProcessStorePlugin(@Named("dbPath") final String dbPath) {
		this.dbPath = new File(dbPath);
		databases = new HashMap<>();
		//database = new BerkeleyDatabase(new File(dbPath));
		writer = new BerkeleyDatabaseWriter();
	}

	/** {@inheritDoc} */
	public void add(final KProcess process) {
		final BerkeleyDatabase database = obtainDatabase(process.getAppName());
		final long key = database.getSequenceNextVal();
		final DatabaseEntry dataKey = writer.writeKey(key);
		final DatabaseEntry dataProcess = writer.writeProcess(process);
		database.put(dataKey, dataProcess);
	}

	private BerkeleyDatabase obtainDatabase(final String systemName) {
		synchronized (databases) {
			BerkeleyDatabase database = databases.get(systemName);
			if (database == null) {
				database = new BerkeleyDatabase(new File(dbPath.getAbsolutePath() + File.separator + systemName));
				database.open(false);
				databases.put(systemName, database);
			}
			return database;
		}
	}

	/** {@inheritDoc} */
	public void start() {
		//database.open(false);
	}

	/** {@inheritDoc} */
	public void stop() {
		for (final BerkeleyDatabase database : databases.values()) {
			database.close();
		}
	}

	/** {@inheritDoc} */
	public List<Identified<KProcess>> getProcess(final String lastKeyString, final Integer maxRow) {
		final List<String> systemNames = new ArrayList<>();
		for (final File databaseDirectory : dbPath.listFiles(new FileFilter() {
			public boolean accept(final File pathname) {
				return pathname.isDirectory();
			}
		})) {
			systemNames.add(databaseDirectory.getName());
		}
		final String systemName = lastKeyString != null ? lastKeyString.split("-")[0] : systemNames.get(0);
		final DatabaseEntry lastKey = lastKeyString != null ? writer.writeKey(Long.parseLong(lastKeyString.split("-")[1])) : new DatabaseEntry();
		final List<Identified<KProcess>> processes = new ArrayList<>();
		final BerkeleyDatabase database = obtainDatabase(systemName);
		final Map<DatabaseEntry, DatabaseEntry> entries = database.next(lastKey, maxRow);
		for (final Map.Entry<DatabaseEntry, DatabaseEntry> entry : entries.entrySet()) {
			final long key = writer.readKey(entry.getKey());
			final KProcess process = writer.readProcess(entry.getValue());
			processes.add(new Identified(systemName + "-" + key, process));
		}
		return processes;
	}

}
