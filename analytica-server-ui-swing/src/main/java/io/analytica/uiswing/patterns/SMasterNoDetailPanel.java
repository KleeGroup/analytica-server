package io.analytica.uiswing.patterns;

import mswing.patterns.MMasterPanel;

/**
 * Maître-détail sans détail mais avec l'action 'imprimer'.
 * @version $Id: SMasterNoDetailPanel.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author emangin
 */
public class SMasterNoDetailPanel extends MMasterPanel {
	private static final long serialVersionUID = -7600423868990730183L;

	/**
	 * Constructeur.
	 */
	public SMasterNoDetailPanel() {
		super();
	}

	/**
	 * Retourne les actions par défaut pour ce masterPanel.
	 * @return int
	 */
	@Override
	protected int getDefaultActions() {
		return ACTION_PRINT;
		// ACTION_ADD | ACTION_DELETE | ACTION_SAVE | ACTION_CANCEL | ACTION_FILTER | ACTION_MODIFY
	}

	/**
	 * actionDelete.
	 */
	@Override
	protected void actionDelete() {
		// on supprime (cela supprime un objet de la liste de manière synchronized)
		synchronized (getList()) {
			super.actionDelete();
		}
		// on ajuste les tailles des colonnes en fonction des données
		getTable().adjustColumnWidths();
	}
}
