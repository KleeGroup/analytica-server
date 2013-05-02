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

import java.util.HashMap;
import java.util.Map;

import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup
 * @version $Id: PlainBean.java,v 1.3 2012/06/06 15:55:49 npiedeloup Exp $
 */
public final class PlainBean {
	private final Map<String, Object> values = new HashMap<String, Object>();
	private final Map<String, Class<?>> fieldType = new HashMap<String, Class<?>>();

	public PlainBean() {
		//rien
	}

	public void set(final String fieldName, final Object value) {
		Assertion.notEmpty(fieldName);
		Assertion.notNull(value);
		//---------------------------------------------------------------------
		values.put(fieldName, value);
		fieldType.put(fieldName, value.getClass());
	}

	public void setNull(final String fieldName, final Class<?> valueClass) {
		Assertion.notEmpty(fieldName);
		Assertion.notNull(valueClass);
		//---------------------------------------------------------------------
		values.put(fieldName, null);
		fieldType.put(fieldName, valueClass);
	}

	public <O extends Object> O get(final String fieldName) {
		Assertion.notEmpty(fieldName);
		//---------------------------------------------------------------------
		final O result = (O) values.get(fieldName);
		assert result != null : "FieldName " + fieldName + " inconnu";
		return result;
	}

	public Class<?> getType(final String fieldName) {
		Assertion.notEmpty(fieldName);
		//---------------------------------------------------------------------
		final Class<?> result = fieldType.get(fieldName);
		assert result != null : "FieldName " + fieldName + " inconnu";
		return result;
	}
}
