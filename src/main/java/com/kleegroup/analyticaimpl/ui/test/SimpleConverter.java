/**
 * 
 */
package com.kleegroup.analyticaimpl.ui.test;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author statchum
 *
 */
public class SimpleConverter implements Converter {


	@Override
	public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) {
		throw new ConverterException("Forbiden!!");
	}
	@Override
	public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object arg2) {
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(arg2);
	}

}
