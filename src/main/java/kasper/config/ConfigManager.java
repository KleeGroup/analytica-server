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
package kasper.config;

import java.util.regex.Pattern;

import kasper.kernel.manager.Manager;

/**
 * Interface du gestionnaire de la configuration applicative.
 * Une configuration est identifiée par un chemin et possède une liste de propriétés. 
 * Une propriété est identifiée par un nom et possède une valeur.
 * Le chemmin est camelCase.camelCase et ne contient que des lettres et chiffres; les séparateurs sont des points.  
 * 	- Une propriété est camelCase et ne contient que des lettres et chiffres.  
 *  	- Une regex précise les chaines autorisées.
 * 
 * Les propriétés sont de trois types : 
 * -boolean
 * -String
 * -int
 *  
 * Le chemin des configuration est hiérachique, il y a un héritage implicite des propriétés. 
 * Le séparateur est le caractère point (.)
 * 
 * Même si une configuration n'est pas déclarée, la remontée est automatique. 
 *  
 * 
 * Exemple :
 * 
 * maconf:{
 *  mapropriete1:toto,
 *  mapropriete2:titi
 * }
 * 
 * maconf.subConf1:{
 *  mapropriete2:tata,
 *  mapropriete3:titi
 * }
 * 
 * maconf.subConf2:{
 *  mapropriete3:tata
 * }
 * 
 * getStringValue(maconf, mapropriete1) => toto
 * getStringValue(maconf.subConf1, mapropriete1) => toto  #La propriété 'mapropriete1' n'étant pas trouvée on remonte au parent.
 * 
 * getStringValue(maconf, mapropriete2) => titi
 * getStringValue(maconf.subConf1, mapropriete2) => tata #La propriété 'mapropriete2' est surchargée 
 *  
 * getStringValue(maconf.subConf2, mapropriete3) => tata
 * getStringValue(maconf, mapropriete3) => erreur #'mapropriete3' n'est pas déclarée dans maConf
 * 
 * getStringValue(maconf.unknown, mapropriete2) => titi 
 * getStringValue(maconf.subConf1.unknown, mapropriete2) => tata 
 * 
 * @author prahmoune, npiedeloup
 * @version $Id: ConfigManager.java,v 1.3 2013/01/14 16:35:20 npiedeloup Exp $ 
 */
public interface ConfigManager extends Manager {
	Pattern REGEX_PATH = Pattern.compile("([a-z][a-zA-Z0-9]*)([a-z][a-zA-Z0-9-]*.)*");
	Pattern REGEX_PROPERTY = Pattern.compile("[a-z][a-zA-Z0-9\\.]*");

	/**
	 * Retourne une implémentation à partir d'une interface ou d'un Bean.
	 * Celà permet de structurer les développements.
	 * @param <C> Type de l'interface de la configuration
	 * @param configPath Chemin décrivant la configuration
	 * @param configClass Interface ou Class de la configuration
	 */
	<C> C resolve(final String configPath, final Class<C> configClass);

	/**
	 * Retourne une propriété de configuration.
	 * @param configPath Chemin décrivant la configuration
	 * @param property Nom de la propriété de la configuration
	 * @return Valeur de la propriété
	 */
	String getStringValue(String configPath, String property);

	/**
	 * Retourne une propriété de configuration.
	 * @param configPath Chemin décrivant la configuration
	 * @param property Propriété de la configuration
	 * @return Valeur de la propriété
	 */
	int getIntValue(String configPath, String property);

	/**
	 * Retourne une propriété de configuration.
	 * @param configPath Chemin décrivant la configuration
	 * @param property Propriété de la configuration
	 * @return Valeur de la propriété
	 */
	boolean getBooleanValue(String configPath, String property);
}
