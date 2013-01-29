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
package com.kleegroup.analytica.server;

import java.util.Date;
import java.util.List;

import kasper.kernel.manager.Manager;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.server.data.Data;
import com.kleegroup.analytica.server.data.DataKey;
import com.kleegroup.analytica.server.data.DataSet;
import com.kleegroup.analytica.server.data.TimeSelection;
import com.kleegroup.analytica.server.data.WhatSelection;

/**
 * Serveur de Analytica.
 * R�ception des donn�es collect�es
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public interface ServerManager extends Manager {
	/**
	 * Ajout d'un process.
	 * @param process Process � ajouter 
	 */
	void push(KProcess process);

	/**
	 * Permet de diffuser les process dans les cubes.
	 * @return nombre de process trait�s
	 */
	int store50NextProcessesAsCube();

	/**
	 * @param timeSelection TimeSelection parent
	 * @return Liste des TimeSelections sous jacentes
	 */
	List<TimeSelection> getSubTimeSelections(final TimeSelection timeSelection);

	/**
	 * @param timeSelection TimeSelection parent
	 * @param whatSelection WhatSelection parent
	 * @return Liste des WhatSelection sous jacentes sur une dur�e donn�e
	 */
	List<WhatSelection> getSubWhatSelections(final TimeSelection timeSelection, final WhatSelection whatSelection);

	/**
	 * @param timeSelection TimeSelection parent
	 * @param whatSelection WhatSelection parent
	 * @return Liste des DataKey possibles
	 */
	List<DataKey> getSubDataKeys(final TimeSelection timeSelection, final WhatSelection whatSelection);

	/**
	 * Fournit une liste de metric agregg�e sur des dates et des what.
	 * @param timeSelection Selection de date a aggr�ger avant resultat
	 * @param whatSelection Selection des WhatPositions a aggr�ger avant resultat
	 * @param metrics Liste des m�trics recherch�es
	 * @return Liste de m�tric pour l'ensemble des WhatPosition sur l'interval de date
	 */
	List<Data> getData(final TimeSelection timeSelection, final WhatSelection whatSelection, final List<DataKey> metrics);

	/**
	 * Fournit une liste de donn�es sur un interval de date, en ASSEMBLANT les WhatPosition r�cup�r�s (par exemple un essemble coh�rent de What).
	 * @param timeSelection Selection de date, la dimension d�terminera le niveau d'agregation
	 * @param whatSelection Selection des WhatPositions a aggr�ger avant resultat
	 * @param metrics Liste des m�trics recherch�es
	 * @return Liste de valeur par m�tric pour l'ensemble des WhatPosition et par date (permet des courbes de metrics entre deux dates pour un ensemble de what)
	 */
	List<DataSet<Date, ?>> getDataTimeLine(final TimeSelection timeSelection, final WhatSelection whatSelection, final List<DataKey> metrics);

	/**
	 * Fournit une liste de donn�es sur un ensemble de what, en ASSEMBLANT les TimePosition r�cup�r�s (par exemple 30 jours glissant).
	 * @param timeSelection Selection de date a aggr�ger avant resultat
	 * @param whatSelection Selection de WhatPositions, la dimension d�terminera le niveau d'agregation
	 * @param metrics Liste des m�trics recherch�es
	 * @return Liste de valeur par m�tric pour l'ensemble des TimePosition et par what (permet des courbes de metrics entre deux what sur une p�riode)
	 */
	List<DataSet<String, ?>> getDataWhatLine(final TimeSelection timeSelection, final WhatSelection whatSelection, final List<DataKey> metrics);

}
