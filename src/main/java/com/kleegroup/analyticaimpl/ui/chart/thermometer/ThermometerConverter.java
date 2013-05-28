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
package com.kleegroup.analyticaimpl.ui.chart.thermometer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Convertisseur pour les graphiques.
 * 
 * @author npiedeloup
 * @version $Id: ThermometerConverter.java,v 1.2 2012/05/29 10:37:35 npiedeloup Exp $
 */
public final class ThermometerConverter implements Converter {

	/** {@inheritDoc} */
	public Object getAsObject(final FacesContext context, final UIComponent component, final String strValue) {
		throw new ConverterException("Opération impossible");
	}

	/** {@inheritDoc} */
	public String getAsString(final FacesContext context, final UIComponent component, final Object objValue) {
		//		final AnalyticaPanelConf panelConf = (AnalyticaPanelConf) objValue;
		//		final List<Data> datas = (List<Data>) component.getAttributes().get("datas");
		//		Assertion.precondition(datas.size() == 3, "Le composant thermometer nécessite 3 données ordonnées : min, max, value");
		//
		//		//---------------------------------------------------------------------
		//		final StringBuilder result = new StringBuilder();
		//		final Data min = datas.get(0);
		//		final Data value = datas.get(1);
		//		final Data max = datas.get(2);
		//		result.append("{ \"min\" :").append(round(min.getValue(), 2));
		//		result.append(", \"max\" :").append(round(max.getValue(), 2));
		//		result.append(", \"value\" :").append(round(value.getValue(), 2));
		//		result.append(" }");
		//		return result.toString();
		return "";

	}

	//	private double round(final double value, final int nbDecimal) {
	//		final double mult = Math.pow(10, nbDecimal);
	//		return Math.round(value * mult) / mult;
	//	}
}
