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

import io.vertigo.kernel.lang.Assertion;

import java.io.Serializable;

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
		Assertion.checkState(processId.equals(other.processId), "On ne peut pas fusionner deux MethodStats qui ne portent pas sur la même méthode: can''t merge {0} in {1}", other.processId, processId);
		hits += other.hits;
		durationsMin = Math.min(durationsMin, other.durationsMin);
		durationsMax = Math.max(durationsMax, other.durationsMax);
		durationsSum += other.durationsSum;
		durationsSqrSum += other.durationsSqrSum;
		durationsLast = Math.max(durationsLast, other.durationsLast);
	}
}
