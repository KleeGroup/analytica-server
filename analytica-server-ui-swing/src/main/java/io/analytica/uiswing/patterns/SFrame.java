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

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import mswing.MDialog;

/**
 * Frame SAE.
 * @author evernat
 * @version $Id: SFrame.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 */
public class SFrame extends JFrame {
	private static final long serialVersionUID = -5073192139367063968L;

	/**
	 * Constructeur.
	 */
	public SFrame() {
		super();
		init();
	}

	/**
	 * Constructeur.
	 * @param title Titre de la frame
	 */
	public SFrame(final String title) {
		super(title);
		init();
	}

	/**
	 * Constructeur.
	 * @param gc java.awt.GraphicsConfiguration
	 */
	public SFrame(final java.awt.GraphicsConfiguration gc) {
		super(gc);
		init();
	}

	private void init() {
		//Toutes ces frames ont en général la même logique lors d'un close
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		final ImageIcon imageIcon = SUtilities.getImageIconFromCache("cofiroute.png");
		if (imageIcon != null) {
			setIconImage(imageIcon.getImage());
		}
	}

	@Override
	public void pack() {
		super.pack();
		setLocationRelativeTo(getOwner() != null ? getOwner() : MDialog.getDefaultOwnerFrame());
	}

	@Override
	public void setVisible(final boolean visible) {
		final boolean wasVisible = isVisible();
		super.setVisible(visible);
		if (!visible && wasVisible) {
			dispose();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("Frame finalized: " + getTitle());
		super.finalize();
	}

	/**
	 * Sets the location of the window relative to the specified component. If the component is not currently showing,
	 * or <code>c</code> is <code>null</code>, the window is centered on the screen. If the bottom of the component is
	 * offscreen, the window is displayed to the right of the component.
	 *
	 * @param component the component in relation to which the window's location is determined
	 * @since 1.4
	 */
	@Override
	public void setLocationRelativeTo(final Component component) {
		if (component == null) {
			return;
		}
		//---------------------------------------------------------------------
		Container root = null;
		if (component instanceof Window || component instanceof Applet) {
			root = (Container) component;
		} else {
			Container parent;
			for (parent = component.getParent(); parent != null; parent = parent.getParent()) {
				if (parent instanceof Window || parent instanceof Applet) {
					root = parent;
					break;
				}
			}
		}

		if (!component.isShowing() || root == null || !root.isShowing()) {
			final Dimension paneSize = getSize();
			final Dimension screenSize = getToolkit().getScreenSize();

			setLocation((screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);
		} else {
			final Dimension invokerSize = component.getSize();
			Point invokerScreenLocation;

			// If this method is called directly after a call to
			// setLocation() on the "root", getLocationOnScreen()
			// may return stale results (Bug#4181562), so we walk
			// up the tree to calculate the position instead
			// (unless "root" is an applet, where we cannot walk
			// all the way up to a toplevel window)
			//
			if (root instanceof Applet) {
				invokerScreenLocation = component.getLocationOnScreen();
			} else {
				invokerScreenLocation = new Point(0, 0);
				Component tc = component;
				Point tcl;
				while (tc != null) {
					tcl = tc.getLocation();
					invokerScreenLocation.x += tcl.x;
					invokerScreenLocation.y += tcl.y;
					if (tc == root) {
						break;
					}
					tc = tc.getParent();
				}
			}
			final Rectangle windowBounds = getBounds();
			final Rectangle configBounds = component.getGraphicsConfiguration().getBounds();
			int dx = invokerScreenLocation.x + (invokerSize.width - windowBounds.width >> 1);
			int dy = invokerScreenLocation.y + (invokerSize.height - windowBounds.height >> 1);

			if (dx + windowBounds.width > configBounds.x + configBounds.width) {
				dx = configBounds.x + configBounds.width - windowBounds.width;
			}
			if (dy + windowBounds.height > configBounds.y + configBounds.height) {
				dy = configBounds.y + configBounds.height - windowBounds.height;

			}
			if (dx < configBounds.x) {
				dx = configBounds.x;
			}
			if (dy < configBounds.y) {
				dy = configBounds.y;

			}
			setLocation(dx, dy);
		}
	}

}
