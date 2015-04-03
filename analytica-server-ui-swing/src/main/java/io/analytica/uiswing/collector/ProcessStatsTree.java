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

import java.lang.RuntimeException;
import io.vertigo.lang.Assertion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProcessStatsTree implements ProcessStatsCollection<ProcessStatsNode> {
	private static final long serialVersionUID = -5994766764834518173L;

	//Map de ProcessStatsNode
	private final Map<String, ProcessStatsNode> methodStatsRoot = new HashMap<>();

	@Override
	public Map<String, ProcessStatsNode> getResults() {
		return methodStatsRoot;
	}

	public void addRequest(final List methodQueue, final long duration) {
		final String[] methods = (String[]) methodQueue.toArray(new String[methodQueue.size()]);
		getProcessStatsByProcesseQueue(methods).addHit(duration);
	}

	public void addRequest(final List methodQueue, final String method1, final long duration) {
		final String[] methods = (String[]) methodQueue.toArray(new String[methodQueue.size() + 1]);
		methods[methods.length - 1] = method1;
		getProcessStatsByProcesseQueue(methods).addHit(duration);
	}

	public void addRequest(final List methodQueue, final String method1, final String method2, final long duration) {
		final String[] methods = (String[]) methodQueue.toArray(new String[methodQueue.size() + 2]);
		methods[methods.length - 2] = method1;
		methods[methods.length - 1] = method2;
		getProcessStatsByProcesseQueue(methods).addHit(duration);
	}

	public void addRequest(final String method1, final String method2, final String method3, final long duration) {
		final String[] methods = { method1, method2, method3 };
		getProcessStatsByProcesseQueue(methods).addHit(duration);
	}

	ProcessStats getProcessStatsByProcesseQueue(final String[] methods) {
		Assertion.checkNotNull(methods);
		Assertion.checkArgument(methods.length >= 1, "La liste des méthodes ne doit pas être vide");
		Map<String, ProcessStatsNode> mapNextProcessStats = methodStatsRoot;
		ProcessStatsNode methodStatsNode = null;
		ProcessStatsNode upperProcessStatsNode;
		ProcessStatsNode methodStatsNodeRoot = null;
		final int length = methods.length;
		String methodId;
		for (int i = 0; i < length; i++) {
			methodId = methods[i];
			if (methodId == null) {
				break;
			}
			upperProcessStatsNode = methodStatsNode;
			methodStatsNode = mapNextProcessStats.get(methodId);
			if (methodStatsNode == null) {
				methodStatsNode = new ProcessStatsNode(methodId, upperProcessStatsNode, methodStatsNodeRoot);
				mapNextProcessStats.put(methodId, methodStatsNode);
			}
			mapNextProcessStats = methodStatsNode.getSousProcessStatsNode();
			if (i == 0) {
				methodStatsNodeRoot = methodStatsNode;
			}
		}
		if (methodStatsNode == null) {
			throw new RuntimeException("methodStatsNode ne doit pas être null");
		}
		return methodStatsNode.getProcessStats();
	}

	@Override
	public void merge(final ProcessStatsCollection other) {
		Assertion.checkNotNull(other);
		Assertion.checkArgument(other instanceof ProcessStatsTree, "On ne peut merger que des ProcessStatsCollection de même type, impossible de merger {0} avec {1}", this.getClass().getName(), other.getClass().getName());
		final Map otherProcessStatsRoot = other.getResults();
		ProcessStatsNode otherProcessStatsNode;
		ProcessStatsNode currentProcessStatsNode;
		Map.Entry entry;
		String method;
		for (final Iterator it = otherProcessStatsRoot.entrySet().iterator(); it.hasNext();) {
			entry = (Map.Entry) it.next();
			method = (String) entry.getKey();
			currentProcessStatsNode = methodStatsRoot.get(method);
			otherProcessStatsNode = (ProcessStatsNode) entry.getValue();
			if (currentProcessStatsNode == null && otherProcessStatsNode != null) {
				methodStatsRoot.put(method, otherProcessStatsNode);
			} else if (currentProcessStatsNode != null && otherProcessStatsNode != null) {
				currentProcessStatsNode.merge(otherProcessStatsNode);
			}
		}
	}
}
