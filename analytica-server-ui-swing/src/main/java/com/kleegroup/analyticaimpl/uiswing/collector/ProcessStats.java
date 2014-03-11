package com.kleegroup.analyticaimpl.uiswing.collector;

import java.io.Serializable;

import kasper.kernel.util.Assertion;

public class ProcessStats implements Serializable {
	private static final long serialVersionUID = 7617530768482922402L;

	private final String processId;
	private long hits = 0;
	private long durationsMin = Long.MAX_VALUE;
	private long durationsMax = Long.MIN_VALUE;
	private long durationsSum = 0;
	private long durationsSqrSum = 0;
	private long durationsLast = -1;
	private long durationsSumForAllMethods = -1;

	public ProcessStats(final String processId) {
		this.processId = processId;
	}

	public String getProcessId() {
		return processId;
	}

	public long getHits() {
		return hits;
	}

	public long getDurationsMax() {
		return durationsMax;
	}

	public long getDurationsMin() {
		return durationsMin;
	}

	public long getDurationsSum() {
		return durationsSum;
	}

	public long getDurationsMean() {
		if (hits > 0) {
			return durationsSum / hits;
		} else {
			return 0;
		}
	}

	public long getDurationsEcartType() {
		// formule ev (non exacte puisque qu'on ne connaît pas toutes les valeurs, mais estimation suffisante)
		// rq : écart type (ou sigma) se dit standard deviation en anglais
		return Math.round(Math.sqrt((durationsSqrSum - (double) durationsSum * durationsSum / hits) / ((double) hits - 1)));

		// formule nico (fausse car trop arrondie si n est faible)
		// return Math.round(Math.sqrt((double) durationsSqrSum / (double) hits - getDurationsMean() * getDurationsMean()));
	}

	public long getDurationsLast() {
		return durationsLast;
	}

	public long getDurationsPercentageComparedToAllMethods() {
		return durationsSumForAllMethods == 0 ? 0 : 100L * durationsSum / durationsSumForAllMethods;
	}

	public void setDurationsSumForAllMethods(final long durationsSumForAllMethods) {
		this.durationsSumForAllMethods = durationsSumForAllMethods;
	}

	public synchronized void addHit(final long duration) {
		if (duration < durationsMin) {
			durationsMin = duration;
		}
		if (duration > durationsMax) {
			durationsMax = duration;
		}
		hits++;
		durationsSum += duration;
		durationsSqrSum += duration * duration;
		durationsLast = duration;
	}

	public synchronized void merge(final ProcessStats other) {
		if (other == null) {
			return;
		}
		Assertion.invariant(processId.equals(other.processId), "On ne peut pas fusionner deux MethodStats qui ne portent pas sur la même méthode: can''t merge {0} in {1}", other.processId, processId);
		hits += other.hits;
		durationsMin = Math.min(durationsMin, other.durationsMin);
		durationsMax = Math.max(durationsMax, other.durationsMax);
		durationsSum += other.durationsSum;
		durationsSqrSum += other.durationsSqrSum;
		durationsLast = Math.max(durationsLast, other.durationsLast);
	}
}
