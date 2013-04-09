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
package com.kleegroup.analytica.server.data;

import java.util.List;

import com.kleegroup.analytica.hcube.cube.DataKey;

import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup
 * @version $Id: Data.java,v 1.6 2013/01/14 16:35:20 npiedeloup Exp $
 */
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

public final class Data {
	private final DataKey key;
	private final Double value;
	private final List<String> stringValues;

	public Data(final DataKey key, final Double value) {
		this(key, value, null);
	}

	public Data(final DataKey key, final List<String> values) {
		this(key, null, values);
	}

	private Data(final DataKey key, final Double value, final List<String> stringValues) {
		Assertion.notNull(key);
		if (key.getType() == DataType.metaData) {
			Assertion.notNull(stringValues, "Pour les méta-données la value est une String mais peut-être vide");
			Assertion.precondition(stringValues != null && !stringValues.isEmpty() && value == null, "Pour les méta-données la value est une liste de avec au moins un élément");
		} else {
			Assertion.precondition(stringValues == null && value != null, "La valeur doit être numérique, sauf pour les méta-données");
		}
		//---------------------------------------------------------------------
		this.key = key;
		this.value = value;
		this.stringValues = stringValues;
	}

	public DataKey getKey() {
		return key;
	}

	public double getValue() {
		Assertion.precondition(key.getType() != DataType.metaData, "Pour les méta-données il n'y a pas de valeur numérique");
		//---------------------------------------------------------------------
		return value;
	}

	public List<String> getStringValues() {
		return stringValues;
	}

	@Override
	public String toString() {
		return key + "=" + (value != null ? value : stringValues);
	}
}
