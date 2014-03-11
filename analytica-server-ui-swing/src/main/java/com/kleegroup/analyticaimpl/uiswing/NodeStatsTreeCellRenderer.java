package com.kleegroup.analyticaimpl.uiswing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.kleegroup.analyticaimpl.uiswing.collector.ProcessStats;
import com.kleegroup.analyticaimpl.uiswing.collector.ProcessStatsNode;

public class NodeStatsTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -7656915802248518075L;
	private static final boolean COMPARE_TO_UPPER = false; //True pour comparer à l'élément supérieur et false pour la racine
	private static final boolean COMPARE_TO_ROOT = !COMPARE_TO_UPPER;
	private static final String TAB_SPACE = "     ";
	private static final String MAX_TAB_SPACE = "                                                                                                    ";
	private static final int FIRST_TAB_SIZE = 90;
	private static final int OTHER_TAB_SIZE = 6;

	public NodeStatsTreeCellRenderer() {
		setFont(new Font("Monospaced", Font.PLAIN, 12));
	}

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, convertValueToText(value), sel, expanded, leaf, row, hasFocus);
		setIcon(null);
		return this;
	}

	/**
	 * Called by the renderers to convert the specified value to
	 * text. This implementation returns <code>value.toString</code>, ignoring
	 * all other arguments. To control the conversion, subclass this
	 * method and use any of the arguments you need.
	 *
	 * @param value the <code>Object</code> to convert to text
	 * @return the <code>String</code> representation of the node's value
	 */
	private String convertValueToText(final Object value) {
		setBackground(Color.WHITE);
		setOpaque(true);
		if (value != null && value instanceof DefaultMutableTreeNode) {
			final Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
			if (userObject != null && userObject instanceof ProcessStatsNode) {
				final ProcessStatsNode methodStatsNode = (ProcessStatsNode) userObject;
				final ProcessStatsNode upperProcessStatsNode = methodStatsNode.getUpperProcessStatsNode();
				ProcessStatsNode rootProcessStatsNode = null;
				if (COMPARE_TO_ROOT) {
					rootProcessStatsNode = methodStatsNode.getProcessStatsNodeRoot();
				}

				final ProcessStats stats = methodStatsNode.getProcessStats();
				final ProcessStats upperStats = upperProcessStatsNode != null ? upperProcessStatsNode.getProcessStats() : null;
				final ProcessStats rootStats = rootProcessStatsNode != null ? rootProcessStatsNode.getProcessStats() : null;
				final ProcessStats comparedStats = COMPARE_TO_ROOT ? rootStats : upperStats;
				final StringBuffer sb = new StringBuffer(methodStatsNode.getProcessId());
				sb.append(MAX_TAB_SPACE.substring(0, Math.max(FIRST_TAB_SIZE - methodStatsNode.getProcessId().length(), 1)));
				sb.append("Total:");
				sb.append(MAX_TAB_SPACE.substring(0, Math.max(OTHER_TAB_SIZE - String.valueOf(stats.getDurationsSum()).length(), 1)));
				sb.append(stats.getDurationsSum()).append("ms");
				if (comparedStats != null) {
					long ratio = 0;
					if (comparedStats.getDurationsSum() > 0) {
						ratio = Math.round(stats.getDurationsSum() * 100d / comparedStats.getDurationsSum());
					}
					if (ratio < 10) {
						sb.append(' ');
					}
					sb.append('(').append(ratio).append("%)");
					Color bgColor = null;
					if (COMPARE_TO_ROOT) {
						ratio = 0;
						if (upperStats.getDurationsSum() > 0) {
							ratio = Math.round(stats.getDurationsSum() * 100d / upperStats.getDurationsSum());
						}
					}

					if (ratio >= 80) {
						bgColor = new Color(255, 25, 25);
					} else if (ratio >= 60) {
						bgColor = new Color(255, 125, 75);
					} else if (ratio >= 40) {
						bgColor = Color.ORANGE;
					} else if (ratio >= 20) {
						bgColor = Color.YELLOW;
					}
					if (bgColor != null) {
						setBackground(bgColor);
					}
				}
				sb.append(TAB_SPACE).append("Moyenne:");
				sb.append(MAX_TAB_SPACE.substring(0, Math.max(OTHER_TAB_SIZE - String.valueOf(stats.getDurationsMean()).length(), 1)));

				sb.append(stats.getDurationsMean()).append("ms");
				if (comparedStats != null) {
					sb.append(" (").append(stats.getHits() * stats.getDurationsMean() / comparedStats.getHits()).append("ms)");
				}
				sb.append(TAB_SPACE).append("Exec:");
				sb.append(MAX_TAB_SPACE.substring(0, Math.max(OTHER_TAB_SIZE - String.valueOf(stats.getHits()).length(), 1)));
				sb.append(stats.getHits()).append("");
				if (comparedStats != null) {
					sb.append("(x").append(Math.round(stats.getHits() * 10d / comparedStats.getHits()) / 10d).append(')');
				}
				sb.append(TAB_SPACE).append("Ecart type:");
				sb.append(MAX_TAB_SPACE.substring(0, Math.max(OTHER_TAB_SIZE - String.valueOf(stats.getDurationsEcartType()).length(), 1)));
				sb.append(stats.getDurationsEcartType()).append("ms");

				return sb.toString();
			}
		}
		return "";
	}

}
