package com.kleegroup.analyticaimpl.uiswing.patterns;

import java.awt.Dialog;
import java.awt.Frame;

import mswing.MDialog;

/**
 * Dialog du SAE.
 * @version $Id: SDialog.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author Antoine Gérard
 */
public class SDialog extends MDialog {

	private static final long serialVersionUID = 1256778083026378095L;

	public SDialog() {
		super();
	}

	public SDialog(final Dialog owner, final String title, final boolean modal) {
		super(owner, title, modal);
	}

	public SDialog(final Frame owner, final String title, final boolean modal) {
		super(owner, title, modal);
	}

	public SDialog(final String title) {
		super(title);
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
		System.out.println("Dialog finalized: " + getTitle());
		super.finalize();
	}
}
