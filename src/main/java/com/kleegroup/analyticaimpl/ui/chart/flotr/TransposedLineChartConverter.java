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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.kleegroup.analytica.server.data.DataSet;
import com.kleegroup.analyticaimpl.ui.controller.AnalyticaPanelConf;

/**
 * Convertisseur pour les graphiques.
 * 
 * @author npiedeloup
 * @version $Id: TransposedLineChartConverter.java,v 1.1 2012/04/17 13:05:42 npiedeloup Exp $
 */
public final class TransposedLineChartConverter implements Converter {

	/** {@inheritDoc} */
	public Object getAsObject(final FacesContext context, final UIComponent component, final String strValue) {
		throw new ConverterException("Opération impossible");
	}

	/** {@inheritDoc} */
	public String getAsString(final FacesContext context, final UIComponent component, final Object objValue) {
		final AnalyticaPanelConf panelConf = (AnalyticaPanelConf) objValue;
		final List<DataSet<String, Double>> datas = (List<DataSet<String, Double>>) component.getAttributes().get("datas");
		//---------------------------------------------------------------------
		final Map<String, SortedMap<String, Double>> values = new LinkedHashMap<String, SortedMap<String, Double>>();
		final SortedSet<String> labels = new TreeSet<String>();
		for (final DataSet<String, Double> serie : datas) {
			values.put(serie.getKey().toString(), indexSerie(serie));
			labels.addAll(serie.getLabels());
		}
		final StringBuilder result = new StringBuilder();
		result.append("{ datas:[");
		String sep = "";
		for (final String label : labels) {
			result.append(sep);

			result.append("{ label:'").append(label).append("', data:[");
			String sep2 = "";
			int i = 0;
			for (final SortedMap<String, Double> value : values.values()) {
				result.append(sep2);
				result.append("[");
				result.append(i);
				result.append(",");
				result.append(String.valueOf(value.get(label)));
				result.append("]");
				i++;
				sep2 = ",";
			}
			result.append("]}");
			sep = ",\n ";
		}
		result.append("], labels:[");
		sep = "";
		final Set<String> keys = values.keySet();
		int i = 0;
		for (final String key : keys) {
			result.append(sep);
			result.append("[").append(i).append(",");
			result.append("'").append(key).append("']");
			i++;
			sep = ",";
		}
		result.append("]}");
		return result.toString();
	}

	private SortedMap<String, Double> indexSerie(final DataSet<String, Double> serie) {
		final SortedMap<String, Double> result = new TreeMap<String, Double>();
		for (int i = 0; i < serie.getLabels().size(); i++) {
			result.put(serie.getLabels().get(i), serie.getValues().get(i));
		}
		return result;
	}

	private void printSerie(final DataSet<?, ?> serie, final StringBuilder result, final List<String> labels) {
		result.append("{ label:'").append(serie.getKey().toString()).append("', data:[");
		String sep = "";
		for (int i = 0; i < serie.getLabels().size(); i++) {
			result.append(sep);
			result.append("[");
			final Object label = serie.getLabels().get(i);
			if (label instanceof Date) {
				result.append(((Date) label).getTime());
			} else {
				result.append(obtainLabelIndex(label, labels));
			}
			result.append(",");
			result.append(String.valueOf(serie.getValues().get(i)));
			result.append("]");
			sep = ",";
		}
		result.append("]}");
	}

	private int obtainLabelIndex(final Object label, final List<String> labels) {
		final String strLabel = String.valueOf(label);
		final int index = labels.indexOf(strLabel);
		if (index >= 0) {
			return index;
		} else {
			labels.add(strLabel);
			return labels.size() - 1;
		}
	}
}
