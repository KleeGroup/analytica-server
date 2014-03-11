package com.kleegroup.analyticaimpl.uiswing.collector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

public class ProcessStatsTree implements ProcessStatsCollection<ProcessStatsNode> {
	private static final long serialVersionUID = -5994766764834518173L;

	private final Map<String, ProcessStatsNode> methodStatsRoot = new HashMap<String, ProcessStatsNode>(); //Map de ProcessStatsNode

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
		Assertion.notNull(methods);
		Assertion.precondition(methods.length >= 1, "La liste des méthodes ne doit pas être vide");
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
			throw new KRuntimeException("methodStatsNode ne doit pas être null");
		}
		return methodStatsNode.getProcessStats();
	}

	public void merge(final ProcessStatsCollection other) {
		Assertion.notNull(other);
		Assertion.invariant(other instanceof ProcessStatsTree, "On ne peut merger que des ProcessStatsCollection de même type, impossible de merger {0} avec {1}", this.getClass().getName(), other.getClass().getName());
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
