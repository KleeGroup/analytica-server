/**
 * 
 */
package com.kleegroup.analyticaimpl.ui.chart.ndv3;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import kasperimpl.spaces.spaces.kraft.DataPoint;

/**
 * @author statchum
 * @version $Id: codetemplates.xml,v 1.2 2011/06/21 14:33:16 npiedeloup Exp $
 */
public class Ndv3LineChartConverter implements Converter {

	/** {@inheritDoc} */
	@Override
	public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) {
		throw new ConverterException("Forbiden Operation");
	}

	/** {@inheritDoc} */
	@Override
	public String getAsString(final FacesContext arg0, final UIComponent component, final Object dataObject) {
		final Map<String, List<DataPoint>> datas = (Map<String, List<DataPoint>>) dataObject;
		List<DataPoint> dataList;

		/*[
			Bar Charts				  {"key":"NOM de la SERIE1",
									   "values":[[x,y],[],...,[]]
									  },
									  {"key":"NOM de la SERIE2",
									   "values":[[],[],...,[]]
									  },
									  {"key":"NOM de la SERIE3",
									   "values":[[],[],...,[]]
									  },
									  .
									  .
									  .
									  ,
									  {"key":"NOM de la SERIEN",
									   "values":[[],[],...,[]]
									  }
									]
		*/

		final StringBuilder result = new StringBuilder();
		result.append("[");

		final Set keys = datas.keySet();
		String key;
		final Iterator<String> iterator = keys.iterator();
		int j = 0;
		while (iterator.hasNext()) {
			j++;
			key = iterator.next();
			dataList = datas.get(key);

			result.append("{ key : ");
			final String cle = '\"' + key + '\"' + " , ";
			result.append(cle);
			result.append("values : [");

			int i = 0;
			for (final DataPoint point : dataList) {
				i++;
				result.append("[");
				final String str = point.getDate().getTime() + " , " + point.getValue();
				result.append(str);
				result.append("]");
				if (i < dataList.size()) {
					result.append(",");
				}
			}
			result.append("]");
			result.append("}");
			if (j < datas.size()) {
				result.append(",");
			}
		}
		result.append("]");
		System.out.println(result.toString());
		return result.toString();
	}
}
