package com.kleegroup.analytica.restserver;

import vertigo.kernel.component.Manager;

/**
 * Manager serveur HTTP REST.
 * @author npiedeloup
 * @version $Id: QueryNetApiPlugin.java,v 1.1 2012/05/11 17:12:17 npiedeloup Exp $
 */
public interface RestServerManager extends Manager {

	/**
	 * Ajoute une classe gérant une ressource Web (préfixée par un @Path).
	 * @param handlerClass class gérant la resource Web.
	 */
	void addResourceHandler(Class<?> handlerClass);

}
