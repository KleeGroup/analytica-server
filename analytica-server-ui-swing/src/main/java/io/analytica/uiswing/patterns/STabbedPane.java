package io.analytica.uiswing.patterns;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mswing.print.MPrintInformations;

/**
 * TabbedPane SAE (panel avec onglets).<br>
 * On ne laisse qu'un constructeur qui positionne TAB_PLACEMENT à TOP et TAB_LAYOUT_POLICY à SCROLL_TAB_LAYOUT,
 * comportement par défaut désiré dans le client SAE. <br>
 * De plus on définit une gestion des couleurs par défaut dans le SAE basés sur les propriétés
 * TabbedPane.passiveForeground, TabbedPane.activeForeground et TabbedPane.passiveBackground qui doivent être
 * initialisées par le client (sinon les couleurs par défaut sont utilisées : noir sur fond gris).
 * @version $Id: STabbedPane.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author Antoine GERARD
 */
public class STabbedPane extends JTabbedPane implements MPrintInformations {

	private static final long serialVersionUID = 933319440473168430L;
	private static final Color ONGLET_PASSIF_FOREGROUND_COLOR = UIManager.getColor("TabbedPane.passiveForeground");
	private static final Color ONGLET_ACTIF_FOREGROUND_COLOR = UIManager.getColor("TabbedPane.activeForeground");
	private static final Color ONGLET_PASSIF_BACKGROUND_COLOR = UIManager.getColor("TabbedPane.passiveBackground");

	public STabbedPane() {
		super(TOP, SCROLL_TAB_LAYOUT);
		// on ajoute un change listener pour mettre à jour les couleurs à chaque changement de l'onglet ouvert.
		// sinon le look and feel les changent à chaque sélection
		addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent changeEvent) {
				updateColors();
			}
		});
		updateColors();
	}

	/**
	 * Met à jour les couleurs des onglets en fonction de l'onglet ouvert.
	 */
	private void updateColors() {
		final int selectedIndex = getSelectedIndex();
		final int nbTabs = getTabCount();
		for (int index = 0; index < nbTabs; index++) {
			if (selectedIndex != index) {
				setForegroundAt(index, ONGLET_PASSIF_FOREGROUND_COLOR);
				setBackgroundAt(index, ONGLET_PASSIF_BACKGROUND_COLOR);
			} else {
				setForegroundAt(index, ONGLET_ACTIF_FOREGROUND_COLOR);
			}
		}
	}

	/**
	 * Cette méthode est appelée par toutes les autres méthodes permettant d'ajouter un onglet. On la surcharge pour
	 * mettre à jour les couleurs à chaque ajout d'onglet.
	 *
	 * @param title String
	 * @param icon Icon
	 * @param component Component
	 * @param tip String
	 * @param index int
	 */
	@Override
	public void insertTab(final String title, final Icon icon, final Component component, final String tip, final int index) {
		super.insertTab(title, icon, component, tip, index);
		updateColors();
	}

	/**
	 * Retourne le titre à inclure dans l'impression/export.
	 * @return java.lang.String
	 */
	public String getPrintTitle() {
		final int selectedIndex = getSelectedIndex();
		if (selectedIndex != -1) {
			return getTitleAt(selectedIndex);
		} else {
			return null;
		}
	}
}
