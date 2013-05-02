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

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kasper.kernel.util.StringUtil;

/**
 * @author npiedeloup
 * @version $Id: SimpleDAO.java,v 1.4 2012/04/17 09:11:28 pchretien Exp $
 */
public class SimpleDAO {

	public static void executeSQL(final String sql, final Object dataBean, final Connection connection) throws DaoException {
		doExecuteSQL(sql, dataBean, connection);
	}

	public static <R extends Object> List<R> executeQueryList(final String sql, final Class<R> resultClass, final Connection connection) throws DaoException {
		return doExecuteSQL(sql, null, resultClass, connection);
	}

	public static <R extends Object> List<R> executeQueryList(final String sql, final Object dataBean, final Class<R> resultClass, final Connection connection) throws DaoException {
		return doExecuteSQL(sql, dataBean, resultClass, connection);
	}

	public static <R extends Object> R executeQuery(final String sql, final Class<R> resultClass, final Connection connection) throws DaoException {
		return executeQuery(sql, null, resultClass, connection);
	}

	public static <R extends Object> R executeQuery(final String sql, final Object dataBean, final Class<R> resultClass, final Connection connection) throws DaoException {
		final List<R> list = doExecuteSQL(sql, dataBean, resultClass, connection);
		assert list.size() > 0 : "Objet introuvable";
		assert list.size() == 1 : "La recherche retourne plus d'un élément";
		return list.get(0);
	}

	public static void executeSQL(final String sql, final Connection connection) throws DaoException {
		doExecuteSQL(sql, null, connection);
	}

	private static void doExecuteSQL(final String sql, final Object dataBean, final Connection connection) throws DaoException {
		try {
			final NamedPreparedStatement preparedSql = new NamedPreparedStatement(sql);
			final CallableStatement call = connection.prepareCall(preparedSql.getSql());
			if (dataBean != null) {
				preparedSql.setValues(call, dataBean);
			}
			call.execute();
			if (preparedSql.hasSequence()) {
				final ResultSet rs = call.getGeneratedKeys();
				if (rs.next()) {
					preparedSql.setSequence(rs.getLong(1), dataBean);
				}
			}

		} catch (final SQLException e) {
			throw new DaoException("Erreur de requete", e);
		}
	}

	private static <R extends Object> List<R> doExecuteSQL(final String sql, final Object dataBean, final Class<R> resultClass, final Connection connection) throws DaoException {
		try {
			final BeanWrapper<R> beanWrapper = new BeanWrapperImpl(resultClass);//incompatible avec le PlainBean à cause des types de champ
			final NamedPreparedStatement preparedSql = new NamedPreparedStatement(sql);
			final PreparedStatement preparedStatement = connection.prepareStatement(preparedSql.getSql());
			//LIMIT 1000 and OFFSET = 50
			//System.out.println(sql);
			if (dataBean != null) {
				preparedSql.setValues(preparedStatement, dataBean);
			}
			final ResultSet rs = preparedStatement.executeQuery();
			final List<R> resultList = new ArrayList<R>();
			while (rs.next()) {
				final R result = beanWrapper.newInstance();
				mapData(rs, result, beanWrapper);
				resultList.add(result);
			}
			return resultList;
		} catch (final SQLException e) {
			throw new DaoException("Erreur de requete", e);
		}
	}

	private static <R extends Object> void mapData(final ResultSet rs, final R result, final BeanWrapper<R> beanWrapper) throws SQLException, DaoException {
		final ResultSetMetaData rsmd = rs.getMetaData();
		for (int col = 1; col <= rsmd.getColumnCount(); col++) {
			final String columnName = rsmd.getColumnLabel(col);//label = alias, name= true column name
			final String fieldName = StringUtil.constToCamelCase(columnName, false);
			final Object value = getValueForResultSet(rs, col, beanWrapper.getType(fieldName));
			beanWrapper.set(result, fieldName, value);
		}
	}

	private static Object getValueForResultSet(final ResultSet rs, final int col, final Class<?> valueClass) throws SQLException, DaoException {
		final Object value;
		if (String.class.equals(valueClass)) {
			if (rs.getMetaData().getColumnType(col) == Types.CLOB) {
				final Clob clob = rs.getClob(col);
				final Long len = clob.length();
				value = clob.getSubString(1L, len.intValue());
			} else {
				//Si la valeur est null rs renvoie bien null
				value = rs.getString(col);
			}
		} else if (Integer.class.equals(valueClass) || int.class.equals(valueClass)) {
			final int vi = rs.getInt(col);
			value = rs.wasNull() ? null : vi;
		} else if (Long.class.equals(valueClass) || long.class.equals(valueClass)) {
			final long vl = rs.getLong(col);
			value = rs.wasNull() ? null : vl;
		} else if (Boolean.class.equals(valueClass) || boolean.class.equals(valueClass)) {
			final int vb = rs.getInt(col);
			value = rs.wasNull() ? null : vb != 0 ? Boolean.TRUE : Boolean.FALSE;
		} else if (Double.class.equals(valueClass) || double.class.equals(valueClass)) {
			final double vd = rs.getDouble(col);
			value = rs.wasNull() ? null : vd;
		} else if (BigDecimal.class.equals(valueClass)) {
			//Si la valeur est null rs renvoie bien null
			value = rs.getBigDecimal(col);
		} else if (Date.class.equals(valueClass)) {
			//Si la valeur est null rs renvoie bien null
			final Timestamp timestamp = rs.getTimestamp(col);
			//Pour avoir une date avec les heures (Sens Java !)
			//il faut récupérer le timeStamp
			//Puis le transformer en java.util.Date (Date+heure)
			value = timestamp == null ? null : new java.util.Date(timestamp.getTime());
		} else {
			throw new DaoException("Type non géré " + valueClass.getName(), null);
		}
		return value;
	}
}
