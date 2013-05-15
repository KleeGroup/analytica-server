package com.kleegroup.analytica.hcube.result;

import java.util.Date;

import com.kleegroup.analytica.hcube.cube.HMetric;

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
