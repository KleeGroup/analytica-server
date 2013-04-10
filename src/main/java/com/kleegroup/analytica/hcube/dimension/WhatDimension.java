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
package com.kleegroup.analytica.hcube.dimension;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

/**
 * Niveaux global, Module, SimpleName ou fullName de la dimension hiérarchique What.
 *
 * @author npiedeloup, pchretien
 * @version $Id: WhatDimension.java,v 1.2 2012/04/11 15:52:24 npiedeloup Exp $
 */
public enum WhatDimension implements Dimension<WhatDimension> {
	/**
	 * Global.
	 */
	Global(null),
	/**
	 * Type. (SQL, PAGE, WORK, ...)
	 */
	Type(Global),
	/**
	 * Module. (pages, resources, ...)
	 */
	Module(Type),
	/**
	 * SimpleName.
	 */
	SimpleName(Module),
	/**
	 * FullName.
	 */
	FullName(SimpleName);

	public static String SEPARATOR = "/";

	private final WhatDimension up;

	/**
	 * Constructeur.
	 * @param up Niveau supérieur
	 */
	WhatDimension(final WhatDimension up) {
		this.up = up;
	}

	/** {@inheritDoc} */
	public WhatDimension drillUp() {
		return up;
	}

	/** {@inheritDoc} */
	public WhatDimension drillDown() {
		switch (this) {
			case Global:
				return Type;
			case Type:
				return Module;
			case Module:
				return SimpleName;
			case SimpleName:
				return FullName;
			case FullName:
				return null;
			default:
				throw new KRuntimeException("WhatDimension inconnu");
		}
	}

	/**
	 * Normalise la valeur pour correspondre au niveau d'agregation de cette dimension.
	 * @param fullName Valeur
	 * @return Valeur normalisée
	 */
	public String reduce(final String fullName) {
		Assertion.notNull(fullName);
		//---------------------------------------------------------------------
		return reduce(fullName, this);
	}

	private static String reduce(final String fullName, final WhatDimension whatDimension) {
		Assertion.precondition(fullName.startsWith(SEPARATOR), "Le nom doit commencer par {0} (ici:{1})", SEPARATOR, fullName);
		//---------------------------------------------------------------------
		final int part;
		switch (whatDimension) {
			case Global:
				part = 0;
				break;
			case Type:
				part = 1;
				break;
			case Module:
				part = 2;
				break;
			case SimpleName:
				part = 3;
				break;
			case FullName:
				part = 15; //on prend tout
				break;
			default:
				throw new UnsupportedOperationException("Dimension non reconnue: " + whatDimension);
		}
		//On garde les N premiers block en fonction du reduce.
		//On accepte que l'entrée n'est pas tout les block attendu : 
		//typiquement une mesure s'appliquant globalement sur un module (c'est déjà son niveau le plus bas)
		//reste visible en dessous

		int indexof = -1;
		for (int i = 0; i < part + 1; i++) {
			final int nextIndex = fullName.indexOf(SEPARATOR, indexof + 1);
			if (nextIndex == -1) {
				indexof = fullName.length();
				break;
			}
			indexof = nextIndex;
		}
		if (indexof == 0) { //exception de la racine
			return SEPARATOR;
		}
		return fullName.substring(0, indexof);
	}
}
