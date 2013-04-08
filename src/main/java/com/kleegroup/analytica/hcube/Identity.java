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
package com.kleegroup.analytica.hcube;

import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup
 * @version $Id: Identity.java,v 1.3 2012/10/16 13:52:38 pchretien Exp $
 */
public abstract class Identity implements Comparable<Identity> {
	private final String id;

	public Identity(final String id) {
		Assertion.notEmpty(id);
		//---------------------------------------------------------------------
		this.id = id;
	}

	@Override
	public final int hashCode() {
		return id.hashCode();
	}

	@Override
	public final boolean equals(final Object object) {
		if (object instanceof Identity) {
			return id.equals(((Identity) object).id());
		}
		return false;
	}

	public final String id() {
		return id;
	}

	public int compareTo(final Identity object) {
		Assertion.notNull(object);
		//---------------------------------------------------------------------
		return id.compareTo(object.id());
	}

	@Override
	public final String toString() {
		return id;
	}

}
