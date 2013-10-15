package com.kleegroup.analyticaimpl.server;

import vertigo.kernel.component.Plugin;

/**
 * Plugin g�rant l'api reseau de requetage des cubes.
 * Plugin proposant une API pour la m�thode execute(HQuery) du ServerManager.
 * @author npiedeloup
 * @version $Id: QueryNetApiPlugin.java,v 1.1 2012/05/11 17:12:17 npiedeloup Exp $
 */
public interface QueryNetApiPlugin extends Plugin {
	// Wrappe cette m�thode :  
	// /**
	//	 * Execute une requ�te et fournit en retour un cube virtuel, constitu� d'une liste de cubes.  
	//	 * @param query Param�tres de la requete
	//	 * @return cube virtuel, constitu� d'une liste de cubes
	//	 */
	//	HResult execute(HQuery query);
}
