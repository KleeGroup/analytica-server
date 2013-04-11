package com.kleegroup.analyticaimpl.uiswing.patterns;

import java.awt.Component;

import javax.swing.JScrollPane;

/**
 * ScrollPane SAE.
 * @author Antoine GERARD
 * @version $Id: SScrollPane.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 */
public class SScrollPane extends JScrollPane {
	private static final long serialVersionUID = 478440008116876279L;

	/**
	 * Constructeur.
	 */
	public SScrollPane() {
		this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	/**
	 * Constructeur.
	 * @param view java.awt.Component
	 */
	public SScrollPane(final Component view) {
		this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	/**
	 * Constructeur.
	 * @param vsbPolicy int
	 * @param hsbPolicy int
	 */
	public SScrollPane(final int vsbPolicy, final int hsbPolicy) {
		this(null, vsbPolicy, hsbPolicy);
	}

	/**
	 * Constructeur.
	 * @param view java.awt.Component
	 * @param vsbPolicy int
	 * @param hsbPolicy int
	 */
	public SScrollPane(final Component view, final int vsbPolicy, final int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);

		// voir JTable.getScrollableUnitIncrement
		getVerticalScrollBar().setUnitIncrement(16); // correspond environ au rowHeight par défaut d'une jtable
		getHorizontalScrollBar().setUnitIncrement(100);
	}
}
