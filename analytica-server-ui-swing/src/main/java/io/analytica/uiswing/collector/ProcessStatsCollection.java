/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidi�re - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.uiswing.collector;

import java.io.Serializable;
import java.util.Map;

public interface ProcessStatsCollection<P extends Serializable> extends Serializable {

	/**
	 * Retourne une map, ou la cl� est le nom d'une m�thode.
	 * La valeur et la technique a utiliser pour traiter les r�sultats d�pends de l'impl�mentation.
	 * Mais dans tous les cas les donn�es statistiques sont dans des ProcessStats
	 * @return Map
	 */
	Map<String, P> getResults();

	/**
	 * Permet de fusionner deux collections de resultats
	 * @param other ProcessStatsCollection
	 */
	void merge(ProcessStatsCollection<P> other);
}
