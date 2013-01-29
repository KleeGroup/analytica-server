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
package kasper.jsf.util;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import kasper.kernel.lang.MessageText;

/**
 * Classe utilitaire pour les messages JSF.
 * @author prahmoune
 * @version $Id: MessageUtil.java,v 1.1 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class MessageUtil {
	private MessageUtil() {
		//
	}

	/**
	 * ClientId pour les messages 'growl'.
	 * 
	 * Note: Ajouter le tag <k:growl/> dans la vue JSF 
	 */
	public static final String GROWL_CLIENT_ID = "growl:growl";

	/**
	 * ClientId pour les messages 'msgs' (messages par défaut). 
	 *
	 * Note: Ajouter le tag <k:messages/> dans la vue JSF
	 */
	public static final String MSGS_CLIENT_ID = "messages:messages";

	/**
	 * Ajoute un message dans le context de la vue JSF
	 * @param message le message	 
	 */
	public static void message(final FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(MSGS_CLIENT_ID, message);
	}

	/**
	 * Ajoute un message dans le context de la vue JSF.
	 * @param clientId identifiant du composant dans la vue JSF
	 * @param message le message	 
	 */
	public static void message(final String clientId, final FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(clientId, message);
	}

	/**
	 * Ajoute un message d'information localisé dans le context de la vue JSF.
	 * @param text le message 	 	 	 
	 */
	public static void info(final MessageText text) {
		log(MSGS_CLIENT_ID, text, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Ajoute un message warning localisé dans le context de la vue JSF.
	 * @param text le message	 	 
	 */
	public static void warn(final MessageText text) {
		log(MSGS_CLIENT_ID, text, FacesMessage.SEVERITY_WARN);
	}

	/**
	 * Ajoute un message d'erreur localisé dans le context de la vue JSF.
	 * @param text le message	 
	 */
	public static void error(final MessageText text) {
		log(MSGS_CLIENT_ID, text, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Ajoute un message d'erreur fatale localisé dans le context de la vue JSF.
	 * @param text le message	 	 
	 */
	public static void fatal(final MessageText text) {
		log(MSGS_CLIENT_ID, text, FacesMessage.SEVERITY_FATAL);
	}
	/**
	 * Ajoute un message d'erreur fatale localisé dans le context de la vue JSF.
	 * @param clientId identifiant du composant dans la vue JSF
	 * @param text le message	 	  
	 */
	private static void log(final String clientId, final MessageText text, final Severity severity) {
		final FacesMessage message = new FacesMessage(severity, text.getDisplay(), null);
		message(clientId, message);
	}
}
