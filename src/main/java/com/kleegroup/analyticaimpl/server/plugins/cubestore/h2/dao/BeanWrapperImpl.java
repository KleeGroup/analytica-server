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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author npiedeloup
 * @version $Id: BeanWrapperImpl.java,v 1.2 2012/04/17 09:11:28 pchretien Exp $
 */
public final class BeanWrapperImpl<O extends Object> implements BeanWrapper<O> {
	private final Map<String, Method> getterMap = new HashMap<String, Method>();
	private final Map<String, Method> setterMap = new HashMap<String, Method>();
	private final Map<String, Class<?>> typeMap = new HashMap<String, Class<?>>();
	private final Class<O> beanClass;

	public BeanWrapperImpl(final Class<O> beanClass) {
		assert beanClass != null;
		assert !PlainBean.class.equals(beanClass) : "Utiliser le BeanPlainWrapper pour les BeanPlain";
		this.beanClass = beanClass;
		initMethodMap();
	}

	public void set(final O bean, final String fieldName, final Object value) throws DaoException {
		try {
			getSetter(fieldName).invoke(bean, value);
		} catch (final IllegalArgumentException e) {
			throw new DaoException("Erreur de " + beanClass + ".set " + fieldName, e);
		} catch (final IllegalAccessException e) {
			throw new DaoException("Erreur de " + beanClass + ".set " + fieldName, e);
		} catch (final InvocationTargetException e) {
			throw new DaoException("Erreur de " + beanClass + ".set " + fieldName, e);
		}
	}

	public <R extends Object> R get(final O bean, final String fieldName) throws DaoException {
		try {
			return (R) getGetter(fieldName).invoke(bean);
		} catch (final IllegalArgumentException e) {
			throw new DaoException("Erreur de " + beanClass + ".get " + fieldName, e);
		} catch (final IllegalAccessException e) {
			throw new DaoException("Erreur de " + beanClass + ".get " + fieldName, e);
		} catch (final InvocationTargetException e) {
			throw new DaoException("Erreur de " + beanClass + ".get " + fieldName, e);
		}
	}

	public O newInstance() throws DaoException {
		try {
			return beanClass.newInstance();
		} catch (final InstantiationException e) {
			throw new DaoException("Erreur de creation du bean", e);
		} catch (final IllegalAccessException e) {
			throw new DaoException("Erreur de creation du bean", e);
		}
	}

	private Method getSetter(final String fieldName) throws DaoException {
		final Method method = setterMap.get(fieldName);
		if (method == null) {
			throw new DaoException("Méthode set " + fieldName + " inacessible sur " + beanClass.getName(), null);
		}
		return method;
	}

	private Method getGetter(final String fieldName) throws DaoException {
		final Method method = getterMap.get(fieldName);
		if (method == null) {
			throw new DaoException("Méthode get " + fieldName + " inacessible sur " + beanClass.getName(), null);
		}
		return method;
	}

	private void initMethodMap() {
		//final String methodName = "set" + fieldName;
		final Method[] methodList = beanClass.getDeclaredMethods();
		for (final Method method : methodList) {
			final String methodName = method.getName();
			if (methodName.startsWith("get") && method.getParameterTypes().length == 0 && !method.getReturnType().equals(Void.TYPE)) {
				final String attributName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
				getterMap.put(attributName, method);
				final Class<?> oldType = typeMap.put(attributName, method.getReturnType());
				assert oldType == null : "Le type du getter (" + method.getReturnType() + ") et du setter (" + oldType + ") diffère";
			} else if (methodName.startsWith("set") && method.getParameterTypes().length == 1 && method.getReturnType().equals(Void.TYPE)) {
				final String attributName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
				setterMap.put(attributName, method);
				final Class<?> oldType = typeMap.put(attributName, method.getParameterTypes()[0]);
				assert oldType == null : "Le type du getter (" + oldType + ") et du setter (" + method.getParameterTypes()[0] + ") diffère";
			}
		}
	}

	public Class<?> getType(final String fieldName) throws DaoException {
		final Class<?> clazz = typeMap.get(fieldName);
		if (clazz == null) {
			throw new DaoException("FieldName " + fieldName + " inconnu sur " + beanClass.getName(), null);
		}
		return clazz;
	}

}
