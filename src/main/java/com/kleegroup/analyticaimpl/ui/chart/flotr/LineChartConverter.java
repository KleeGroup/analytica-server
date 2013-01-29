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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * @version $Id: LineChartConverter.java,v 1.1 2012/04/17 13:05:42 npiedeloup Exp $
 */
public final class LineChartConverter implements Converter {

	/** {@inheritDoc} */
	public Object getAsObject(final FacesContext context, final UIComponent component, final String strValue) {
		throw new ConverterException("Opération impossible");
	}

	/** {@inheritDoc} */
	public String getAsString(final FacesContext context, final UIComponent component, final Object objValue) {
		final AnalyticaPanelConf panelConf = (AnalyticaPanelConf) objValue;
		final List<DataSet<?, ?>> datas = (List<DataSet<?, ?>>) component.getAttributes().get("datas");
		//---------------------------------------------------------------------
		final List<String> labels = new ArrayList<String>();
		final StringBuilder result = new StringBuilder();
		result.append("{ datas:[");
		String sep = "";
		for (int i = 0; i < datas.size(); i++) {
			final DataSet<?, ?> serie = datas.get(i);
			result.append(sep);
			printSerie(serie, panelConf.getLabels().get(i), result, labels);
			sep = ",\n ";
		}
		result.append("],\n labels:[");
		sep = "";
		for (int i = 0; i < labels.size(); i++) {
			result.append(sep);
			result.append("[").append(i).append(",");
			result.append("'").append(labels.get(i)).append("']");
			sep = ",";
		}
		result.append("]}");
		return result.toString();
	}

	private void printSerie(final DataSet<?, ?> serie, final String serieLabel, final StringBuilder result, final List<String> labels) {
		result.append("{ label:'").append(serieLabel).append("', data:[");
		String sep = "";
		for (int i = 0; i < serie.getLabels().size(); i++) {
			result.append(sep);
			result.append("[");
			final Object label = serie.getLabels().get(i);
			if (label instanceof Date) {
				result.append(((Date) label).getTime());
			} else {
				final String strLabel = String.valueOf(label);
				final int index = labels.indexOf(strLabel);
				if (index >= 0) {
					result.append(index);
				} else {
					labels.add(strLabel);
					result.append(labels.size() - 1);
				}
			}
			result.append(",");
			result.append(String.valueOf(serie.getValues().get(i)));
			result.append("]");
			sep = ",";
		}
		result.append("]}");
	}
}
