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
