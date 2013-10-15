package com.kleegroup.analyticaimpl.server;

import vertigo.kernel.component.Plugin;

/**
 * Plugin gérant l'api reseau de requetage des cubes.
 * Plugin proposant une API pour la méthode execute(HQuery) du ServerManager.
 * @author npiedeloup
 * @version $Id: QueryNetApiPlugin.java,v 1.1 2012/05/11 17:12:17 npiedeloup Exp $
 */
public interface QueryNetApiPlugin extends Plugin {
	// Wrappe cette méthode :  
	// /**
	//	 * Execute une requête et fournit en retour un cube virtuel, constitué d'une liste de cubes.  
	//	 * @param query Paramètres de la requete
	//	 * @return cube virtuel, constitué d'une liste de cubes
	//	 */
	//	HResult execute(HQuery query);
}
