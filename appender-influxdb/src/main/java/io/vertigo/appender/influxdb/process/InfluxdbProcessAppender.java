package io.vertigo.appender.influxdb.process;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.influxdb.dto.Point;

import io.vertigo.appender.influxdb.AbstractInfluxdbAppender;

public class InfluxdbProcessAppender extends AbstractInfluxdbAppender<AProcess> {

	@Override
	protected Point eventToPoint(final AProcess process, final String host) {
		final Map measures = process.getMeasures();
		final VisitState state = new VisitState();
		flatProcess(process, state);

		final Map<String, Object> countFields = state.getCountsByCategory().entrySet().stream()
				.collect(Collectors.toMap((entry) -> entry.getKey() + "_count", (entry) -> entry.getValue()));
		final Map<String, Object> durationFields = state.getDurationsByCategory().entrySet().stream()
				.collect(Collectors.toMap((entry) -> entry.getKey() + "_duration", (entry) -> entry.getValue()));

		return Point.measurement(process.getCategory())
				.time(process.getStart(), TimeUnit.MILLISECONDS)
				.tag(TAG_NAME, process.getName())
				.tag(TAG_LOCATION, host)
				.tag(process.getTags())
				.addField("duration", process.getDurationMillis())
				.addField("subprocesses", process.getSubProcesses().size())
				.addField("name", process.getName())
				.fields(countFields)
				.fields(durationFields)
				.build();
	}

	@Override
	protected Type getEventType() {
		return AProcess.class;
	}

	private void flatProcess(final AProcess process, final VisitState visitState) {
		process.getSubProcesses().stream()
				.forEach(subProcess -> {
					visitState.push(subProcess);
					//on descend => stack.push
					flatProcess(subProcess, visitState);
					//on remonte => stack.poll
					visitState.pop();
				});

	}

	class VisitState {
		private final Map<String, Integer> countsByCategory = new HashMap<>();
		private final Map<String, Long> durationsByCategory = new HashMap<>();
		private final Stack<String> stack = new Stack<>();

		void push(final AProcess process) {
			if (!stack.contains(process.getCategory())) {
				incDurations(process.getCategory(), process.getDurationMillis());
			}
			incCounts(process.getCategory());
			stack.push(process.getCategory());

		}

		void pop() {
			stack.pop();
		}

		private void incDurations(final String category, final Long duration) {
			final Long existing = durationsByCategory.get(category);
			if (existing == null) {
				durationsByCategory.put(category, duration);
			} else {
				durationsByCategory.put(category, existing + duration);
			}
		}

		private void incCounts(final String category) {
			final Integer existing = countsByCategory.get(category);
			if (existing == null) {
				countsByCategory.put(category, 1);
			} else {
				countsByCategory.put(category, existing + 1);
			}
		}

		Map<String, Integer> getCountsByCategory() {
			return countsByCategory;
		}

		Map<String, Long> getDurationsByCategory() {
			return durationsByCategory;
		}

	}

}
