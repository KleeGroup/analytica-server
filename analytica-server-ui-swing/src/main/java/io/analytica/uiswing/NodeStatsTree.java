package io.analytica.uiswing;

import io.analytica.uiswing.collector.PerfCallStackCollector;
import io.analytica.uiswing.collector.ProcessStatsNode;
import io.analytica.uiswing.patterns.STree;
import io.analytica.uiswing.patterns.SUtilities;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

public class NodeStatsTree extends STree {

	private static final long serialVersionUID = 7633109217821538213L;

	public NodeStatsTree(final Map nodeStatsRoot) {
		super();
		try {
			setModel(makeTreeModel(nodeStatsRoot));
			setShowsRootHandles(true);
			setCellRenderer(new NodeStatsTreeCellRenderer());
			getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		} catch (final Exception ex) {
			SUtilities.handleException(ex);
		}

	}

	/**
	 * Construit le modèle du <code>STree</code> à partir d'un arbre de règles.
	 *
	 * @param arbre PmvArbre
	 * @return TreeModel
	 */
	private TreeModel makeTreeModel(final Map arbre) {
		final DefaultMutableTreeNode topNode = new DefaultMutableTreeNode("Ratio Appel Methodes");

		addNodeStatsToTreeModel(topNode, arbre);

		return new DefaultTreeModel(topNode);
	}

	private void addNodeStatsToTreeModel(final DefaultMutableTreeNode upperNode, final Map mapNodeStats) {
		ProcessStatsNode methodStatsNode;
		final List sortedList = CollUtils.sortList(mapNodeStats.values(), new String[] { "processStats.durationsSum" });
		Collections.reverse(sortedList);
		Map sousMethodStatsNode;
		for (final Iterator it = sortedList.iterator(); it.hasNext();) {
			methodStatsNode = (ProcessStatsNode) it.next();
			final DefaultMutableTreeNode category = new DefaultMutableTreeNode(methodStatsNode);
			upperNode.add(category);
			sousMethodStatsNode = methodStatsNode.getSousProcessStatsNode();
			if (sousMethodStatsNode != null && !sousMethodStatsNode.isEmpty()) {
				if (sousMethodStatsNode.size() == 1) {
					if (PerfCallStackCollector.AUTRE_LIBELLE.equals(sousMethodStatsNode.keySet().iterator().next())) {
						continue;
					}
				}
				addNodeStatsToTreeModel(category, sousMethodStatsNode);
			}
		}
	}
}
