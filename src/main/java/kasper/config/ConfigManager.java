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
package kasper.config;

import java.util.regex.Pattern;

import kasper.kernel.manager.Manager;

/**
 * Interface du gestionnaire de la configuration applicative.
 * Une configuration est identifi�e par un chemin et poss�de une liste de propri�t�s. 
 * Une propri�t� est identifi�e par un nom et poss�de une valeur.
 * Le chemmin est camelCase.camelCase et ne contient que des lettres et chiffres; les s�parateurs sont des points.  
 * 	- Une propri�t� est camelCase et ne contient que des lettres et chiffres.  
 *  	- Une regex pr�cise les chaines autoris�es.
 * 
 * Les propri�t�s sont de trois types : 
 * -boolean
 * -String
 * -int
 *  
 * Le chemin des configuration est hi�rachique, il y a un h�ritage implicite des propri�t�s. 
 * Le s�parateur est le caract�re point (.)
 * 
 * M�me si une configuration n'est pas d�clar�e, la remont�e est automatique. 
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
 * getStringValue(maconf.subConf1, mapropriete1) => toto  #La propri�t� 'mapropriete1' n'�tant pas trouv�e on remonte au parent.
 * 
 * getStringValue(maconf, mapropriete2) => titi
 * getStringValue(maconf.subConf1, mapropriete2) => tata #La propri�t� 'mapropriete2' est surcharg�e 
 *  
 * getStringValue(maconf.subConf2, mapropriete3) => tata
 * getStringValue(maconf, mapropriete3) => erreur #'mapropriete3' n'est pas d�clar�e dans maConf
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
	 * Retourne une impl�mentation � partir d'une interface ou d'un Bean.
	 * Cel� permet de structurer les d�veloppements.
	 * @param <C> Type de l'interface de la configuration
	 * @param configPath Chemin d�crivant la configuration
	 * @param configClass Interface ou Class de la configuration
	 */
	<C> C resolve(final String configPath, final Class<C> configClass);

	/**
	 * Retourne une propri�t� de configuration.
	 * @param configPath Chemin d�crivant la configuration
	 * @param property Nom de la propri�t� de la configuration
	 * @return Valeur de la propri�t�
	 */
	String getStringValue(String configPath, String property);

	/**
	 * Retourne une propri�t� de configuration.
	 * @param configPath Chemin d�crivant la configuration
	 * @param property Propri�t� de la configuration
	 * @return Valeur de la propri�t�
	 */
	int getIntValue(String configPath, String property);

	/**
	 * Retourne une propri�t� de configuration.
	 * @param configPath Chemin d�crivant la configuration
	 * @param property Propri�t� de la configuration
	 * @return Valeur de la propri�t�
	 */
	boolean getBooleanValue(String configPath, String property);
}
