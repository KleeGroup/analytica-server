/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
	 * Construit le modéle du <code>STree</code> é partir d'un arbre de régles.
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
