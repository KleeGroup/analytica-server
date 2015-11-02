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
import io.analytica.server.store.Identified;
import io.analytica.server.store.ProcessStorePlugin;
import io.vertigo.lang.Activeable;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.sleepycat.je.DatabaseEntry;

/**
 * Stockage des Process dans une base Berkeley.
 * @author npiedeloup
 * @version $Id: $
 */
public final class BerkeleyProcessStorePlugin implements ProcessStorePlugin, Activeable {
	private static final Logger LOG = Logger.getLogger(BerkeleyProcessStorePlugin.class);
	private final File dbPath;
	private final Map<String, BerkeleyDatabase> databases;
	private final BerkeleyDatabaseWriter writer;

	/**
	 * @param dbPath Chemin de stockage de la base berkeley
	 */
	@Inject
	public BerkeleyProcessStorePlugin(@Named("dbPath") final String dbPath) {
		System.out.println("Trying to start BerkleyDB at the path " + dbPath);
		this.dbPath = new File(dbPath);
		databases = new HashMap<>();
		final String[] currentApps = this.dbPath.list(new FilenameFilter() {
			@Override
			public boolean accept(final File current, final String name) {
				return new File(current, name).isDirectory();
			}
		});
		if(currentApps!=null){
			for (final String appName : currentApps) {
				final BerkeleyDatabase database = new BerkeleyDatabase(new File(this.dbPath.getAbsolutePath() + File.separator + appName));
				database.open(false);
				databases.put(appName, database);
			}
		}
		writer = new BerkeleyDatabaseWriter();
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getApps() {
		return new ArrayList<>(databases.keySet());
	}

	/** {@inheritDoc} */
	@Override
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
	@Override
	public void start() {
		//database.open(false);
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		for (final BerkeleyDatabase database : databases.values()) {
			database.close();
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<Identified<KProcess>> getProcess(final String appName, final String lastKeyString, final Integer maxRow) {
		final List<String> systemNames = new ArrayList<>();
		final List<Identified<KProcess>> processes = new ArrayList<>();

		for (final File databaseDirectory : dbPath.listFiles(new FileFilter() {
			@Override
			public boolean accept(final File pathname) {
				return pathname.isDirectory();
			}
		})) {
			systemNames.add(databaseDirectory.getName());
		}
		final String systemName = appName;
		final DatabaseEntry lastKey = lastKeyString != null ? writer.writeKey(Long.parseLong(lastKeyString.split("-")[1])) : new DatabaseEntry();

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
