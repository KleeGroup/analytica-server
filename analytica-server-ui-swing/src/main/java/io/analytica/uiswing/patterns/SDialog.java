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
