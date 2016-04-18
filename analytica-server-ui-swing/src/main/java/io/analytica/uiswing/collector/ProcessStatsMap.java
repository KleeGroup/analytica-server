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

import io.vertigo.lang.Assertion;

import java.util.HashMap;
import java.util.Map;

public class ProcessStatsMap implements ProcessStatsCollection<ProcessStats> {

	private static final long serialVersionUID = 2967543630766127354L;

	private final Map<String, ProcessStats> processStatsMap = new HashMap<>(); //Map key:nom de process  value:ProcessStats

	@Override
	public Map<String, ProcessStats> getResults() {
		return processStatsMap;
	}

	public void addRequest(final String process, final long duration) {
		ProcessStats processStats = processStatsMap.get(process);
		if (processStats == null) {
			processStats = new ProcessStats(process);
			processStatsMap.put(process, processStats);
		}
		processStats.addHit(duration);
	}

	@Override
	public void merge(final ProcessStatsCollection<ProcessStats> other) {
		Assertion.checkNotNull(other);
		Assertion.checkArgument(other instanceof ProcessStatsMap, "On ne peut merger que des ProcessStatsCollection de méme type, impossible de merger {0} avec {1}", this.getClass().getName(), other.getClass().getName());
		final Map<String, ProcessStats> otherResults = other.getResults();
		ProcessStats otherProcessStats;
		ProcessStats currentProcessStats;
		String process;
		for (final Map.Entry<String, ProcessStats> entry : otherResults.entrySet()) {
			process = entry.getKey();
			currentProcessStats = processStatsMap.get(process);
			otherProcessStats = entry.getValue();
			if (currentProcessStats == null && otherProcessStats != null) {
				processStatsMap.put(process, otherProcessStats);
			} else if (currentProcessStats != null && otherProcessStats != null) {
				currentProcessStats.merge(otherProcessStats);
			}
		}
	}
}
