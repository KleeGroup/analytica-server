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
package com.kleegroup.analytica.hcube.cube;

import java.util.Collection;

/**
 * Un cube contient :
 *  - des m�triques nomm�es
 *  	exemple : temps r�ponse, nombre de mails envoy�s
 *  - des m�tadonn�es 
 *  	exemple : tags, users
 *  
 * @author npiedeloup, pchretien
 * @version $Id: Cube.java,v 1.6 2012/10/16 13:34:49 pchretien Exp $
 */
public interface HVirtualCube {

	/**
	 * Acc�s d'une m�trique par son nom
	 * @param name Nom de la m�trique
	 * @return M�trique
	 */
	HMetric getMetric(final HMetricKey metricKey);

	/**
	 * Liste de toutes les m�triques
	 * @return M�triques du cube
	 */
	Collection<HMetric> getMetrics();

}
