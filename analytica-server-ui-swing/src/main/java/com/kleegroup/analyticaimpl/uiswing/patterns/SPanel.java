package com.kleegroup.analyticaimpl.uiswing.patterns;

import java.awt.LayoutManager;

import mswing.MPanel;

/**
 * Panel simple SAE.
 * @author Antoine GERARD
 * @version $Id: SPanel.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 */
public class SPanel extends MPanel {
	private static final long serialVersionUID = -3588078003340353544L;

	/**
	 * Constructeur.
	 */
	public SPanel() {
		super();
	}

	/**
	 * Constructeur.
	 * @param layout java.awt.LayoutManager
	 */
	public SPanel(final LayoutManager layout) {
		super(layout);
	}

}
