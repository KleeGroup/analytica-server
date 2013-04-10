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
package com.kleegroup.analyticaimpl.server.plugins.cubestore.h2.bean;

import com.kleegroup.analytica.hcube.cube.DataKey;
import com.kleegroup.analytica.hcube.cube.DataType;
import com.kleegroup.analytica.hcube.cube.MetricKey;

/**
 * DataKey.
 * Une dataKey possède un type et un nom.
 *  
 * @author npiedeloup
 * @version $Id: DataKeyBuilderBean.java,v 1.1 2012/03/15 18:03:50 npiedeloup Exp $
 */
public final class DataKeyBuilderBean {
	private String name;

	/**
	 * @param dataType Type de la data (non connu du select)
	 * @return Build de DataKey.
	 */
	public DataKey build(final DataType dataType) {
		return new DataKey(new MetricKey(name), dataType);
	}

	/**
	 * @param name Nom de la data
	 */
	public final void setName(final String name) {
		this.name = name;
	}
}
