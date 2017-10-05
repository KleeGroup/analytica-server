package io.vertigo.analytics.server.events.process;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.vertigo.analytics.server.Assertion;

/**
 * Un processus est un evenement
 *   - declenche dans une application specifique
 *   - relatif a un type d'evenement (exemple : metrics des pages, des requetes sql, des mails envoyés, des services ...)
 *
 *	Un evenement est defini selon 3 axes
 *	 - when, quand a eu lieu l'evenement
 *	 - what, de quoi s'agit-il ?
 *	 - where, ou s'est passe l'evenement  ? sur quel serveur ?
 *
 *	[what]
 * - category (examples : sql, jpa, tasks)
 * - name (examples : /create/movies)

 * 	[when]
 * - start timestamp
 * - end   timestamp
 *
 * 	[data]
 * - list of measures
 * - list of tagss
 * - list of sub processes (0..*)
 *
 * @author pchretien, npiedeloup
 * @version $Id: KProcess.java,v 1.8 2012/10/16 17:18:26 pchretien Exp $
 */
public final class AProcess {
	/**
	 * REGEX used to define rules on category, mesaures and tags.
	 */
	public static final Pattern PROCESS_CATEGORY_REGEX = Pattern.compile("[a-z]+");
	public static final Pattern MEASURE_REGEX = Pattern.compile("[a-zA-Z][a-zA-Z0-9_-]+");
	public static final Pattern TAG_REGEX = Pattern.compile("[a-zA-Z][a-zA-Z0-9_-]+");

	public static final String CATEGORY_SEPARATOR = "/";
	private final String category; //ex : sql, page....

	private final String name; //what ex : accounts/search

	private final long start; //when
	private final long end; //when

	private final Map<String, Double> measures;
	private final Map<String, String> tags;
	private final List<AProcess> subProcesses;

	/**
	 * Constructor.
	 * @param category the category
	 * @param name  the name
	 * @param start the start instant
	 * @param end the end instant
	 * @param measures the measures
	 * @param tags the tags
	 * @param subProcesses the list of sub processes (0..*)
	 */
	AProcess(
			final String category,
			final String name,
			final Instant start,
			final Instant end,
			final Map<String, Double> measures,
			final Map<String, String> tags,
			final List<AProcess> subProcesses) {
		Assertion.checkNotNull(category, "the category of the process is required");
		Assertion.checkNotNull(name, "the name of the process is required");
		Assertion.checkNotNull(start, "the start is required");
		Assertion.checkNotNull(end, "the end is required");
		Assertion.checkNotNull(measures, "the measures are required");
		Assertion.checkNotNull(tags, "the tags are required");
		Assertion.checkNotNull(subProcesses, "the subProcesses are required");

		//---
		checkRegex(category, PROCESS_CATEGORY_REGEX, "process type");
		//	ckeckRegex(type, CATEGORY_REGEX, "category");
		measures.keySet()
				.forEach(measureName -> checkRegex(measureName, MEASURE_REGEX, "measure name"));
		tags.keySet()
				.forEach(tagName -> checkRegex(tagName, TAG_REGEX, "metadata name"));
		//---------------------------------------------------------------------
		this.category = category;
		this.name = name;
		this.start = start.toEpochMilli();
		this.end = end.toEpochMilli();
		this.measures = Collections.unmodifiableMap(new HashMap<>(measures));
		this.tags = Collections.unmodifiableMap(new HashMap<>(tags));
		this.subProcesses = subProcesses;
	}

	private static void checkRegex(final String s, final Pattern pattern, final String info) {
		if (!pattern.matcher(s).matches()) {
			throw new IllegalArgumentException(info + " " + s + " must match regex :" + pattern.pattern());
		}
	}

	/**
	 * [what]
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the duration of the process (in milliseconds)
	 */
	public long getDurationMillis() {
		return end - start;
	}

	/**
	 * [when]
	 * @return the start timestamp in Millis
	 */
	public long getStart() {
		return start;
	}

	/**
	 * [when]
	 * @return the end timestamp in Millis
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * @return the measures of the process
	 */
	public Map<String, Double> getMeasures() {
		return measures;
	}

	/**
	 * @return the tags of the process
	 */
	public Map<String, String> getTags() {
		return tags;
	}

	/**
	 * @return the list of sub processes
	 */
	public List<AProcess> getSubProcesses() {
		return subProcesses;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "{category :" + category + ", name :" + name + "}";
	}
}
