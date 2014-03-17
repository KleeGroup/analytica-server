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
package io.analytica.uiswing.collector;

import java.util.Collections;
import java.util.Map;

public class PerfSilenceCollector implements PerfCollector {

	public final void clearResults(final String moduleName) {
		//rien
	}

	public final void clearResults() {
		//rien
	}

	public final ProcessStatsCollection getResults(final String moduleName) {
		return null;
	}

	public final Map getResults() {
		return Collections.emptyMap();
	}

	public final void onProcessError(final String moduleName, final String processId, final Object obj, final Object[] params, final Throwable throwable) {
		//rien
	}

	public final void onProcessFinish(final String moduleName, final String processId, final Object obj, final Object[] params, final Object ret, final long duration, final boolean success) {
		//rien
	}

	public final void onProcessStart(final String moduleName, final String processId, final Object obj, final Object[] params) {
		//rien
	}

	public StringBuffer print(final StringBuffer out) {
		return out;
	}
}
