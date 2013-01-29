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

import kasper.kernel.util.Assertion;

/**
 * Message de forward
 *
 * @author npiedeloup
 * @version $Id: MessageReader.java,v 1.1 2013/01/14 16:35:20 npiedeloup Exp $
 */
public final class MessageReader {
	//	private final StringBuilder newOutcome = new StringBuilder();
	private final boolean redirect;

	/**
	 * Constructeur.
	 */
	MessageReader() {
		redirect = JSFUtil.getHttpServletRequest().getAttribute(MessageWriter.REDIRECT_MESSAGE_MARKER) != null;
	}

	/**
	 * Getters : Récupère une données du corps du message.
	 * @param <V> Type de la valeur
	 * @param key Clé de l'objet
	 * @return Valeur de l'objet
	 */
	public <V extends Serializable> V get(final String key) {
		final V value;
		if (redirect) {
			value = (V) JSFUtil.getHttpServletRequest().getParameter(key);
		} else {
			value = (V) JSFUtil.getHttpServletRequest().getAttribute(key);
		}
		Assertion.notNull(value, "Le paramètre {0} n''a pas été passé.", key);
		return MessageWriter.NULL_VALUE.equals(value) ? null : value;
	}
}