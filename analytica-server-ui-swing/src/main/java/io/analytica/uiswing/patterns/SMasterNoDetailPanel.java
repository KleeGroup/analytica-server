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

import mswing.patterns.MMasterPanel;

/**
 * Maétre-détail sans détail mais avec l'action 'imprimer'.
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
		// on supprime (cela supprime un objet de la liste de maniére synchronized)
		synchronized (getList()) {
			super.actionDelete();
		}
		// on ajuste les tailles des colonnes en fonction des données
		getTable().adjustColumnWidths();
	}
}
