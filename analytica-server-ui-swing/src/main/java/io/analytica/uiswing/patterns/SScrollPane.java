/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
