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
package com.kleegroup.analyticaimpl.ui.chart.flotr;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Convertisseur pour les graphiques.
 * 
 * @author npiedeloup
 * @version $Id: LineChartStackedConverter.java,v 1.1 2012/04/17 13:05:42 npiedeloup Exp $
 */
public final class LineChartStackedConverter implements Converter {

	/** {@inheritDoc} */
	public Object getAsObject(final FacesContext context, final UIComponent component, final String strValue) {
		throw new ConverterException("Opération impossible");
	}

	/** {@inheritDoc} */
	public String getAsString(final FacesContext context, final UIComponent component, final Object objValue) {
		//		final AnalyticaPanelConf panelConf = (AnalyticaPanelConf) objValue;
		//		final List<DataSet<?, ?>> datas = (List<DataSet<?, ?>>) component.getAttributes().get("datas");
		//		//---------------------------------------------------------------------
		//		final Map<Object, Double> stackMap = new HashMap<Object, Double>();
		//
		//		final List<String> labels = new ArrayList<String>();
		//		final StringBuilder result = new StringBuilder();
		//		result.append("{ datas:[");
		//		String sep = "";
		//		for (int i = 0; i < datas.size(); i++) {
		//			final DataSet<?, ?> serie = datas.get(i);
		//			result.append(sep);
		//			printSerie(serie, panelConf.getLabels().get(i), stackMap, result, labels);
		//			sep = ",\n ";
		//		}
		//		result.append("],\n labels:[");
		//		sep = "";
		//		for (int i = 0; i < labels.size(); i++) {
		//			result.append(sep);
		//			result.append("[").append(i).append(",");
		//			result.append("'").append(labels.get(i)).append("']");
		//			sep = ",";
		//		}
		//		result.append("] ");
		//		appendColors(panelConf.getColors(), datas.size(), result);
		//		result.append("}");
		//		return result.toString();
		return "";

	}

	//	private void appendColors(final String colors, final int nbSeries, final StringBuilder result) {
	//		if ("DEFAULT".equals(colors)) {
	//			//default on ne fait rien
	//		} else if ("RAINBOW".equals(colors)) {
	//			final Color[] mainColors = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, new Color(75, 0, 130), new Color(238, 130, 238) };
	//			appendInterpolatedColors(mainColors, nbSeries, "HSL", result);
	//		} else if ("SPECTRUM".equals(colors)) {
	//			final Color[] mainColors = { new Color(230, 31, 30), new Color(230, 230, 30), new Color(30, 230, 30), new Color(30, 230, 230), new Color(30, 30, 230), new Color(230, 30, 230), new Color(230, 30, 31) };
	//			appendInterpolatedColors(mainColors, nbSeries, "CATMULL", result);
	//		} else if ("RED2GREEN".equals(colors)) {
	//			final Color[] mainColors = { new Color(255, 51, 51), new Color(255, 255, 51), new Color(51, 153, 51) };
	//			appendInterpolatedColors(mainColors, nbSeries, "HSL", result);
	//		} else if ("HEAT".equals(colors)) {
	//			final Color[] mainColors = { new Color(255, 51, 51), new Color(255, 255, 51), new Color(51, 153, 51), new Color(51, 153, 255) };
	//			appendInterpolatedColors(mainColors, nbSeries, "HSL", result);
	//		} else if ("GREEN:INTENSITY".equals(colors)) {
	//			final Color[] mainColors = { new Color(0, 170, 85), new Color(240, 240, 170) };
	//			appendInterpolatedColors(mainColors, nbSeries, "LINEAR", result);
	//		} else {
	//			throw new KRuntimeException("code couleur inconnu : " + colors + " (codes: DEFAULT, RAINBOW, HEAT)");
	//		}
	//	}
	//
	//	private RGBInterpolation obtainRGBInterpolation(final String colors) {
	//		if (colors.endsWith("LINEAR")) {
	//			return new RGBLinearInterpolation();
	//		} else if (colors.endsWith("CATMULL")) {
	//			return new RGBCatmullInterpolation();
	//		} else if (colors.endsWith("BSPLINE")) {
	//			return new RGBCatmullInterpolation();
	//		} else if (colors.endsWith("HSL")) {
	//			return new HSLLinearInterpolation();
	//		} else {
	//			throw new KRuntimeException("code d'interpolation : " + colors + " (codes: LINEAR, BSPLINE, CATMULL, HSL)");
	//		}
	//	}
	//
	//	private void appendInterpolatedColors(final Color[] mainColors, final int nbSeries, final String interpolationCode, final StringBuilder result) {
	//		final RGBInterpolation rgbInterpolation = obtainRGBInterpolation(interpolationCode);
	//		result.append(",\n colors: [");
	//		String sep = "";
	//		rgbInterpolation.setMainColors(mainColors);
	//		final List<Color> colors = rgbInterpolation.getColors(nbSeries);
	//		for (final Color color : colors) {
	//			result.append(sep);
	//			result.append("'#");
	//			result.append(Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1));
	//			result.append("'");
	//			sep = ",";
	//		}
	//		result.append("]");
	//	}
	//
	//	//	private void appendInterpolatedColors(final Color[] mainColors, final int nbSeries, final StringBuilder result) {
	//	//		result.append(",\n colors: [");
	//	//		String sep = "";
	//	//		for (int i = 0; i < nbSeries; i++) {
	//	//			result.append(sep);
	//	//			result.append("'#");
	//	//
	//	//			final int[] rgb = hsl2rgb((int) Math.round(linear(i / (nbSeries - 1f), 0, 120)), 100, 60);
	//	//			final Color color = new Color(rgb[0], rgb[1], rgb[2]);
	//	//			result.append(Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1));
	//	//			result.append("'");
	//	//			sep = ",";
	//	//		}
	//	//		result.append("]");
	//	//	}
	//
	//	//	private void appendInterpolatedColors(final Color[] mainColors, final int nbSeries, final StringBuilder result) {
	//	//		final RGBBezierSpline bSpline = new RGBBezierSpline(mainColors);
	//	//
	//	//		result.append(",\n colors: [");
	//	//		String sep = "";
	//	//		for (int i = 0; i < nbSeries; i++) {
	//	//			result.append(sep);
	//	//			result.append("'#");
	//	//			final Color color = bSpline.getColor(i / (nbSeries - 1f));
	//	//			result.append(Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1));
	//	//			result.append("'");
	//	//			sep = ",";
	//	//		}
	//	//		result.append("]");
	//	//	}
	//
	//	//	private void appendInterpolatedColors(final Color[] mainColors, final int nbSeries, final StringBuilder result) {
	//	//		final RGBBezierSpline bSpline = new RGBBezierSpline(mainColors);
	//	//
	//	//		result.append(",\n colors: [");
	//	//		String sep = "";
	//	//		for (int i = 0; i < nbSeries; i++) {
	//	//			result.append(sep);
	//	//			result.append("'#");
	//	//			final Color color = bSpline.getColor(i / (nbSeries - 1f));
	//	//			result.append(Integer.toHexString(color.getRGB() & 0xffffff | 0x1000000).substring(1));
	//	//			result.append("'");
	//	//			sep = ",";
	//	//		}
	//	//		result.append("]");
	//	//	}
	//
	//	private void printSerie(final DataSet<?, ?> serie, final String serieLabel, final Map<Object, Double> stackMap, final StringBuilder result, final List<String> labels) {
	//
	//		result.append("{ label:'").append(serieLabel).append("', data:[");
	//		String sep = "";
	//		for (int i = 0; i < serie.getLabels().size(); i++) {
	//			result.append(sep);
	//			result.append("[");
	//			final Object label = serie.getLabels().get(i);
	//			if (label instanceof Date) {
	//				result.append(((Date) label).getTime());
	//			} else {
	//				final String strLabel = String.valueOf(label);
	//				final int index = labels.indexOf(strLabel);
	//				if (index >= 0) {
	//					result.append(index);
	//				} else {
	//					labels.add(strLabel);
	//					result.append(labels.size() - 1);
	//				}
	//			}
	//			result.append(",");
	//			final Double oldValue = stackMap.get(label);
	//			final Double newValue = (Double) serie.getValues().get(i);
	//			final double sumValue = (oldValue != null ? oldValue : 0) + (newValue != null ? newValue : 0);
	//			stackMap.put(label, sumValue);
	//			result.append(String.valueOf(sumValue));
	//			result.append("]");
	//			sep = ",";
	//		}
	//		result.append("]}");
	//	}
}
