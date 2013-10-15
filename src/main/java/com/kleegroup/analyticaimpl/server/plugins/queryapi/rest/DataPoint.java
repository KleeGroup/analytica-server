package com.kleegroup.analyticaimpl.server.plugins.queryapi.rest;

import java.util.Date;

import vertigo.kernel.lang.Assertion;

/*
 * Couple(date, metrique)
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public final class DataPoint {
	private final Date date;
	private final long x;
	private final Double y;

	public DataPoint(final Date date, final double value) {
		Assertion.checkNotNull(date);
		//---------------------------------------------------------------------
		this.date = date;
		x = date.getTime();
		y = Double.isNaN(value) ? null : value;
	}

	/**
	 * @return Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return time in seconds
	 */
	public long getTimeInS() {
		return x;
	}

	/**
	 * @return value
	 */
	public Double getValue() {
		return y;
	}
}
