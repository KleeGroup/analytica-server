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

import java.io.Serializable;

/**
 * Message de forward.
 *
 * @author npiedeloup
 * @version $Id: MessageWriter.java,v 1.1 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class MessageWriter {
	static final String REDIRECT_MESSAGE_MARKER = "redirect-message";
	static final String NULL_VALUE = "redirect-nullValue";
	private final StringBuilder newOutcome = new StringBuilder();
	private final boolean redirect;

	/**
	 * Constructeur.
	 * @param outcome Outcome d'origine
	 * @param redirect Si redirect
	 */
	MessageWriter(final String outcome, final boolean redirect) {
		this.redirect = redirect;
		newOutcome.append(outcome).append("?faces-redirect=").append(redirect);
		if (redirect) {
			JSFUtil.getHttpServletRequest().setAttribute(REDIRECT_MESSAGE_MARKER, Boolean.TRUE);
		}
	}

	/**
	 * Putters : ajout de données au corps du message.
	 * @param key Clé de l'objet
	 * @param value Valeur de l'objet
	 */
	public void put(final String key, final Serializable value) {
		final Serializable notNullValue = value != null ? value : NULL_VALUE;
		if (redirect) {
			newOutcome.append('&');
			newOutcome.append(key).append('=').append(String.valueOf(notNullValue));
		} else {
			JSFUtil.getHttpServletRequest().setAttribute(key, notNullValue);
		}
	}

	/**
	 * @return Outcome retourné par JSF.
	 */
	public String toOutcome() {
		return newOutcome.toString();
	}
}