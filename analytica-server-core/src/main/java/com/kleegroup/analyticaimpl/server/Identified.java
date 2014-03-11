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
package com.kleegroup.analyticaimpl.server;

import io.vertigo.kernel.lang.Assertion;

/**
 * Objet avec identifiant externalisé.
 * @author npiedeloup
 * @version $Id: Identified.java,v 1.1 2012/03/22 09:16:40 npiedeloup Exp $
 * @param <T> Type de la data
 */
public final class Identified<T> {
	private final String key;
	private final T data;

	/**
	 * Constructeur.
	 * @param key Clée unique
	 * @param data Données
	 */
	public Identified(final String key, final T data) {
		Assertion.checkArgNotEmpty(key);
		Assertion.checkNotNull(data);
		//-------------------------------------------------------------------------
		this.key = key;
		this.data = data;
	}

	/**
	 * @return Clé unique
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return Données
	 */
	public T getData() {
		return data;
	}

}
