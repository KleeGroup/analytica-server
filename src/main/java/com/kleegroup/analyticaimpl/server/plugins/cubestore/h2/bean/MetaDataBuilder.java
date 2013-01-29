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

import kasper.kernel.lang.Builder;

import com.kleegroup.analyticaimpl.server.cube.MetaData;

/**
 * Meta-données d'un processus.
 * Une méta-données possède un nom et une valeur.
 * Exemple : 
 * 	Type du processus
 * 		name  =  Type
 * 		value = SQL
 *  
 * @author npiedeloup
 * @version $Id: MetaDataBuilder.java,v 1.2 2012/10/16 12:40:46 npiedeloup Exp $
 */
public final class MetaDataBuilder implements Builder<MetaData> {
	private long cubId;
	private String value;
	private String name;

	/**
	 * @return Build de metaData
	 */
	public MetaData build() {
		return new MetaData(name, value);
	}

	public final long getCubId() {
		return cubId;
	}

	public final void setCubId(final long cubId) {
		this.cubId = cubId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
