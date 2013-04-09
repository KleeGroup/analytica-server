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
package com.kleegroup.analytica.hcube.cube;


import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup
 * @version $Id: DataKey.java,v 1.3 2012/04/17 09:15:26 pchretien Exp $
 */
public final class DataKey {
	private final String name;
	private final DataType type;

	//COMMENTAIRES 

	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 
	//COMMENTAIRES 

	public DataKey(final String name, final DataType type) {
		Assertion.notEmpty(name);
		Assertion.notNull(type);
		//---------------------------------------------------------------------
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public DataType getType() {
		return type;
	}

	@Override
	public String toString() {
		return name + ":" + type.name();
	}
}
