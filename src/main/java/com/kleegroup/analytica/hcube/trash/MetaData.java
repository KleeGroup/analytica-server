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
 * Meta-données d'un processus.
 * Une méta-données possède un nom et une valeur.
 * Exemple : 
 * 	Type du processus
 * 		name  = Type
 * 		value = SQL
 *  
 * @author npiedeloup
 * @version $Id: MetaData.java,v 1.1 2012/10/15 15:28:08 pchretien Exp $
 */
public final class MetaData {
	private final String value;
	private final String name;

	private final int hashcode;

	/**
	 * Constructeur.
	 * @param name Nom de la méta-donnée
	 * @param value Valeur (non null, mais peut être vide). 
	 */
	public MetaData(final String name, final String value) {
		Assertion.notNull(value);
		//Assertion.precondition(values.size() >= 0, "Une méta-donnée doit avoir au moins une valeur");
		Assertion.notEmpty(name);
		//---------------------------------------------------------------------
		this.value = value;
		this.name = name;
		hashcode = value.hashCode() * 31 ^ name.hashCode();
	}

	/**
	 * @return Valeurs de la méta-données
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return Nom de la méta-donnée
	 */
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object other) {
		throw new IllegalAccessError();
		//		if (other instanceof MetaData) {
		//			final MetaData otherMetaData = (MetaData) other;
		//			return name.equals(otherMetaData.name) && value.equals(otherMetaData.value);
		//		}
		//		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return hashcode;
	}
}
