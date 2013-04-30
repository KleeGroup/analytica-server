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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author npiedeloup
 * @version $Id: NamedPreparedStatement.java,v 1.2 2012/04/17 09:11:28 pchretien Exp $
 */
public class NamedPreparedStatement {
	private static final Pattern GENERATED_KEY_FIELD_PATTERN = Pattern.compile("@generated\\((\\p{Alnum}+)\\)");
	private static final Pattern BINDED_FIELD_PATTERN = Pattern.compile("\\$\\{(\\p{Alnum}+)\\}");
	private final List<String> orderedFieldName = new ArrayList<String>();
	private final String bindedSql;
	private String generatedField;

	NamedPreparedStatement(final String sql) {
		assert sql != null;
		//---------------------------------------------------------------------
		String sqlRead = readCommands(sql);
		sqlRead = parse(sqlRead);
		bindedSql = sqlRead;
	}

	private String parse(final String sql) {
		//final int lastFoundIndex = 0;
		final StringBuffer sb = new StringBuffer(sql.length());
		final Matcher m = BINDED_FIELD_PATTERN.matcher(sql);
		while (m.find()) {
			m.appendReplacement(sb, "?");
			//sb.append(sql.subSequence(lastFoundIndex, m.end()));
			//sb.append('?');
			//lastFoundIndex = m.end();
			orderedFieldName.add(m.group(1));//on récupère le nom du field
		}
		//et la fin
		m.appendTail(sb);
		//sb.append(sql.subSequence(lastFoundIndex, sql.length()));
		return sb.toString();
	}

	private String readCommands(final String sql) {
		final StringBuffer sb = new StringBuffer(sql.length());
		final Matcher m = GENERATED_KEY_FIELD_PATTERN.matcher(sql);
		if (m.find()) {
			m.appendReplacement(sb, "");
			generatedField = m.group(1);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public String getSql() {
		return bindedSql;
	}

	public <O extends Object> void setValues(final PreparedStatement statement, final O dataBean) throws DaoException {
		final BeanWrapper<O> beanWrapper = createBeanWrapper(dataBean);
		for (int parameterIndex = 0; parameterIndex < orderedFieldName.size(); parameterIndex++) {
			final String fieldName = orderedFieldName.get(parameterIndex);
			setValue(dataBean, fieldName, parameterIndex, statement, beanWrapper);
		}
	}

	private <O extends Object> BeanWrapper<O> createBeanWrapper(final O dataBean) {
		if (dataBean instanceof PlainBean) {
			return (BeanWrapper<O>) new PlainBeanWrapper((PlainBean) dataBean);
		}
		return new BeanWrapperImpl(dataBean.getClass());
	}

	private <O extends Object> void setValue(final O dataBean, final String fieldName, final int parameterIndex, final PreparedStatement statement, final BeanWrapper<O> beanWrapper) throws DaoException {
		final Object value = beanWrapper.get(dataBean, fieldName);
		final int sqlType = resolveSqlType(beanWrapper.getType(fieldName));
		//System.out.println(fieldName + "=" + value);
		setValue(fieldName, value, parameterIndex, statement, sqlType);
	}

	private void setValue(final String fieldName, final Object value, final int parameterIndex, final PreparedStatement statement, final int sqlType) throws DaoException {
		try {
			final int statementIndex = parameterIndex + 1;
			if (value == null) {
				statement.setNull(statementIndex, sqlType);
			} else {
				switch (sqlType) {
					case Types.INTEGER:
						statement.setInt(statementIndex, ((Integer) value).intValue());
						break;
					case Types.BIGINT:
						statement.setLong(statementIndex, ((Long) value).longValue());
						break;
					case Types.BIT:
						final int intValue = Boolean.TRUE.equals(value) ? 1 : 0;
						statement.setInt(statementIndex, intValue);
						break;
					case Types.DOUBLE:
						statement.setDouble(statementIndex, ((Double) value).doubleValue());
						break;
					case Types.DECIMAL:
						statement.setBigDecimal(statementIndex, (BigDecimal) value);
						break;
					case Types.VARCHAR:
						statement.setString(statementIndex, (String) value);
						break;
					case Types.TIMESTAMP:
						if (value instanceof java.sql.Timestamp) {
							statement.setTimestamp(statementIndex, (java.sql.Timestamp) value);
						} else {
							final java.sql.Timestamp ts = new java.sql.Timestamp(((java.util.Date) value).getTime());
							statement.setTimestamp(statementIndex, ts);
						}
						break;

					default:
						throw new DaoException("Type non géré " + value.getClass().getName(), null);
				}
			}
		} catch (final SQLException e) {
			throw new DaoException("Erreur à l'enregistrement du paramètre " + fieldName, e);
		}
	}

	//	private Object getValue(final Method method, final Object dataBean) throws DaoException {
	//		try {
	//			return method.invoke(dataBean);
	//		} catch (final IllegalArgumentException e) {
	//			throw new DaoException(
	//					"Erreur lors de l'appel à " + dataBean.getClass().getName() + "." + method.getName(), e);
	//		} catch (final IllegalAccessException e) {
	//			throw new DaoException(
	//					"Erreur lors de l'appel à " + dataBean.getClass().getName() + "." + method.getName(), e);
	//		} catch (final InvocationTargetException e) {
	//			throw new DaoException(
	//					"Erreur lors de l'appel à " + dataBean.getClass().getName() + "." + method.getName(), e);
	//		}
	//	}
	//
	//	private void setValue(final String fieldName, final Object value, final Object dataBean) throws DaoException {
	//		assert value != null;
	//		final Method method = getSetter(fieldName, value.getClass(), dataBean);
	//		try {
	//			method.invoke(dataBean, value);
	//		} catch (final IllegalArgumentException e) {
	//			throw new DaoException(
	//					"Erreur lors de l'appel à " + dataBean.getClass().getName() + "." + method.getName(), e);
	//		} catch (final IllegalAccessException e) {
	//			throw new DaoException(
	//					"Erreur lors de l'appel à " + dataBean.getClass().getName() + "." + method.getName(), e);
	//		} catch (final InvocationTargetException e) {
	//			throw new DaoException(
	//					"Erreur lors de l'appel à " + dataBean.getClass().getName() + "." + method.getName(), e);
	//		}
	//	}

	//	private Method getGetter(final String fieldName, final Object dataBean) throws DaoException {
	//		final String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	//		try {
	//			return dataBean.getClass().getMethod(methodName);
	//		} catch (final SecurityException e) {
	//			throw new DaoException("Méthode " + methodName + " inacessible sur " + dataBean.getClass().getName(), e);
	//		} catch (final NoSuchMethodException e) {
	//			throw new DaoException("Méthode " + methodName + " inexistante sur " + dataBean.getClass().getName(), e);
	//		}
	//	}
	//
	//	private Method getSetter(final String fieldName, final Class<?> valueClass, final Object dataBean)
	//			throws DaoException {
	//		final String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	//		try {
	//			return dataBean.getClass().getMethod(methodName, valueClass);
	//		} catch (final SecurityException e) {
	//			throw new DaoException("Méthode " + methodName + " inacessible sur " + dataBean.getClass().getName(), e);
	//		} catch (final NoSuchMethodException e) {
	//			throw new DaoException("Méthode " + methodName + " inexistante sur " + dataBean.getClass().getName(), e);
	//		}
	//	}

	private int resolveSqlType(final Class<?> returnType) throws DaoException {
		assert returnType != null;
		//---------------------------------------------------------------------
		if (Integer.class.equals(returnType) || int.class.equals(returnType)) {
			return Types.INTEGER;
		} else if (Boolean.class.equals(returnType) || boolean.class.equals(returnType)) {
			return Types.BIT;
		} else if (Long.class.equals(returnType) || long.class.equals(returnType)) {
			return Types.BIGINT;
		} else if (Double.class.equals(returnType) || double.class.equals(returnType)) {
			return Types.DOUBLE;
		} else if (BigDecimal.class.equals(returnType)) {
			return Types.DECIMAL;
		} else if (String.class.equals(returnType)) {
			return Types.VARCHAR;
		} else if (Date.class.equals(returnType)) {
			return Types.TIMESTAMP;
		} else {
			throw new DaoException("Type non géré " + returnType.getName(), null);
		}
	}

	public boolean hasSequence() {
		return generatedField != null;
	}

	public void setSequence(final long pk, final Object dataBean) throws DaoException {
		final BeanWrapper<Object> beanWrapper = createBeanWrapper(dataBean);
		beanWrapper.set(dataBean, generatedField, pk);
	}
}
