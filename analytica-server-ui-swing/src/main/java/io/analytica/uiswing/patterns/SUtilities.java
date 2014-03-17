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

import io.vertigo.kernel.exception.VRuntimeException;
import io.vertigo.kernel.exception.VUserException;
import io.vertigo.kernel.lang.MessageText;

import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.print.PrinterJob;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.print.PrintService;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import mswing.MDialog;
import mswing.MImageIconCache;
import mswing.MUtilities;

/**
 * Classe utilitaire pour la gestion d'exception et pour l'exécution en tests de composants Swing.
 * @version $Id: SUtilities.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author Emeric Vernat
 */
public class SUtilities {
	// fichiers à supprimer à la fermeture de l'application (et aussi à la relève d'opérateur)
	private static final List<String> TEMPORARY_FILES = new ArrayList<String>();
	private static final Logger LOG = Logger.getLogger(SUtilities.class.getName());

	/**
	 * Constructeur.
	 * (private : pas d'instance)
	 */
	private SUtilities() {
		//contructeur privé
	}

	/**
	* Retourne la frame principale de l'application.
	* @return MainFrame
	*/
	public static SFrame getMainFrame() {
		return (SFrame) MDialog.getMainFrame();
	}

	/**
	 * Trace l'exception et affiche une boîte de dialogue d'erreur.
	 * @param throwable java.lang.Throwable
	 */
	public static void handleException(final Throwable throwable) {
		if (throwable instanceof OutOfMemoryError || throwable instanceof VRuntimeException || throwable instanceof Error || throwable instanceof UnmarshalException || throwable instanceof ConnectException) {
			// note : SAssertion hérite de Error et est donc considérée aussi comme erreur système
			handleError(throwable);

		} else {
			throwable.printStackTrace();
			String message;
			if (isOracleUserException(throwable)) {
				// c'est une erreur fonctionnelle lancée dans une procédure stockée Oracle
				// le message d'erreur à afficher à l'utilisateur est taggé par <text>message</text>
				message = extractOracleUserExceptionMessage(throwable);
				handleThrowable(new VUserException(new MessageText(message, null)), false);
			} else if (throwable instanceof RemoteException && exceptionContains(throwable, "Transaction timed out")) {
				message = "Le délai maximum pour effectuer l'opération a expiré.\nVeuillez relancer votre opération s'il y a lieu.";
				handleThrowable(new VUserException(new MessageText(message, null)), false);
			} else {
				handleThrowable(throwable, false);
			}
		}
	}

	/**
	 * Trace l'exception et affiche une boîte de dialogue d'erreur système.
	 * @param throwable java.lang.Throwable
	 */
	public static void handleError(final Throwable throwable) {
		throwable.printStackTrace();
		// l'affichage des erreurs systèmes aux utilisateurs est encapsulée avec le message "Veuillez contacter..."
		if (throwable instanceof VRuntimeException && exceptionContains(throwable, "[Erreur SQL]")) {
			if (exceptionContains(throwable, "ORA-01089")) {
				// gestion erreurs oracle "ORA-01089: immediate shutdown in progess - no operations are permitted"
				// pour afficher un message plus lisible
				handleThrowable(new VRuntimeException("Veuillez contacter un administrateur.\n(" + "La base de données SAE est en train de s'arrêter.)", throwable.getCause()), true);
			} else {
				// dans kasper.model.ServiceProviderSQL.handleException, kasper masque la sqlexception
				// par une KSystemException et remplace le message oracle par la requête sql à l'origine de l'erreur
				// (horrible pour un utilisateur qui préférerait le simple message oracle, qui lui peut être lu à un administrateur,
				// "ORA-02291: violation de contrainte (SAE.FMC_REF_URGENCE_FK) d'intégrité - touche parent introuvable")
				Throwable t = throwable;
				while (t != null && t.getCause() != null && t.getCause() != t && !(t instanceof SQLException)) {
					t = t.getCause();
				}
				handleThrowable(new VRuntimeException("Veuillez contacter un administrateur.\n(" + t.toString() + ')', throwable), true);
			}
		} else if (throwable instanceof OutOfMemoryError) {
			handleThrowable(new Exception("L'application ne dispose pas de suffisamment de mémoire pour fonctionner, elle va s'arrêter", throwable), true);
			System.exit(2);
		} else {
			handleThrowable(new VRuntimeException("Veuillez contacter un administrateur.\n(" + throwable.toString() + ')', throwable), true);
		}
	}

	/**
	 * Indique si le throwable est une erreur utilisateur remontée depuis une procédure stockée Oracle
	 * (dans ce cas le message d'erreur à afficher à l'utilisateur est taggé comme suit : <text>message</text>).
	 * @param throwable Throwable
	 * @return boolean
	 */
	public static boolean isOracleUserException(final Throwable throwable) {
		final String message = throwable.getMessage();
		return throwable instanceof VUserException && message != null && message.indexOf("<text>") != -1 && message.indexOf("</text>") != -1;
	}

	/**
	 * Extrait le message d'erreur depuis un message d'erreur utilisateur remontée depuis une procédure stockée Oracle
	 * (dans ce cas le message d'erreur à afficher à l'utilisateur est taggé comme suit : <text>message</text>).
	 * @param throwable Throwable
	 * @return String
	 */
	public static String extractOracleUserExceptionMessage(final Throwable throwable) {
		String message = throwable.getMessage();
		message = message.substring(message.indexOf("<text>") + "<text>".length());
		return message.substring(0, message.indexOf("</text>"));
	}

	/**
	 * Affiche la boîte de dialogue d'erreur avec MSwing (sauf si il s'agit générateur d'image synoptique)
	 * @param throwable Throwable
	 * @param isSystemError boolean
	 */
	private static void handleThrowable(final Throwable throwable, final boolean isSystemError) {
		MUtilities.handleThrowable(throwable, isSystemError);
	}

	private static boolean exceptionContains(final Throwable throwable, final String stringContained) {
		if (throwable != null && throwable.getMessage() != null && throwable.getMessage().indexOf(stringContained) != -1) {
			return true;
		} else if (throwable != null && throwable.getCause() != null && throwable.getCause() != throwable) {
			return exceptionContains(throwable.getCause(), stringContained);
		} else {
			return false;
		}
	}

	public static boolean showConfirmDialog(final Component parentComponent, final String message) {
		final int result = JOptionPane.showConfirmDialog(parentComponent, message, "Confirmation", JOptionPane.YES_NO_OPTION);
		return result == JOptionPane.YES_OPTION;
	}

	/**
	 * Retourne une imageIcon à partir du cache, en la chargeant auparavant si elle n'y est pas déjà.
	 * @return javax.swing.ImageIcon
	 * @param iconName java.lang.String
	 */
	public static ImageIcon getImageIconFromCache(final String iconName) {
		return MImageIconCache.getImageIcon("/img/" + iconName);
	}

	/**
	 * Retourne la fonte par défaut des labels.
	 * <br>Cela est utile par exemple pour faire un deriveFont
	 * (sans avoir à instancier une fonte en spécifiant la taille).
	 * @return java.awt.Font
	 */
	public static Font getDefaultLabelFont() {
		return UIManager.getFont("Label.font");
	}

	/**
	 * Retourne la fonte par défaut des labels en style gras.
	 * <br>Cela est utile par exemple pour éviter de faire un deriveFont sur getDefaultLabelFont())
	 * (donc sans avoir à instancier une fonte avec un simple getDefaultLabelFont().deriveFont(BOLD)).
	 * @return java.awt.Font
	 */
	public static Font getDefaultLabelBoldFont() {
		return UIManager.getFont("Label.boldFont");
	}

	/**
	 * Retourne l'instance courante de la classe componentClass contenant l'élément component.
	 * <BR>Cette méthode peut-être très utile pour récupérer une référence
	 * à un parent éloigné (ancêtre), en l'absence de référence directe du type attribut.
	 *
	 * <BR>Ex : un composant detailPanel désire une référence sur son
	 * internalFrame parente, alors l'instruction suivante suffit :
	 * 		getAncestorOfClass(JInternalFrame.class, detailPanel)
	 * <BR>Rq : si dans l'application il y a deux instances de cette frame
	 * et deux instances du detailPanel, l'instance renvoyée serait bien
	 * celle parente (ce qui n'est pas possible avec des singletons statiquesc
	 * et beaucoup plus souple que l'écriture de références directes).
	 *
	 * <BR>De plus, si aucune instance parente n'est trouvée mais qu'il y a un MMDetailDialog parent,
	 * la recherche continue à partir du MMasterDetailPanel lié.
	 *
	 * @return java.awt.Component
	 * @param componentClass java.lang.Class
	 * @param component java.awt.Component
	 */
	public static Component getAncestorOfClass(final Class componentClass, final Component component) {
		return MUtilities.getAncestorOfClass(componentClass, component);
	}

	/**
	 * Retourne l'instance courante de Dialog ou Frame contenant l'élément component.
	 * <br>Cette méthode peut-être très utile pour récupérer une référence
	 * à un parent éloigné (ancêtre) de type Dialog ou Frame en l'absence de référence directe du type attribut.
	 * <br>Ex : un composant detailPanel désire une référence sur sa dialog ou frame parente,
	 * alors l'instruction suivante suffit :
	 * 		getWindowForComponent(detailPanel)
	 * @param component java.awt.Component
	 * @return java.awt.Window
	 */
	public static Window getWindowForComponent(final Component component) {
		return SwingUtilities.windowForComponent(component);
	}

	/**
	 * Vérifie si une imprimante est configurée sur le poste.
	 * @return boolean
	 */
	public static boolean checkHasDefaultPrinter() {
		final PrintService printService = PrinterJob.getPrinterJob().getPrintService();
		return printService != null;
	}

	public static void cancelTimerTaskIfWindowDisposed(final TimerTask task, final Component attachedComponent) {
		final Window window = SUtilities.getWindowForComponent(attachedComponent);
		if (window == null || !window.isVisible()) {
			// note : la task et donc la référence sont apparemment libérées lors de la prochaine exécution prévue,
			// le component et la window (frame ou dialog) seront donc éventuellement garbage collectés à ce moment là
			task.cancel();
		}
	}

}
