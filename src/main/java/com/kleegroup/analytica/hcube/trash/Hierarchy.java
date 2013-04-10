package com.kleegroup.analytica.hcube.trash;

import java.util.List;

import com.kleegroup.analytica.hcube.dimension.Dimension;

/**
 * Une hi�rachie est constitu�e de niveaux appel�s dimensions. 
 * Il existe par exemple 
 *  - une hi�rarchie temporelle constitu�e des dimensions ann�es, mois, jours...
 *  - une hi�rachie g�ographique constitu�e des dimensions pays, r�gions(ou �quivalent)
 * 
 * @author pchretien
 */
public interface Hierarchy<D extends Dimension<D>> {
	/**
	 * @return Nom de la hi�rachie.
	 */
	String getName();

	/**
	 * Liste des dimensions participant � la hi�rachie.
	 * La liste est ordonn�e du g�n�ral (ex ann�e) vers le particulier (ex minute). 
	 * @return Liste des dimensions participant � la hi�rachie 
	 */
	List<D> getDimensions();

	/**
	 * @return Niveau sup�rieur ou null.
	 */
	D drillUp(D dimension);

	/**
	 * @return Niveau inf�rieur ou null.
	 */
	D drillDown(D dimension);
}
