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
package com.kleegroup.analyticaimpl.server;

import io.vertigo.kernel.component.Plugin;

import java.util.List;

import com.kleegroup.analytica.core.KProcess;

/**
 * Plugin gérant le stockage des process.
 * @author npiedeloup
 * @version $Id: ProcessStorePlugin.java,v 1.2 2012/04/06 16:06:46 npiedeloup Exp $
 */
public interface ProcessStorePlugin extends Plugin {
	/**
	 * Ajout un processus identifié.
	 * @param process processus identifié.
	 */
	void add(KProcess process);

	/**
	 * Liste des process suivant.
	 * @param lastId Dernier id de process chargé (exclus du resultat)
	 * @param maxRow Nombre de ligne max
	 * @return Liste des process suivant
	 */
	List<Identified<KProcess>> getProcess(final String lastId, final Integer maxRow);
}
