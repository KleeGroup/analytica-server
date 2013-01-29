/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidi�re - BP 159 - 92357 Le Plessis Robinson Cedex - France
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

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class JSFUtil {
	/**
	 * @return le context JSF de la servlet JSF
	 */
	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	/**
	 * @return la requ�te HTTP dans un context JSF
	 */
	public static HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	/**
	 * @return la session HTTP dans un context JSF
	 */
	public static HttpSession getHttpSession() {
		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	/**
	 * @return le flash scope
	 */
	public static Flash getFlashScope() {
		return FacesContext.getCurrentInstance().getExternalContext().getFlash();
	}

	/**
	 * Retourne la valeur du param�tre de la requ�te HTTP � partir d'une cl� (dans un context JSF)
	 * 
	 * @param key la cl� du param�tre
	 * @return la valeur du param�tre
	 */
	public static String getRequestParameter(final String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}

	/**
	 * Indique si la requ�te est un post back
	 * @return true si la requ�te est un post back
	 */
	public static boolean isPostBack() {
		final FacesContext context = FacesContext.getCurrentInstance();
		return context.getRenderKit().getResponseStateManager().isPostback(context);
	}

	/**
	 * Forward une requ�te vers la page indiqu�e.
	 * @param outcome Outcome de navigation
	 */
	public static MessageWriter forward(final String outcome) {
		return new MessageWriter(outcome, false);
	}

	/**
	 * @return MessageReader.
	 */
	public static MessageReader createMessageReader() {
		return new MessageReader();
	}

}
