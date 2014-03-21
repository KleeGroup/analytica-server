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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thomas
 * Date: Jun 27, 2006
 * Time: 10:46:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class RGBBezierSpline implements RGBInterpolation {
	private Color[] mainColors;

	/** {@inheritDoc} */
	@Override
	public String getInterpolationCode() {
		return "BSPLINE";
	}

	/** {@inheritDoc} */
	public void setMainColors(final Color[] mainColors) {
		this.mainColors = mainColors;
	}

	/** {@inheritDoc} */
	public List<Color> getColors(final int nbColors) {
		final List<Color> result = new ArrayList<>(nbColors);
		for (float i = 0; i < nbColors; i++) {
			result.add(getColor(i / nbColors));
		}
		return result;
	}

	private Color getColor(final float param) {
		Color[] output = new Color[mainColors.length];
		System.arraycopy(mainColors, 0, output, 0, mainColors.length);

		/* Keep evaluating the Systolic Array until one point remains */
		do {
			output = evaluateSystolicArray(param, output);
		} while (output.length > 1);
		return output[0];
	}

	/**
	 * Evaluates the given step in the systolic array.
	 *
	 * @param t      Parameter for interpolating between points
	 * @param points Control points for given step of systolic array
	 * @return Next level of systolic array
	 */
	private Color[] evaluateSystolicArray(final float t, final Color[] points) {
		final Color[] output = new Color[points.length - 1];
		for (int i = 0; i < output.length; i++) {
			output[i] = new Color(Math.round((1 - t) * points[i].getRed() + t * points[i + 1].getRed()), Math.round((1 - t) * points[i].getGreen() + t * points[i + 1].getGreen()), Math.round((1 - t) * points[i].getBlue() + t * points[i + 1].getBlue()));
		}
		return output;
	}

}
