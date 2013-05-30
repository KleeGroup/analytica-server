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
package com.kleegroup.analyticaimpl.ui.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * @author npiedeloup
 * @version $Id: AnalyticaContext.java,v 1.1 2012/03/15 18:05:42 npiedeloup Exp $
 */
@SessionScoped()
@ManagedBean(name = "analyticaContext")
public final class AnalyticaContext implements Serializable {
	private static final long serialVersionUID = 855858989288016205L;

	private boolean initialize = false;
	private boolean aggregateTime = true;
	private boolean aggregateWhat = true;

	public final boolean isInitialize() {
		return initialize;
	}

	public final void initialized() {
		initialize = true;
	}

	public final boolean isAggregateTime() {
		return aggregateTime;
	}

	public final void setAggregateTime(final boolean aggregateTime) {
		this.aggregateTime = aggregateTime;
	}

	public final boolean isAggregateWhat() {
		return aggregateWhat;
	}

	public final void setAggregateWhat(final boolean aggregateWhat) {
		this.aggregateWhat = aggregateWhat;
	}

}
