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
package kasperimpl.ui.plugins.servlet;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import kasper.kernel.lang.Option;
import kasper.kernel.util.Assertion;
import kasperimpl.resource.ResourceResolverPlugin;

/**
 * Résolution des URL liées à la servlet.
 * @author prahmoune
 * @version $Id: ServletResourceResolverPlugin.java,v 1.1 2013/01/14 16:35:20 npiedeloup Exp $ 
 */
public final class ServletResourceResolverPlugin implements ResourceResolverPlugin {
	private static final ThreadLocal<ServletContext> servletContextRef = new ThreadLocal<ServletContext>();

	public static void setServletContext(final ServletContext servletContext) {
		Assertion.notNull(servletContext);
		//---------------------------------------------------------------------
		servletContextRef.set(servletContext);
	}

	private final ServletContext servletContext;

	public ServletResourceResolverPlugin() {
		Assertion.notNull(servletContextRef.get());
		//---------------------------------------------------------------------
		servletContext = servletContextRef.get();
	}

	/** {@inheritDoc} */
	public Option<URL> resolve(final String resource) {
		Assertion.notNull(resource);
		// ---------------------------------------------------------------------
		// 2. On recherche dans le context de la webapp
		try {
			return Option.some(servletContext.getResource(resource));
		} catch (final MalformedURLException e) {
			return Option.none();
		}
	}
}
