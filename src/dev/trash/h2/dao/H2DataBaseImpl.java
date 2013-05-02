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
package com.kleegroup.analyticaimpl.server.plugins.cubestore.h2.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

/**
 * @author npiedeloup
 * @version $Id: H2DataBaseImpl.java,v 1.3 2012/04/17 09:11:28 pchretien Exp $
 */
public class H2DataBaseImpl implements H2DataBase {

	private final String dbUrl;
	private final String dbLogin;
	private final String dbPassword;
	private final JdbcConnectionPool connectionPool;

	public H2DataBaseImpl(final String dbUrl, final String dbLogin, final String dbPassword) {
		assert dbUrl != null;
		assert dbLogin != null;
		assert dbPassword != null;
		//---------------------------------------------------------------------
		this.dbUrl = dbUrl.contains("IFEXISTS=TRUE") ? dbUrl : dbUrl + ";IFEXISTS=TRUE";
		this.dbLogin = dbLogin;
		this.dbPassword = dbPassword;
		//---------------------------------------------------------------------
		org.h2.Driver.load();
		connectionPool = JdbcConnectionPool.create(this.dbUrl, this.dbLogin, this.dbPassword);
	}

	public boolean isBddCreated() {
		try {
			final Connection conn = DriverManager.getConnection(dbUrl, dbLogin, dbPassword);
			conn.close();
			return true;
		} catch (final SQLException e) {
			return false;
		}
	}

	public void createDb() throws DaoException {
		try {
			final String createUrl = dbUrl.replaceAll(";IFEXISTS=TRUE", "");
			final Connection conn = DriverManager.getConnection(createUrl, dbLogin, dbPassword);
			conn.close();
		} catch (final SQLException e) {
			throw new DaoException("Erreur de creation de la base", e);
		}

	}

	public Connection getConnection() throws DaoException {
		try {
			final Connection connection = connectionPool.getConnection();
			if (connection.getAutoCommit()) {
				connection.setAutoCommit(false);
			}
			return connection;
		} catch (final SQLException e) {
			throw new DaoException("Erreur de creation de la connection", e);
		}
	}

	public void close() {
		connectionPool.dispose();
	}

	public void commit(final Connection connection) throws DaoException {
		try {
			connection.commit();
		} catch (final SQLException e) {
			throw new DaoException("Erreur de commit de la connection", e);
		}
	}

	public void close(final Connection connection) throws DaoException {
		try {
			connection.rollback();
		} catch (final SQLException e) {
			System.err.println("Erreur de rollback de la connection");
			e.printStackTrace(System.err);
			//throw new DaoException("Erreur de rollback de la connection", e);
		}
		try {
			connection.close();
		} catch (final SQLException e) {
			throw new DaoException("Erreur de fermeture de la connection", e);
		}
	}

	public void dispose() throws DaoException {
		connectionPool.dispose();
	}
}
