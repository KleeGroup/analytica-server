/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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

public class PerfCollectorFactory {
	private static final PerfCollectorFactory INSTANCE = new PerfCollectorFactory();

	private static PerfCollector PERF_COLLECTOR = new PerfSilenceCollector();

	/*static {
	    String defaultPerfCollector = PerfProcessCollector.class.getName();
	    String perfCollectorName = null;
	    try {
	        //perfCollectorName = getString("KANAP.GRABBER.PERF_COLLECTOR", defaultPerfCollector);
	        PERF_COLLECTOR = (PerfCollector) Class.forName(perfCollectorName).newInstance();
	    } catch (Throwable t) {
	        throw new RuntimeException("PerfCollector non trouvé (" + perfCollectorName + ")", t); //Cette exception arretera le chargement de la class et donc l'appli.
	    }
	}*/

	public static PerfCollectorFactory getSingleton() {
		return INSTANCE;
	}

	private PerfCollectorFactory() {
		//constructeur privé
	}

	public void setPerfCollector(final PerfCollector perfCollector) {
		PERF_COLLECTOR = perfCollector;
	}

	public PerfCollector getPerfCollector() {
		return PERF_COLLECTOR;
	}
}
