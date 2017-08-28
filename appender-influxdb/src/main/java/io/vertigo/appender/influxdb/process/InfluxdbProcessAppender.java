package io.vertigo.appender.influxdb.process;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.influxdb.dto.Point;

import io.vertigo.appender.influxdb.AbstractInfluxdbAppender;

public class InfluxdbProcessAppender extends AbstractInfluxdbAppender<AProcess> {

	@Override
	protected List<Point> eventToPoints(final AProcess aProcess, final String host) {
		return flatProcess(aProcess, host);
	}

	private static Point processToPoint(final AProcess process, final VisitState visitState, final String host) {
		final Map<String, Object> countFields = visitState.getCountsByCategory().entrySet().stream()
				.collect(Collectors.toMap((entry) -> entry.getKey() + "_count", (entry) -> entry.getValue()));
		final Map<String, Object> durationFields = visitState.getDurationsByCategory().entrySet().stream()
				.collect(Collectors.toMap((entry) -> entry.getKey() + "_duration", (entry) -> entry.getValue()));

		final Map<String, String> properedTags = process.getTags().entrySet()
				.stream()
				.collect(Collectors.toMap(
						entry -> properString(entry.getKey()),
						entry -> properString(entry.getValue())));

		return Point.measurement(process.getCategory())
				.time(process.getStart(), TimeUnit.MILLISECONDS)
				.tag(TAG_NAME, properString(process.getName()))
				.tag(TAG_LOCATION, host)
				.tag(properedTags)
				.addField("duration", process.getDurationMillis())
				.addField("subprocesses", process.getSubProcesses().size())
				.addField("name", properString(process.getName()))
				.fields(countFields)
				.fields(durationFields)
				.build();
	}

	@Override
	protected Type getEventType() {
		return AProcess.class;
	}

	private List<Point> flatProcess(final AProcess process, final String host) {
		final List<Point> points = new ArrayList<>();
		flatProcess(process, new Stack<>(), points, host);
		return points;
	}

	private VisitState flatProcess(final AProcess process, final Stack<String> upperCategory, final List<Point> points, final String host) {
		final VisitState visitState = new VisitState(upperCategory);
		process.getSubProcesses().stream()
				.forEach(subProcess -> {
					visitState.push(subProcess);
					//on descend => stack.push
					final VisitState childVisiteState = flatProcess(subProcess, upperCategory, points, host);
					visitState.merge(childVisiteState);
					//on remonte => stack.poll
					visitState.pop();
				});
		points.add(processToPoint(process, visitState, host));
		return visitState;

	}

	class VisitState {
		private final Map<String, Integer> countsByCategory = new HashMap<>();
		private final Map<String, Long> durationsByCategory = new HashMap<>();
		private final Stack<String> stack;

		public VisitState(final Stack<String> upperCategory) {
			stack = upperCategory;
		}

		void push(final AProcess process) {
			incDurations(process.getCategory(), process.getDurationMillis());
			incCounts(process.getCategory(), 1);
			stack.push(process.getCategory());
		}

		void merge(final VisitState visitState) {
			visitState.durationsByCategory.entrySet()
					.forEach((entry) -> incDurations(entry.getKey(), entry.getValue()));
			visitState.countsByCategory.entrySet()
					.forEach((entry) -> incCounts(entry.getKey(), entry.getValue()));
		}

		void pop() {
			stack.pop();
		}

		private void incDurations(final String category, final Long duration) {
			if (!stack.contains(category)) {
				final Long existing = durationsByCategory.get(category);
				if (existing == null) {
					durationsByCategory.put(category, duration);
				} else {
					durationsByCategory.put(category, existing + duration);
				}
			}
		}

		private void incCounts(final String category, final Integer count) {
			final Integer existing = countsByCategory.get(category);
			if (existing == null) {
				countsByCategory.put(category, count);
			} else {
				countsByCategory.put(category, existing + count);
			}
		}

		Map<String, Integer> getCountsByCategory() {
			return countsByCategory;
		}

		Map<String, Long> getDurationsByCategory() {
			return durationsByCategory;
		}

	}

	private static String properString(final String string) {
		if (string == null) {
			return string;
		}
		return string.replaceAll("\n", " ");
	}

}
