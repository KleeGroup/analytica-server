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
package com.kleegroup.analytica.server;

import java.util.Date;
import java.util.List;

import kasper.kernel.manager.Manager;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.hcube.query.Query;
import com.kleegroup.analytica.server.data.Data;
import com.kleegroup.analytica.server.data.DataSet;

/**
 * Serveur de Analytica.
 * Réception des données collectées
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public interface ServerManager extends Manager {
	/**
	 * Ajout d'un process.
	 * @param process Process à ajouter 
	 */
	void push(KProcess process);

	/**
	 * Permet de diffuser les process dans les cubes.
	 * @return nombre de process traités
	 */
	int store50NextProcessesAsCube();

	/**
	 * Fournit une liste de metric agreggée sur des dates et des what.
	 * @param query  Requête précisant les selections a aggréger avant resultat
	 * @return Liste de métric pour l'ensemble des WhatPosition sur l'interval de date
	 */
	List<Data> getData(final Query query);

	/**
	 * Fournit une liste de données sur un interval de date, en ASSEMBLANT les WhatPosition récupérés (par exemple un essemble cohérent de What).
	 * @param query  Requête précisant les selections
	 * @return Liste de valeur par métric pour l'ensemble des WhatPosition et par date (permet des courbes de metrics entre deux dates pour un ensemble de what)
	 */
	List<DataSet<Date, ?>> getDataTimeLine(final Query query);

	/**
	 * Fournit une liste de données sur un ensemble de what, en ASSEMBLANT les TimePosition récupérés (par exemple 30 jours glissant).
	 * @param query  Requête précisant les selections
	 * @return Liste de valeur par métric pour l'ensemble des TimePosition et par what (permet des courbes de metrics entre deux what sur une période)
	 */
	List<DataSet<String, ?>> getDataWhatLine(final Query query);

}
