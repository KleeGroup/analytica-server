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
package io.analytica.uiswing.patterns;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * Composant graphique de type "arbre" pour le SAE
 *
 * @version $Id: STree.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author agerard
 */
public class STree extends JTree {

	private static final long serialVersionUID = -4684768283301197558L;

	public STree() {
		super();
	}

	public STree(final Object[] value) {
		super(value);
	}

	public STree(final Hashtable value) {
		super(value);
	}

	public STree(final Vector value) {
		super(value);
	}

	public STree(final TreeModel treeModel) {
		super(treeModel);
	}

	public STree(final TreeNode rootNode) {
		super(rootNode);
	}

	public STree(final TreeNode rootNode, final boolean asksAllowsChildren) {
		super(rootNode, asksAllowsChildren);
	}
}
