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
package com.kleegroup.analyticaimpl.ui.chart.amchart;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Convertisseur pour les graphiques.
 * 
 * @author npiedeloup
 * @version $Id: LineChartConverter.java,v 1.1 2012/04/17 13:05:42 npiedeloup Exp $
 */
public final class LineChartConverter implements Converter {

	/** {@inheritDoc} */
	public Object getAsObject(final FacesContext context, final UIComponent component, final String strValue) {
		throw new ConverterException("Opération impossible");
	}

	/** {@inheritDoc} */
	public String getAsString(final FacesContext context, final UIComponent component, final Object objValue) {
		//		final AnalyticaPanelConf panelConf = (AnalyticaPanelConf) objValue;
		//		final List<DataSet<?, ?>> datas = (List<DataSet<?, ?>>) component.getAttributes().get("datas");
		//		//---------------------------------------------------------------------
		//		/*
		//		 var chartData = {datas:[{
		//		        year: 2005,
		//		        income: 23.5,
		//		        expenses: 18.1
		//		    }, {
		//		        year: 2006,
		//		        income: 26.2,
		//		        expenses: 22.8
		//		    }, {
		//		        year: 2007,
		//		        income: 30.1,
		//		        expenses: 23.9
		//		    }, {
		//		        year: 2008,
		//		        income: 29.5,
		//		        expenses: 25.1
		//		    }, {
		//		        year: 2009,
		//		        income: 24.6,
		//		        expenses: 25.0
		//		    }],
		//		    xLabel:'year',
		//		    series[{title:'', field:''},{title:'', field:''}]};
		//
		//		 */
		//		final SortedMap<Object, Map<DataKey, Object>> allDatas = convertDatas(datas);
		//
		//		final StringBuilder result = new StringBuilder();
		//		result.append("{ datas:[");
		//		String sep = "";
		//		final List<DataKey> dataKeys = panelConf.getQuery().getKeys();
		//		for (final Map.Entry<Object, Map<DataKey, Object>> entry : allDatas.entrySet()) {
		//			result.append(sep);
		//			result.append("{key:");
		//			final Object key = entry.getKey();
		//			if (key instanceof Date) {
		//				result.append(((Date) key).getTime());
		//			} else {
		//				result.append("'").append(String.valueOf(key)).append("'");
		//			}
		//			for (final DataKey dataKey : dataKeys) {
		//				final Object value = entry.getValue().get(dataKey);
		//				if (value != null) {
		//					result.append(",");
		//					result.append(encode(dataKey)).append(":");
		//					if (value instanceof Double) {
		//						final double roundedValue = Math.round((Double) value * 100) / 100d;
		//						result.append(String.valueOf(roundedValue));
		//					} else {
		//						result.append(String.valueOf(value));
		//					}
		//
		//				}
		//				result.append("");
		//			}
		//			result.append("}");
		//			sep = ",\n ";
		//		}
		//		result.append("],\n xlabel:'key'");
		//		appendColors(panelConf.getColors(), datas.size(), result);
		//		result.append(",\n series:[");
		//		sep = "";
		//		final List<String> labels = panelConf.getLabels();
		//		for (int i = 0; i < dataKeys.size(); i++) {
		//			result.append(sep);
		//			result.append("{title:'").append(labels.get(i)).append("',");
		//			result.append("field:'").append(encode(dataKeys.get(i))).append("'}");
		//			sep = ",";
		//		}
		//		result.append("]}");
		//		return result.toString();
		return "";
	}

	//	private String encode(final DataKey dataKey) {
	//		return dataKey.getMetricKey().id() + "_" + dataKey.getType().name();
	//	}
	//
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
	//	private SortedMap<Object, Map<DataKey, Object>> convertDatas(final List<DataSet<?, ?>> datas) {
	//		final SortedMap<Object, Map<DataKey, Object>> allDatas = new TreeMap<Object, Map<DataKey, Object>>();
	//		for (final DataSet<?, ?> dataSet : datas) {
	//			for (int i = 0; i < dataSet.getLabels().size(); i++) {
	//				final Object label = dataSet.getLabels().get(i);
	//				final Map<DataKey, Object> labelValues = obtainLabelValues(label, allDatas);
	//				labelValues.put(dataSet.getKey(), dataSet.getValues().get(i));
	//			}
	//		}
	//		return allDatas;
	//	}
	//
	//	private Map<DataKey, Object> obtainLabelValues(final Object label, final SortedMap<Object, Map<DataKey, Object>> allDatas) {
	//		Map<DataKey, Object> labelValues = allDatas.get(label);
	//		if (labelValues == null) {
	//			labelValues = new HashMap<DataKey, Object>();
	//			allDatas.put(label, labelValues);
	//		}
	//		return labelValues;
	//	}

}
