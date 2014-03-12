package io.analytica.uiswing;

import io.analytica.api.KProcess;
import io.analytica.hcube.plugins.memorystack.LastProcessMXBean;
import io.analytica.uiswing.collector.PerfCallStackCollector;
import io.analytica.uiswing.collector.PerfCollector;
import io.analytica.uiswing.collector.ProcessStats;
import io.analytica.uiswing.collector.ProcessStatsCollection;
import io.analytica.uiswing.collector.ProcessStatsMap;
import io.analytica.uiswing.collector.ProcessStatsTree;
import io.analytica.uiswing.patterns.SFrame;
import io.analytica.uiswing.patterns.SLabel;
import io.analytica.uiswing.patterns.SMasterNoDetailPanel;
import io.analytica.uiswing.patterns.SPanel;
import io.analytica.uiswing.patterns.SScrollPane;
import io.analytica.uiswing.patterns.STabbedPane;
import io.analytica.uiswing.patterns.STree;
import io.analytica.uiswing.patterns.SUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.BorderFactory;

import mswing.patterns.MMasterPanel;
import mswing.table.MTable;
import mswing.table.MUtilitiesTable;

import com.google.gson.Gson;

/**
 * @version $Id: ParametrageMenuController.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author gteissier
 */
public class ParametrageMenuController {

	public static void main(final String[] args) {
		try {
			//ApplicationState.getSingleton().getFacadeFactory().getFacadeReferentielEquipement().getListStationTrafic();
			final ParametrageMenuController controller = new ParametrageMenuController();
			final STabbedPane statsPanel = controller.createStatisticsPanel();

			/* On doit definir si il s'agit d'une fmc previsionnelle ou aleatoire */

			/* On fait les set/create necessaires */

			/* On est obligé d'appeler la méthode init APRES avoir définit le controlleur
			         dans les vues car celles-ci font appel au controlleur pour s'afficher */
			//courbeRdtMainPanel.init();

			final SFrame frame = new SFrame("Statistiques");
			frame.getContentPane().add(statsPanel);
			frame.pack();
			frame.setVisible(true);
		} catch (final Exception e) {
			SUtilities.handleException(e);
		}
	}

	public STabbedPane createStatisticsPanel() throws MalformedObjectNameException, NullPointerException, IOException, NotBoundException {
		final STabbedPane tabbedPane = new STabbedPane();
		Map.Entry entry;
		Object key;
		ProcessStats serverValue;
		final ProcessStats clientValue;

		final Map perModuleStats = getServerResults();
		final Set moduleNameSet = new HashSet();
		moduleNameSet.addAll(perModuleStats.keySet());
		for (final Iterator itStats = moduleNameSet.iterator(); itStats.hasNext();) {
			final String moduleName = (String) itStats.next();
			final ProcessStatsCollection moduleStatsCollection = (ProcessStatsCollection) perModuleStats.get(moduleName);
			if (moduleStatsCollection == null || moduleStatsCollection instanceof ProcessStatsMap) {
				final Map serverStats = moduleStatsCollection != null ? moduleStatsCollection.getResults() : new HashMap();
				final List allStats = new ArrayList();
				long serverDurationsSumForAllMethods = 0;
				for (final Iterator it = serverStats.values().iterator(); it.hasNext();) {
					serverValue = (ProcessStats) it.next();
					serverDurationsSumForAllMethods += serverValue.getDurationsSum();
				}
				for (final Iterator it = serverStats.entrySet().iterator(); it.hasNext();) {
					entry = (Map.Entry) it.next();
					key = entry.getKey();
					serverValue = (ProcessStats) entry.getValue();
					serverValue.setDurationsSumForAllMethods(serverDurationsSumForAllMethods);
					allStats.add(serverValue);
				}
				final SMasterNoDetailPanel statsListMasterPanel = createStatMasterPanel(getModuleDecriptionByModuleName(moduleName), allStats, false, hasPercentageByModuleName(moduleName));
				tabbedPane.add(getModuleLibelleByModuleName(moduleName), statsListMasterPanel);

			} else if (moduleStatsCollection instanceof ProcessStatsTree) {
				final Map serverStats = moduleStatsCollection.getResults();
				final STree statsTree = new NodeStatsTree(serverStats);
				final SScrollPane stastMasterPanel = new SScrollPane(statsTree);
				final SPanel sPanel = new SPanel();
				sPanel.setLayout(new BorderLayout());
				final SLabel sLabel = new SLabel(getModuleDecriptionByModuleName(moduleName));
				sLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));
				sPanel.add(sLabel, BorderLayout.NORTH);
				sPanel.add(stastMasterPanel, BorderLayout.CENTER);
				sPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
				tabbedPane.add(getModuleLibelleByModuleName(moduleName), sPanel);
			}
		}

		return tabbedPane;
	}

	private Map getServerResults() throws IOException, MalformedObjectNameException, NullPointerException, NotBoundException {
		final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:3334" + "/jmxrmi");
		final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		try {
			final MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			final ObjectName mbeanName = new ObjectName("analytica:type=LastProcessMXBean");
			final LastProcessMXBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, LastProcessMXBean.class);
			final PerfCollector perfCollector = convertAsPerfCollector(mbeanProxy);
			return perfCollector.getResults();
		} finally {
			jmxc.close();
		}
		//		final ProcessStoreMXBean mbeanProxy = (ProcessStoreMXBean) Naming.lookup("//localhost/ProcessStorePlugin");
		//		final PerfCollector perfCollector = convertAsPerfCollector(mbeanProxy);
		//		return perfCollector.getResults();

	}

	private PerfCollector convertAsPerfCollector(final LastProcessMXBean mbeanProxy) {
		final PerfCollector perfCollector = new PerfCallStackCollector();
		final KProcess[] nextProcesses = new Gson().fromJson(mbeanProxy.getLastProcessesJson(), KProcess[].class);
		for (final KProcess process : nextProcesses) {
			storeToPerfCollector(process, perfCollector);
		}
		return perfCollector;
	}

	private void storeToPerfCollector(final KProcess process, final PerfCollector perfCollector) {
		perfCollector.onProcessStart(process.getType(), Arrays.asList(process.getSubTypes()).toString(), null, null);
		for (final KProcess subprocess : process.getSubProcesses()) {
			storeToPerfCollector(subprocess, perfCollector);
		}
		perfCollector.onProcessFinish(process.getType(), Arrays.asList(process.getSubTypes()).toString(), null, null, null, Math.round(process.getMeasures().get(KProcess.DURATION)), true);
	}

	private String getModuleLibelleByModuleName(final String moduleName) {
		return moduleName;
	}

	private String getModuleDecriptionByModuleName(final String moduleName) {
		return moduleName;
	}

	private boolean hasPercentageByModuleName(final String moduleName) {
		return false;
	}

	private SMasterNoDetailPanel createStatMasterPanel(final String masterTitle, final List list, final boolean hasClient, final boolean hasPercentage) {
		String[][] headers;
		if (hasClient) {
			headers = new String[][] { { "serverOrClient.processId", "Fonction" }, { "server.durationsSum", "Tps total srv" }, { "server.hits", "Exéc srv" }, { "server.durationsMin", "Tps min srv" }, { "server.durationsMean", "Tps moy srv" }, { "server.durationsMax", "Tps max srv" }, { "server.durationsLast", "Dernier tps srv" }, { "server.durationsEcartType", "Ecart type srv" }, { "server.durationsPercentageComparedToAllMethods", "Pct tps srv" }, { "client.durationsSum", "Tps total clt" }, { "client.hits", "Exéc clt" }, { "client.durationsMin", "Tps min clt" }, { "client.durationsMean", "Tps moy clt" }, { "client.durationsMax", "Tps max clt" }, { "client.durationsLast", "Dernier tps clt" }, { "client.durationsEcartType", "Ecart type clt" }, { "client.durationsPercentageComparedToAllMethods", "Pct tps clt" } };
		} else {
			headers = new String[][] { { "server.processId", "Fonction" }, { "server.durationsSum", "Tps total srv" }, { "server.hits", "Exéc srv" }, { "server.durationsMin", "Tps min srv" }, { "server.durationsMean", "Tps moy srv" }, { "server.durationsMax", "Tps max srv" }, { "server.durationsLast", "Dernier tps srv" }, { "server.durationsEcartType", "Ecart type srv" }, { "server.durationsPercentageComparedToAllMethods", "Pct tps srv" } };
		}
		final SMasterNoDetailPanel masterPanel = new SMasterNoDetailPanel();
		masterPanel.setMasterTitle(masterTitle);
		final MTable table = masterPanel.getTable();
		table.setHeaders(headers);
		if (!hasPercentage) {
			MUtilitiesTable.hideColumn(table.getColumn("server.durationsPercentageComparedToAllMethods"));
			if (hasClient) {
				MUtilitiesTable.hideColumn(table.getColumn("client.durationsPercentageComparedToAllMethods"));
			}
		}
		table.sort("server.durationsSum", false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setFont(table.getFont().deriveFont(9f));
		table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(9f));
		masterPanel.setActions(MMasterPanel.ACTION_PRINT ^ MMasterPanel.ACTION_CLOSE);
		masterPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		masterPanel.setList(list);

		return masterPanel;
	}
}
