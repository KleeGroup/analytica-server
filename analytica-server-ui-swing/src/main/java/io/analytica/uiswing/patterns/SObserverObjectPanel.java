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

import java.awt.LayoutManager;
import java.util.Observable;
import java.util.Observer;

/**
 * ObjectPanel observer.
 * @version $Id: SObserverObjectPanel.java,v 1.1 2012/01/13 13:43:55 npiedeloup Exp $
 * @author Antoine GERARD
 */
public class SObserverObjectPanel extends SObjectPanel implements Observer {
	private static final long serialVersionUID = 1521971951158954886L;

	/**
	 * Constructeur.
	 */
	public SObserverObjectPanel() {
		super();
	}

	/**
	 * Constructeur.
	 * @param layout java.awt.LayoutManager
	 */
	public SObserverObjectPanel(final LayoutManager layout) {
		super(layout);
	}

	/**
	 * Définit la valeur de la propriété object.
	 * <br>Sur le setObject on désinscrit la vue de l'ancien objet et on l'inscrit sur le nouvel objet
	 * en tant qu'Observer.
	 * @param object java.lang.Object
	 */
	@Override
	public void setObject(final Object object) {
		final Object formerObject = getObject();
		if (formerObject instanceof Observable) {
			((Observable) formerObject).deleteObserver(this);
		}
		if (object instanceof Observable) {
			((Observable) object).addObserver(this);
		}
		super.setObject(object);
	}

	/**
	 * Signale que l'objet associé à ce Panel a envoyé une notification de modification.
	 * On fait un setObject sur l'observable.
	 * A surcharger si on veut un comportement plus fin.
	 * @param observable Observable
	 * @param object java.lang.Object
	 */
	public void update(final Observable observable, final Object object) {
		//sae.client.log.ClientLog.fine("updateObserver reçu dans " + getClass() + " pour " + observable);
		setObject(observable);
	}
}
