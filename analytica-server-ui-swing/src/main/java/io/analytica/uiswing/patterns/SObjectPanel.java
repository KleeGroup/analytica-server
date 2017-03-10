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

import java.awt.LayoutManager;

import mswing.MChangedPanel;
import mswing.MLabelledField;
import mswing.MMapping;
import mswing.MStringArea;
import mswing.MTextDocument;
import mswing.MTextField;
import mswing.MUtilities;
import mswing.MViewAdaptee;

/**
 * Panel utilisé pour représenter les champs correspondant é un "objet métier de données".
 * <br>C'est un composant "container" représentant la vue graphique d'un objet
 * et contenant en général des sous composants.
 * <br>Sur ce panel toutes les modifications effectuées par l'utilisateur sont détectées.
 * Vous pouvez contréler le statut du panel par les méthodes hasChanged() et setChanged(boolean).
 * @version $Id: SObjectPanel.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author evernat
 */
public class SObjectPanel extends MChangedPanel {
	private static final long serialVersionUID = -1596093460217852454L;
	private boolean constraintsInitialized; // = false;

	/**
	 * Constructeur.
	 */
	public SObjectPanel() {
		super();
	}

	/**
	 * Constructeur.
	 * @param layout java.awt.LayoutManager
	 */
	public SObjectPanel(final LayoutManager layout) {
		super(layout);
	}

	/**
	 * Définit la valeur de la propriété object.
	 * <br>Cette méthode positionne également l'attibut "changed" é false.
	 * <br>Elle initialise également le panel é partir du DT (cela ne peut étre fait qu'avec une instance de ce DT).
	 * @param object java.lang.Object
	 */
	@Override
	public void setObject(final Object object) {
		super.setObject(object);
		if (!constraintsInitialized) {
			initFromDtObject();
			constraintsInitialized = true;
		}
	}

	/**
	 * Initialisation des champs é partir des méta-données du DT :
	 * - champs not null,
	 * - libellés
	 * - contraintes de longueurs
	 */
	private void initFromDtObject() {
		mswing.MMapping mapping;
		String identifier;
		MViewAdaptee adaptee;
		MLabelledField labelledField;
		MTextDocument textDocument;
		String labelText;
		try {
			for (final java.util.Iterator it = getMappings().iterator(); it.hasNext();) {
				mapping = (MMapping) it.next();
				identifier = mapping.getIdentifier();
				adaptee = mapping.getAdaptee();
				if (!mapping.isNotNull() && isNotNull(identifier)) {
					// si le mapping n'est pas déjé not null et que le dt indique not null,
					// on met le mapping é not null
					mapping.setNotNull(true);
				}

				/** les différentes ihm de l'objet étant assez différentes et pour différents personnes
				 * (liste et détail(s) client lourd, liste et détail(s) client léger),
				 * est-ce profitable de centraliser et unifier les libellés ?
				 */
				if (adaptee instanceof MLabelledField) {
					// si l'adaptee est un labelledField (champ accompagné d'un label)
					// et que son labelText est null ou vide,
					// on met le label indiqué dans le DT
					labelledField = (MLabelledField) adaptee;
					labelText = getLabelText(identifier);
					if (labelText != null && labelText.length() != 0) {
						labelledField.setLabelText(labelText);
					}
				}

				// si c'est un MTextField (MStringField mais aussi MIntegerField, MDoubleField),
				// ou un MStringArea,
				// ou un MLabelledField avec comme field un MTextField (idem) ou un MStringArea
				// et que la maxLength est -1,
				// alors on met la contrainte de longueur indiquée dans le DT

				if (adaptee instanceof MLabelledField) {
					labelledField = (MLabelledField) adaptee;
					if (MUtilities.getField(labelledField) instanceof MTextField && ((MTextField) MUtilities.getField(labelledField)).getDocument() instanceof MTextDocument) {
						textDocument = (MTextDocument) ((MTextField) MUtilities.getField(labelledField)).getDocument();
						if (textDocument.getMaxLength() == -1) {
							textDocument.setMaxLength(getMaxLength(identifier));
						}
					} else if (MUtilities.getField(labelledField) instanceof MStringArea && ((MStringArea) MUtilities.getField(labelledField)).getMaxLength() == -1) {
						((MStringArea) MUtilities.getField(labelledField)).setMaxLength(getMaxLength(identifier));
					}
				} else if (adaptee instanceof MTextField && ((MTextField) adaptee).getDocument() instanceof MTextDocument) {
					textDocument = (MTextDocument) ((MTextField) adaptee).getDocument();
					if (textDocument.getMaxLength() == -1) {
						textDocument.setMaxLength(getMaxLength(identifier));
					}
				} else if (adaptee instanceof MStringArea && ((MStringArea) adaptee).getMaxLength() == -1) {
					((MStringArea) adaptee).setMaxLength(getMaxLength(identifier));
				}
			}
		} catch (final Exception e) {
			handleError(e);
		}
	}

	/**
	 * Retourne un booléen selon que la propriété spécifiée est "not null" dans le DT.
	 * @param identifier String
	 * @return boolean
	 */
	private boolean isNotNull(final String identifier) {
		return false;
	}

	/**
	 * Retourne le libellé de la propriété spécifiée telle qu'indiquée dans le DT.
	 * @param identifier String
	 * @return String
	 */
	private String getLabelText(final String identifier) {
		return null;
	}

	/**
	 * Retourne la longueur maximale de la propriété spécifiée telle qu'indiqué dans le DT.
	 * @param identifier String
	 * @return String
	 */
	private int getMaxLength(final String identifier) {
		return -1;
	}
}
