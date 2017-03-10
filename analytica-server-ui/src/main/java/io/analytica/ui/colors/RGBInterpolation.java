/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.ui.colors;

import java.awt.Color;
import java.util.List;

/**
 * Interface d'interpolarion de couleurs.
 * @author npiedeloup
 * @version $Id: $
 */
public interface RGBInterpolation {

	/**
	 * @return Code identifiant ce type d'interpolation
	 */
	String getInterpolationCode();

	/**
	 * Paramétre les couleurs principales (de guidage)
	 * @param maincolors Fixe les couleurs principale (de guidage)
	 */
	void setMainColors(Color[] maincolors);

	/**
	 * Récupére une liste de couleur pour un nombre de couleur voulue.
	 * Le nombre de couleur attendu sont interpolées é partir des couleurs principales. 
	 * @param nbColors Nombre de couleur demandé
	 * @return Liste des couleurs interpolées.
	 */
	List<Color> getColors(final int nbColors);
}
