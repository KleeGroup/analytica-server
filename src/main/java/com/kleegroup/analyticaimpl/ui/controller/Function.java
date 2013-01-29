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
package com.kleegroup.analyticaimpl.ui.controller;

/**
 * Approche fonctionnelle.
 * On définit un objet fonction.
 * @author pchretien
 * @version $Id: Function.java,v 1.1 2012/05/10 09:38:14 npiedeloup Exp $
 * @param <IN> Type de paramètre d'entrée de la fonction
 * @param <OUT> Type de paaramètre de sortie de la fonction
 */
interface Function<IN, OUT> {
	/**
	 * @param input Paramètre d'entrée
	 * @return Calcul de la fonction
	 */
	OUT apply(final IN input);
}
