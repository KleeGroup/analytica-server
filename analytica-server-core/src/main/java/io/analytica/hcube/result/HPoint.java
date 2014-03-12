package io.analytica.hcube.result;

import io.analytica.hcube.cube.HMetric;

import java.util.Date;

/*
 * Couple(date, metrique) 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public interface HPoint {
	/**
	 * @return Date
	 */
	Date getDate();

	/**
	 * @return Metric
	 */
	HMetric getMetric();
}
