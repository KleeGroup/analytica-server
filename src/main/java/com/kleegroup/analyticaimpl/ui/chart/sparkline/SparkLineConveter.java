package com.kleegroup.analyticaimpl.ui.chart.sparkline;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * @author statchum
 *
 */

public class SparkLineConveter implements Converter {

	@Override
	public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) {
		throw new ConverterException("Forbiden Operation!");
	}

	@Override
	public String getAsString(final FacesContext context, final UIComponent component, final Object objValue) {
		//		final AnalyticaPanelConf panelConf = (AnalyticaPanelConf) objValue;
		//		final List<DataSet<?, ?>> datas = (List<DataSet<?, ?>>) component.getAttributes().get("datas");
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
		//
		//		result.append("]}");
		//		return result.toString();
		return "";

	}


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
