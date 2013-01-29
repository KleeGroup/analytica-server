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

import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup
 * @version $Id: PlainBeanWrapper.java,v 1.4 2012/06/06 15:55:49 npiedeloup Exp $
 */
public class PlainBeanWrapper implements BeanWrapper<PlainBean> {
	public PlainBean bean;

	public PlainBeanWrapper(final PlainBean bean) {
		Assertion.notNull(bean);
		//---------------------------------------------------------------------
		this.bean = bean;
	}

	public void set(final PlainBean bean2, final String fieldName, final Object value) {
		Assertion.precondition(bean == bean2, "L'instance de traitement est différent de celle associé à ce Wrapper");
		//---------------------------------------------------------------------
		bean.set(fieldName, value);
	}

	public <R extends Object> R get(final PlainBean bean2, final String fieldName) {
		Assertion.precondition(bean == bean2, "L'instance de traitement est différent de celle associé à ce Wrapper");
		//---------------------------------------------------------------------
		return bean.<R> get(fieldName);
	}

	public PlainBean newInstance() {
		return new PlainBean();
	}

	public Class<?> getType(final String fieldName) throws DaoException {
		final Class<?> clazz = bean.getType(fieldName);
		if (clazz == null) {
			throw new DaoException("FieldName " + fieldName + " inconnu", null);
		}
		return clazz;
	}

}
