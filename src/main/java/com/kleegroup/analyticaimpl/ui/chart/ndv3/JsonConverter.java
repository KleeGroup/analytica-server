/**
 * 
 */
package com.kleegroup.analyticaimpl.ui.chart.ndv3;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.google.gson.Gson;

/**
 * @author statchum
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public class JsonConverter implements Converter {

	/** {@inheritDoc} */
	@Override
	public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) {
		throw new ConverterException("Forbiden operation!");
	}

	/** {@inheritDoc} */
	@Override
	public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object dataObject) {
		return new Gson().toJson(dataObject);
	}

}
