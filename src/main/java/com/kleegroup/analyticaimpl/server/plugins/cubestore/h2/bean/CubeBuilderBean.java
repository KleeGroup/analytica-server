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
package com.kleegroup.analyticaimpl.server.plugins.cubestore.h2.bean;

import java.util.Date;

import kasper.kernel.lang.Builder;

import com.kleegroup.analytica.server.query.TimeDimension;
import com.kleegroup.analytica.server.query.WhatDimension;
import com.kleegroup.analyticaimpl.server.cube.CubeBuilder;
import com.kleegroup.analyticaimpl.server.cube.CubePosition;
import com.kleegroup.analyticaimpl.server.cube.TimePosition;
import com.kleegroup.analyticaimpl.server.cube.WhatPosition;

/**
 * Key d'un cube.
 *  
 * @author npiedeloup, pchretien
 * @version $Id: CubeBuilderBean.java,v 1.5 2012/10/16 16:22:30 pchretien Exp $
 */
public final class CubeBuilderBean implements Builder<CubeBuilder> {
	private long cubId;
	private Date time;
	private TimeDimension timeDimension;
	private String what;
	private WhatDimension whatDimension;

	/**
	 * @return Build de metric
	 */
	public CubeBuilder build() {
		final TimePosition timePosition = new TimePosition(time, timeDimension);
		final WhatPosition whatPosition = new WhatPosition(what, whatDimension);
		CubePosition key = new CubePosition(timePosition, whatPosition);
		return new CubeBuilder(key);
	}

	public final long getCubId() {
		return cubId;
	}

	public final void setCubId(final long cubId) {
		this.cubId = cubId;
	}

	public final void setTidCd(final String tidCd) {
		timeDimension = TimeDimension.valueOf(tidCd);
	}

	public final void setWhdCd(final String whdCd) {
		whatDimension = WhatDimension.valueOf(whdCd);
	}

	public final void setTimePosition(final Date timePosition) {
		time = timePosition;
	}

	public final void setWhatPosition(final String whatPosition) {
		what = whatPosition;
	}
}
