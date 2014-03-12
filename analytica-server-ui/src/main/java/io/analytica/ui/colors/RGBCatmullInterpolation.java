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
package io.analytica.ui.colors;

import java.awt.Color;

/**
 * Transformation par interpolation selon l'algorithme Catmull–Rom spline des composantes RGB.
 * @author npiedeloup
 * @version $Id: $
 */
public class RGBCatmullInterpolation extends AbstractRGBPointToPointInterpolation {

	/** {@inheritDoc} */
	public String getInterpolationCode() {
		return "CATMULL";
	}

	/** {@inheritDoc} */
	@Override
	protected Color colorInterpolation(final double t, final Color c1, final Color c2, final Color c3, final Color c4) {
		final int red = (int) Math.max(Math.min(Math.round(catmull(t, getRed(c1), getRed(c2), getRed(c3), getRed(c4))), 255), 0);
		final int green = (int) Math.max(Math.min(Math.round(catmull(t, getGreen(c1), getGreen(c2), getGreen(c3), getGreen(c4))), 255), 0);
		final int blue = (int) Math.max(Math.min(Math.round(catmull(t, getBlue(c1), getBlue(c2), getBlue(c3), getBlue(c4))), 255), 0);
		return new Color(red, green, blue);
	}

	private Double getRed(final Color color) {
		return color != null ? (double) color.getRed() : null;
	}

	private Double getGreen(final Color color) {
		return color != null ? (double) color.getGreen() : null;
	}

	private Double getBlue(final Color color) {
		return color != null ? (double) color.getBlue() : null;
	}

	//Catmull-Rom spline interpolation function
	//p0 et p3 servent a orienter le chemin entre p1 et p2
	//t est une fraction entre 
	private static double catmull(final double t, final Double inP0, final double p1, final double p2, final Double inP3) {
		final double delta = p2 - p1;
		final double p0 = inP0 != null ? inP0.doubleValue() : p1 - delta;
		final double p3 = inP3 != null ? inP3.doubleValue() : p2 + delta;
		return 0.5 * (2 * p1 + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
	}
}
