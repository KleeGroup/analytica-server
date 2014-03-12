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
