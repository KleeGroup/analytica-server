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

import com.kleegroup.analytica.hcube.dimension.WhatDimension;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;

/**
 * Metric d'un cube.
 * Une metric possède un type et une valeur.
 *  
 * @author npiedeloup, pchretien
 * @version $Id: WhatPositionBuilderBean.java,v 1.2 2012/03/22 09:16:40 npiedeloup Exp $
 */
public final class WhatPositionBuilderBean {
	private String whatPosition;
	private WhatDimension whatDimension;

	/**
	 * @return Build de metric
	 */
	public WhatPosition buildWhatPosition() {
		return new WhatPosition(whatPosition, whatDimension);
	}

	public final void setWhdCd(final String tidCd) {
		whatDimension = WhatDimension.valueOf(tidCd);
	}

	public final void setWhatPosition(final String whatPosition) {
		this.whatPosition = whatPosition;
	}
}
