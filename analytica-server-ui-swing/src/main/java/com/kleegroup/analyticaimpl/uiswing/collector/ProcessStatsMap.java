package com.kleegroup.analyticaimpl.uiswing.collector;

import io.vertigo.kernel.lang.Assertion;

import java.util.HashMap;
import java.util.Map;

public class ProcessStatsMap implements ProcessStatsCollection<ProcessStats> {

	private static final long serialVersionUID = 2967543630766127354L;

	private final Map<String, ProcessStats> processStatsMap = new HashMap<String, ProcessStats>(); //Map key:nom de process  value:ProcessStats

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

	public void merge(final ProcessStatsCollection<ProcessStats> other) {
		Assertion.checkNotNull(other);
		Assertion.checkArgument(other instanceof ProcessStatsMap, "On ne peut merger que des ProcessStatsCollection de même type, impossible de merger {0} avec {1}", this.getClass().getName(), other.getClass().getName());
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
