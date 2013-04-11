package com.kleegroup.analyticaimpl.uiswing.collector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProcessStatsNode implements Serializable {
	private static final long serialVersionUID = -6488173645257320235L;

	private final ProcessStats stats;
	private final ProcessStatsNode processStatsNodeRoot;
	private final ProcessStatsNode upperProcessStatsNode;
	private Map sousProcessStatsNode;

	public ProcessStatsNode(final String processId, final ProcessStatsNode upperProcessStatsNode, final ProcessStatsNode processStatsNodeRoot) {
		this.upperProcessStatsNode = upperProcessStatsNode;
		this.processStatsNodeRoot = processStatsNodeRoot;
		stats = new ProcessStats(processId);
	}

	public String getProcessId() {
		return stats.getProcessId();
	}

	public ProcessStatsNode getUpperProcessStatsNode() {
		return upperProcessStatsNode;
	}

	public ProcessStatsNode getProcessStatsNodeRoot() {
		return processStatsNodeRoot;
	}

	public boolean hasSousProcessStatsNode() {
		return sousProcessStatsNode != null && !sousProcessStatsNode.isEmpty();
	}

	public Map getSousProcessStatsNode() {
		if (sousProcessStatsNode == null) {
			sousProcessStatsNode = new HashMap();
		}
		return sousProcessStatsNode;
	}

	public ProcessStats getProcessStats() {
		return stats;
	}

	@Override
	public boolean equals(final Object o) {
		if (o != null && o instanceof ProcessStatsNode) {
			return getProcessId().equals(((ProcessStatsNode) o).getProcessId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getProcessId().hashCode();
	}

	public void merge(final ProcessStatsNode other) {
		if (other == null || !other.hasSousProcessStatsNode()) {
			return;
		}
		getProcessStats().merge(other.getProcessStats());

		final Map otherSousProcessStatsNode = other.getSousProcessStatsNode();
		ProcessStatsNode otherProcessStatsNode;
		ProcessStatsNode currentProcessStatsNode;
		Map.Entry entry;
		String process;
		for (final Iterator it = otherSousProcessStatsNode.entrySet().iterator(); it.hasNext();) {
			entry = (Map.Entry) it.next();
			process = (String) entry.getKey();
			currentProcessStatsNode = (ProcessStatsNode) sousProcessStatsNode.get(process);
			otherProcessStatsNode = (ProcessStatsNode) entry.getValue();
			if (currentProcessStatsNode == null && otherProcessStatsNode != null) {
				sousProcessStatsNode.put(process, otherProcessStatsNode);
			} else if (currentProcessStatsNode != null && otherProcessStatsNode != null) {
				currentProcessStatsNode.merge(otherProcessStatsNode);
			}
		}
	}
}
