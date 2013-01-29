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
package kasperimpl.jsf.component;

import kasper.kernel.Home;
import kasper.kernel.di.container.Container;
import kasper.kernel.di.injector.Injector;

import com.sun.faces.spi.InjectionProvider;
import com.sun.faces.spi.InjectionProviderException;
import com.sun.faces.vendor.WebContainerInjectionProvider;

/**
 * Wrapper d'injection.
 *  
 * Le mécanisme d'injcetion JSF est surchargé afin de permettre l'injection des propriétés par l'annotation @Inject. 
 * 
 * Le provider est défini dans le fichier web.xml
 * 
 * ex: 
 * 	<context-param>
 *		<param-name>com.sun.faces.injectionProvider</param-name>
 *		<param-value>kasperimpl.jsf.component.ComponentInjectionProvider</param-value>
 *	</context-param> 
 *  
 * @author pchretien
 * @version $Id: ComponentInjectionProvider.java,v 1.2 2013/01/25 10:53:37 npiedeloup Exp $
 */
public final class ComponentInjectionProvider implements InjectionProvider {
	private static final WebContainerInjectionProvider wcInjectionProvider = new WebContainerInjectionProvider();
	private static final Injector injector = new Injector();

	private final Container container;

	public ComponentInjectionProvider() {
		//container = new DualContainer(Home.getContainer().getRootContainer(), Home.getContainer().getManager(ComponentManager.class).getContainer());
		container = Home.getContainer().getRootContainer();
	}

	public void inject(final Object managedBean) throws InjectionProviderException {
		// allow the default injector to inject the bean.
		wcInjectionProvider.inject(managedBean);
		// then inject with the  injector.
		injector.injectMembers(managedBean, container);
	}

	public void invokePostConstruct(final Object managedBean) throws InjectionProviderException {
		// don't do anything here for guice, just let the default do its thing
		wcInjectionProvider.invokePostConstruct(managedBean);
	}

	public void invokePreDestroy(final Object managedBean) throws InjectionProviderException {
		wcInjectionProvider.invokePreDestroy(managedBean);
	}
}
