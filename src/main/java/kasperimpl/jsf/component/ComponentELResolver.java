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

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;

import kasper.kernel.Home;
import kasper.kernel.di.container.Container;

/**
 * Cette classe assure une redirection de la recherche d'un composant depuis JSF vers le conteneur de composants.
 *
 * La déclaration du resolver est à ajouter dans le fichier faces-config.xml.
 * 
 * ex:
 * <el-resolver>kasperimpl.jsf.component.ComponentELResolver</el-resolver>
 * 
 * @author prahmoune
 * @version $Id: ComponentELResolver.java,v 1.5 2013/01/25 10:53:37 npiedeloup Exp $
 */
public final class ComponentELResolver extends ELResolver {
	/** Conteneur de composants. */
	private final Container container;

	/**
	 * Constructeur.
	 */
	public ComponentELResolver() {
		//container = new DualContainer(Home.getContainer().getManager(ComponentManager.class).getContainer(), Home.getContainer().getRootContainer());
		container = Home.getContainer().getRootContainer();
	}

	/**
	 * Recherche d'un composant dans le conteneur de composants; sinon, delegue à la chaîne d'ELResolver.
	 */
	@Override
	public Object getValue(final ELContext elContext, final Object base, final Object property) {
		// If we have a non-null base object, function as a PropertyResolver
		if (null == base) {
			// function as a VariableResolver
			if (null == property) {
				throw new PropertyNotFoundException("ComponentELResolver: la propriété ne doit pas être null");
			}
			final String service = (String) property;
			if (container.contains(service)) {
				final Object result = container.resolve(service, Object.class);
				elContext.setPropertyResolved(true);
				return result;
			}
		}
		// le composant n'est pas trouvé
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Class<?> getType(final ELContext elContext, final Object base, final Object property) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void setValue(final ELContext elContext, final Object base, final Object property, final Object value) {
		if (null == base) {
			final String service = (String) property;
			if (container.contains(service)) {
				throw new ELException("Can not set value on '" + property + "'.");
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isReadOnly(final ELContext elContext, final Object base, final Object property) {
		if (null == base) {
			final String service = (String) property;
			if (container.contains(service)) {
				elContext.setPropertyResolved(true);
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext elContext, final Object base) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
		return Object.class;
	}
}
