package io.analytica.uiswing.collector;

import java.util.Collections;
import java.util.Map;

public class PerfSilenceCollector implements PerfCollector {

	public final void clearResults(final String moduleName) {
		//rien
	}

	public final void clearResults() {
		//rien
	}

	public final ProcessStatsCollection getResults(final String moduleName) {
		return null;
	}

	public final Map getResults() {
		return Collections.emptyMap();
	}

	public final void onProcessError(final String moduleName, final String processId, final Object obj, final Object[] params, final Throwable throwable) {
		//rien
	}

	public final void onProcessFinish(final String moduleName, final String processId, final Object obj, final Object[] params, final Object ret, final long duration, final boolean success) {
		//rien
	}

	public final void onProcessStart(final String moduleName, final String processId, final Object obj, final Object[] params) {
		//rien
	}

	public StringBuffer print(final StringBuffer out) {
		return out;
	}
}
