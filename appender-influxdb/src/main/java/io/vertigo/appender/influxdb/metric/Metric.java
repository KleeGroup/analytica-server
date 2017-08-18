/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2017, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiere - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.appender.influxdb.metric;

import java.time.Instant;

/**
 * Interface décrivant un résultat de metric.
 *
 * @author mlaroche, pchretien
 */
public final class Metric {

	public enum Status {
		/** Exécution OK*/
		SUCCESS,
		/** Erreur lors de l'exécution*/
		ERROR;
	}

	private final Instant measureInstant;
	private final String name;
	private final String topic;
	private final Double value;//migth be null
	private final Status status;

	Metric(
			final Instant measureTime,
			final String name,
			final String topic,
			final Double value,
			final Status status) {
		measureInstant = measureTime;
		this.name = name;
		this.topic = topic;
		this.value = value;
		this.status = status;

	}

	public Instant getMeasureInstant() {
		return measureInstant;
	}

	public String getName() {
		return name;
	}

	public String getTopic() {
		return topic;
	}

	public Double getValue() {
		return value;
	}

	public Status getStatus() {
		return status;
	}

}
