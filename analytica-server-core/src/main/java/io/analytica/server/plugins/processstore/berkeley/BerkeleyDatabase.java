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

import io.vertigo.kernel.lang.Assertion;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;
import com.sleepycat.je.Transaction;

/**
 * @author pchretien
 * @version $Id: BerkeleyDatabase.java,v 1.3 2012/04/06 16:08:34 npiedeloup Exp $
 */
final class BerkeleyDatabase {
	private final File myDbEnvPath;
	private Environment myEnv;
	private Database db;
	private static final boolean USE_INDEXES = false;
	private final Map<Indexes, SecondaryDatabase> indexMap = new HashMap<>();
	private Database sequenceDb;
	private Sequence sequence;

	public enum Indexes {
		//noIndex
	}

	/**
	 * Constructeur.
	 */
	BerkeleyDatabase(final File myDbEnvPath) {
		Assertion.checkNotNull(myDbEnvPath);
		//----------------------------------------------------------------------
		this.myDbEnvPath = myDbEnvPath;
	}

	long count() {
		try {
			return db.count();
		} catch (final DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	void put(final DatabaseEntry key, final DatabaseEntry data) {
		try {
			final Transaction transaction = createTransaction();
			boolean committed = false;
			try {
				final OperationStatus status = db.put(transaction, key, data);
				if (!OperationStatus.SUCCESS.equals(status)) {
					throw new DatabaseException("la sauvegarde a échouée");
				}
				transaction.commit();
				committed = true;
			} finally {
				if (!committed) {
					transaction.abort();
				}
			}
		} catch (final DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	//	private Cursor openCursor() {
	//		try {
	//			System.out.println(">>> Open cursor : " + db.count() + " élements");
	//			return db.openCursor(null, null); //pas de TX=readOnly et cursorConfig default
	//		} catch (final DatabaseException e) {
	//			throw new KRuntimeException(e);
	//		}
	//	}
	//
	//	private void closeCursor(final Cursor cursor) {
	//		try {
	//			cursor.close();
	//		} catch (final DatabaseException e) {
	//			throw new KRuntimeException(e);
	//		}
	//	}

	Map<DatabaseEntry, DatabaseEntry> next(final DatabaseEntry lastKey, final Integer maxRow) {
		Assertion.checkNotNull(maxRow);
		Assertion.checkArgument(maxRow >= 1, "MaxRow doit être strictement positif");
		//---------------------------------------------------------------------
		try {
			final Map<DatabaseEntry, DatabaseEntry> result = new LinkedHashMap<>(maxRow);
			// Open a cursor using a database handle
			final Cursor cursor = db.openCursor(null, null); //pas de TX=readOnly et cursorConfig default
			try {
				final DatabaseEntry theKey;
				final DatabaseEntry theData = new DatabaseEntry();

				if (lastKey.getSize() == 0) { //si lastKey est null, on veut le premier élément
					theKey = new DatabaseEntry();
				} else {
					//Si on a dejà un lastKey, on repositionne le cursor dessus, puis on fait next()
					theKey = new DatabaseEntry(lastKey.getData());
					final OperationStatus status = cursor.getSearchKey(theKey, theData, null);
					Assertion.checkState(OperationStatus.SUCCESS.equals(status), "L'ancien document n'a pas été retrouvé : {0}", lastKey);
				}
				while (result.size() < maxRow && cursor.getNext(theKey, theData, null).equals(OperationStatus.SUCCESS)) {
					result.put(new DatabaseEntry(theKey.getData()), new DatabaseEntry(theData.getData()));
				}
				return result; //plus de suivant
			} finally {
				cursor.close();
			}
		} catch (final DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	private Transaction createTransaction() throws DatabaseException {
		return db.getEnvironment().beginTransaction(null, null);
	}

	public long getSequenceNextVal() {
		try {
			return sequence.get(null, 1);
		} catch (final DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	DatabaseEntry get(final DatabaseEntry theKey) {
		try {
			final DatabaseEntry theData = new DatabaseEntry();
			final OperationStatus status = db.get(null, theKey, theData, null); //pas de TX=readOnly et cursorConfig default
			if (OperationStatus.SUCCESS.equals(status)) {
				return theData;
			}
			return null; //pas trouvé
		} catch (final DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param indexKey Clé de l'index
	 * @param index Index à utiliser
	 * @return Données de la dataBase récupérer par l'index
	 */
	DatabaseEntry getByIndex(final DatabaseEntry indexKey, final Indexes index) {
		if (USE_INDEXES) {
			try {
				final DatabaseEntry theData = new DatabaseEntry();
				final OperationStatus status = indexMap.get(index).get(null, indexKey, theData, null); //pas de TX=readOnly et cursorConfig default
				if (OperationStatus.SUCCESS.equals(status)) {
					return theData;
				}
			} catch (final DatabaseException e) {
				throw new RuntimeException(e);
			}
		}
		//Pas trouvé
		return null;
	}

	void open(final boolean readOnly) {
		try {
			final EnvironmentConfig myEnvConfig = new EnvironmentConfig();
			final DatabaseConfig myDbConfig = new DatabaseConfig();
			final SequenceConfig mySequenceConfig = new SequenceConfig();

			// If the environment is read-only, then
			// make the databases read-only too.
			myEnvConfig.setReadOnly(readOnly);
			myDbConfig.setReadOnly(readOnly);

			// If the environment is opened for write, then we want to be
			// able to create the environment and databases if
			// they do not exist.
			myEnvConfig.setAllowCreate(!readOnly);
			myDbConfig.setAllowCreate(!readOnly);

			// Allow transactions if we are writing to the database
			myEnvConfig.setTransactional(!readOnly);
			myDbConfig.setTransactional(!readOnly);

			//On limite l'utilisation du cache à 20% de la mémoire globale.
			myEnvConfig.setCachePercent(20);
			//CHECKME On limite l'utilisation du cache à 200Mo
			//myEnvConfig.setCacheSize(200 * 1000 * 1000);

			// Open the environment
			myEnv = new Environment(myDbEnvPath, myEnvConfig);

			// Now open, or create and open, our databases
			// Open the vendors and inventory databases
			db = myEnv.openDatabase(null, "MyDB", myDbConfig);

			mySequenceConfig.setAllowCreate(!readOnly);
			final EntryBinding sequenceBinding = TupleBinding.getPrimitiveBinding(String.class);
			final DatabaseEntry sequenceName = new DatabaseEntry();
			sequenceBinding.objectToEntry("MySequence", sequenceName);
			sequenceDb = myEnv.openDatabase(null, "MySequenceDB", myDbConfig);
			sequence = sequenceDb.openSequence(null, sequenceName, mySequenceConfig);

		} catch (final DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	void close() {
		if (myEnv != null) {
			try {
				db.close();
				sequenceDb.close();
				for (final SecondaryDatabase index : indexMap.values()) {
					index.close();
				}
				// Finally, close the environment.
				myEnv.close();
			} catch (final DatabaseException dbe) {
				System.err.println("Error closing MyDbEnv: " + dbe.toString());
				System.exit(-1);
			}
		}
	}
}
