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
package io.analytica.ui;

import io.analytica.ui.colors.HSLLinearInterpolation;
import io.analytica.ui.colors.RGBBezierSpline;
import io.analytica.ui.colors.RGBCatmullInterpolation;
import io.analytica.ui.colors.RGBInterpolation;
import io.analytica.ui.colors.RGBLinearInterpolation;

import java.awt.Color;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Cas de Test JUNIT de l'API Analytics.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ColorsTest.java,v 1.2 2012/04/17 13:05:59 npiedeloup Exp $
 */
public final class ColorsTest extends TestCase {
	/** Logger. */
	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Test RGB Interpolation.
	 */
	@Test
	public void testRGBInterpolations() {
		//final Color[] controlPoints = { new Color(255, 0, 0), new Color(255, 255, 0), new Color(0, 255, 0), new Color(0, 255, 255), new Color(0, 0, 255), new Color(255, 0, 255) };
		//final Color[] controlPoints = { new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255) };
		//final Color[] controlPoints = { new Color(255, 51, 51), new Color(255, 255, 51), new Color(51, 153, 51), new Color(51, 153, 255)};
		//final Color[] controlPoints = { new Color(230, 31, 30), new Color(230, 130, 30), new Color(230, 230, 30), new Color(130, 230, 30), new Color(30, 230, 30), new Color(30, 230, 130), new Color(30, 230, 230), new Color(30, 130, 230), new Color(30, 30, 230), new Color(130, 30, 230), new Color(230, 30, 230), new Color(130, 30, 130), new Color(230, 30, 31) };
		//final Color[] controlPoints = { new Color(230, 31, 30), new Color(230, 230, 30), new Color(30, 230, 30), new Color(30, 230, 230), new Color(30, 30, 230), new Color(230, 30, 230), new Color(230, 30, 31) };
		//final Color[] controlPoints = { new Color(0, 170, 85), new Color(240, 240, 170) };
		final Color[] controlPoints = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, new Color(75, 0, 130), new Color(238, 130, 238) };

		//		final Color[] controlPoints = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, new Color(138, 43, 226), //violet
		//				new Color(75, 0, 130) //indigo
		//		};
		final RGBInterpolation[] rbgInterpolations = { new RGBBezierSpline(), new RGBCatmullInterpolation(), new RGBLinearInterpolation(), new HSLLinearInterpolation() };
		log.info("\n<div style='float:left;width:10px;font-size:5px;'>&nbsp;</div>");
		for (final RGBInterpolation rbgInterpolation : rbgInterpolations) {
			final StringBuilder result = new StringBuilder();
			final int nbSerie = 10;
			rbgInterpolation.setMainColors(controlPoints);
			for (final Color color : rbgInterpolation.getColors(nbSerie)) {
				result.append("<div style='height:" + 400 / nbSerie + "px;width:10px;background:#");
				result.append(Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1));
				result.append(";'></div>");

			}
			System.out.print("\n<div style='float:left;font-size:5px;' >" + rbgInterpolation.getClass().getSimpleName().substring(0, 4) + "\n" + result + "\n</div>");
		}
	}
}
