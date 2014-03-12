package io.analytica.server.plugins.queryapi.rest;

import io.vertigo.kernel.lang.Assertion;

import java.util.Date;
import java.util.Map;

/*
 * Couple(date, metriques)
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public final class TimedDataSerie {
	private final Date date;
	private final long time;
	private final Map<String, String> values;

	public TimedDataSerie(final Date date, final Map<String, String> values) {
		Assertion.checkNotNull(date);
		//---------------------------------------------------------------------
		this.date = date;
		time = date.getTime();
		this.values = values;
	}
}
