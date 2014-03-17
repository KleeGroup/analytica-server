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

import javax.swing.Icon;
import javax.swing.JLabel;

import mswing.MAssertion;
import mswing.MView;
import mswing.MViewAdaptee;

/**
 * Label SAE.
 * @author Antoine GERARD
 * @version $Id: SLabel.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 */
public class SLabel extends JLabel implements MViewAdaptee {
	private static final long serialVersionUID = -3406323890345345345L;

	private class LabelAdapter implements MView {
		public String getModelName() {
			return "text";
		}

		public MViewAdaptee getAdaptee() {
			return SLabel.this;
		}

		public Object getModel() {
			return getText();
		}

		public void setModel(final Object model) {
			MAssertion.preCondition(model == null || model instanceof String, "Dans SLabel.LabelAdapter.setModel, le model n'est pas null ou une instance de String");
			setText((String) model);
		}
	}

	private MView view;

	/**
	 * Constructeur.
	 */
	public SLabel() {
		super();
	}

	/**
	 * Constructeur.
	 * @param text Texte du label
	 */
	public SLabel(final String text) {
		super(text);
	}

	/**
	 * Constructeur.
	 * @param icon Icône du label
	 */
	public SLabel(final Icon icon) {
		super(icon);
	}

	public MView getView() {
		if (view == null) {
			view = new LabelAdapter();
		}
		return view;
	}
}
