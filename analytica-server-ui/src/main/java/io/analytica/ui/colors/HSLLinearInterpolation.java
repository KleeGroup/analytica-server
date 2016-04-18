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

/**
 * Transformation par interpolation linaire en HSL.
 * Algorithme de transfromation par Prof. Dr.-Ing. Kai Uwe Barthel.
 * @see "http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/farbraumJava.htm"
 *  * 
 * @author npiedeloup
 * @version $Id: $
 */
public class HSLLinearInterpolation extends AbstractRGBPointToPointInterpolation {
	/** {@inheritDoc} */
	@Override
	public String getInterpolationCode() {
		return "HSL";
	}

	/** {@inheritDoc} */
	@Override
	protected Color colorInterpolation(final double t, final Color c1, final Color c2, final Color c3, final Color c4) {
		return hslLinear(t, c2, c3);
	}

	private Color hslLinear(final double t, final Color c2, final Color c3) {
		final int[] hsl1 = rgb2hsl(c2.getRed(), c2.getGreen(), c2.getBlue());
		final int[] hsl2 = rgb2hsl(c3.getRed(), c3.getGreen(), c3.getBlue());
		final int[] hsl3 = { (int) linear(t, hsl1[0], hsl2[0]), (int) linear(t, hsl1[1], hsl2[1]), (int) linear(t, hsl1[2], hsl2[2]) };
		final int[] rgb3 = hsl2rgb(hsl3[0], hsl3[1], hsl3[2]);
		return new Color(rgb3[0], rgb3[1], rgb3[2]);
	}

	private int[] rgb2hsl(final int r, final int g, final int b) {

		final float var_R = r / 255f;
		final float var_G = g / 255f;
		final float var_B = b / 255f;

		float var_Min; //Min. value of RGB
		float var_Max; //Max. value of RGB
		float del_Max; //Delta RGB value

		if (var_R > var_G) {
			var_Min = var_G;
			var_Max = var_R;
		} else {
			var_Min = var_R;
			var_Max = var_G;
		}

		if (var_B > var_Max) {
			var_Max = var_B;
		}
		if (var_B < var_Min) {
			var_Min = var_B;
		}

		del_Max = var_Max - var_Min;

		float H = 0, S, L;
		L = (var_Max + var_Min) / 2f;

		if (del_Max == 0) {
			H = 0;
			S = 0;
		} // gray
		else { //Chroma
			if (L < 0.5) {
				S = del_Max / (var_Max + var_Min);
			} else {
				S = del_Max / (2 - var_Max - var_Min);
			}

			final float del_R = ((var_Max - var_R) / 6f + del_Max / 2f) / del_Max;
			final float del_G = ((var_Max - var_G) / 6f + del_Max / 2f) / del_Max;
			final float del_B = ((var_Max - var_B) / 6f + del_Max / 2f) / del_Max;

			if (var_R == var_Max) {
				H = del_B - del_G;
			} else if (var_G == var_Max) {
				H = 1 / 3f + del_R - del_B;
			} else if (var_B == var_Max) {
				H = 2 / 3f + del_G - del_R;
			}
			if (H < 0) {
				H += 1;
			}
			if (H > 1) {
				H -= 1;
			}
		}
		final int hsl[] = { (int) (360 * H), (int) (S * 100), (int) (L * 100) };
		return hsl;
	}

	private int[] hsl2rgb(final int inH, final int inS, final int inL) {
		double m1, m2, hue;
		int r, g, b;
		final double s = inS / 100d;
		final double l = inL / 100d;
		if (s == 0) {
			r = g = b = (int) Math.round(l * 255d);
		} else {
			if (l <= 0.5) {
				m2 = l * (s + 1);
			} else {
				m2 = l + s - l * s;
			}
			m1 = l * 2 - m2;
			hue = inH / 360d;
			r = HueToRgb(m1, m2, hue + 1 / 3d);
			g = HueToRgb(m1, m2, hue);
			b = HueToRgb(m1, m2, hue - 1 / 3d);
		}
		final int[] rgb = { r, g, b };
		return rgb;
	}

	private int HueToRgb(final double m1, final double m2, final double hue) {
		double v;
		final double normalizedHue;
		if (hue < 0) {
			normalizedHue = hue + 1;
		} else if (hue > 1) {
			normalizedHue = hue - 1;
		} else {
			normalizedHue = hue;
		}

		if (6 * normalizedHue < 1) {
			v = m1 + (m2 - m1) * normalizedHue * 6;
		} else if (2 * normalizedHue < 1) {
			v = m2;
		} else if (3 * normalizedHue < 2) {
			v = m1 + (m2 - m1) * (2 / 3d - normalizedHue) * 6;
		} else {
			v = m1;
		}

		return (int) Math.round(255 * v);
	}

	private static double linear(final double t, final double p1, final double p2) {
		return p1 + (p2 - p1) * t;
	}
}
