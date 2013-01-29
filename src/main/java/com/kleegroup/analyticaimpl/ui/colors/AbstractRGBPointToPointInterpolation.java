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
package com.kleegroup.analyticaimpl.ui.colors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import kasper.kernel.util.Assertion;

/**
 * Classe abstraire d'interpolation de couleur.
 * Permet de réaliser une interpolation en implémentant seulement l'interpolation à partir de 4 points clés et le ratio entre la couleur 2 et 3.
 * 	C1-----C2----|--C3-----C4
 * @author npiedeloup
 * @version $Id: $
 */
abstract class AbstractRGBPointToPointInterpolation implements RGBInterpolation {
	private Color[] mainColors;

	/** {@inheritDoc} */
	public void setMainColors(final Color[] mainColors) {
		this.mainColors = mainColors;
	}

	/** {@inheritDoc} */
	public List<Color> getColors(final int nbColors) {
		Assertion.notNull(mainColors, "Les couleurs principales n'ont pas été settées.");
		//---------------------------------------------------------------------
		int startJ = 0;
		final List<Color> interpolatedColor = new ArrayList<Color>();
		int nbInterpolatedColor = mainColors.length;
		int nbInterpolatedColorDegree = 0;
		while ((nbInterpolatedColor - 1) % (nbColors - 1) != 0) {
			nbInterpolatedColorDegree++;
			nbInterpolatedColor = mainColors.length + nbInterpolatedColorDegree * (mainColors.length - 1);
		}
		nbInterpolatedColorDegree++;
		for (int i = 0; i < mainColors.length - 1; i++) {
			final Color c1 = i - 1 >= 0 ? mainColors[i - 1] : null;
			final Color c2 = mainColors[i];
			final Color c3 = mainColors[i + 1];
			final Color c4 = i + 2 < mainColors.length ? mainColors[i + 2] : null;
			for (double j = startJ; j < nbInterpolatedColorDegree + 1; j++) {
				final Color color = colorInterpolation(j / nbInterpolatedColorDegree, c1, c2, c3, c4);
				interpolatedColor.add(color);
			}
			startJ = 1; //on ne refait pas le premier point (dejà atteint)
		}
		final List<Color> result = new ArrayList<Color>();
		for (int i = 0; i < nbColors; i++) {
			final int index = (interpolatedColor.size() - 1) / (nbColors - 1) * i;
			result.add(interpolatedColor.get(index));
		}
		return result;
	}

	/**
	 * Fournit l'interpolation d'une couleur en fonction de quatre points clés, et une position (ratio entre 0 et 1) entre la couleur 2 et 3.
	 * @param t ratio entre 0 et 1 (0 pour c2 et 1 pour c3)
	 * @param c1 couleur clé 1
	 * @param c2 couleur clé 2
	 * @param c3 couleur clé 3
	 * @param c4 couleur clé 4
	 * @return couleur interpolée.
	 */
	protected abstract Color colorInterpolation(final double t, final Color c1, final Color c2, final Color c3, final Color c4);
}
