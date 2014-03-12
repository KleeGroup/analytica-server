package io.analytica.uiswing.collector;

public class PerfCollectorFactory {
	private static final PerfCollectorFactory INSTANCE = new PerfCollectorFactory();

	private static PerfCollector PERF_COLLECTOR = new PerfSilenceCollector();

	/*static {
	    String defaultPerfCollector = PerfProcessCollector.class.getName();
	    String perfCollectorName = null;
	    try {
	        //perfCollectorName = getString("KANAP.GRABBER.PERF_COLLECTOR", defaultPerfCollector);
	        PERF_COLLECTOR = (PerfCollector) Class.forName(perfCollectorName).newInstance();
	    } catch (Throwable t) {
	        throw new RuntimeException("PerfCollector non trouvé (" + perfCollectorName + ")", t); //Cette exception arretera le chargement de la class et donc l'appli.
	    }
	}*/

	public static PerfCollectorFactory getSingleton() {
		return INSTANCE;
	}

	private PerfCollectorFactory() {
		//constructeur privé
	}

	public void setPerfCollector(final PerfCollector perfCollector) {
		PERF_COLLECTOR = perfCollector;
	}

	public PerfCollector getPerfCollector() {
		return PERF_COLLECTOR;
	}
}
