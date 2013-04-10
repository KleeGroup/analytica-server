package com.kleegroup.analytica.hcube.trash;

import java.util.List;

import com.kleegroup.analytica.hcube.dimension.Dimension;

/**
 * Une hiérachie est constituée de niveaux appelés dimensions. 
 * Il existe par exemple 
 *  - une hiérarchie temporelle constituée des dimensions années, mois, jours...
 *  - une hiérachie géographique constituée des dimensions pays, régions(ou équivalent)
 * 
 * @author pchretien
 */
public interface Hierarchy<D extends Dimension<D>> {
	/**
	 * @return Nom de la hiérachie.
	 */
	String getName();

	/**
	 * Liste des dimensions participant à la hiérachie.
	 * La liste est ordonnée du général (ex année) vers le particulier (ex minute). 
	 * @return Liste des dimensions participant à la hiérachie 
	 */
	List<D> getDimensions();

	/**
	 * @return Niveau supérieur ou null.
	 */
	D drillUp(D dimension);

	/**
	 * @return Niveau inférieur ou null.
	 */
	D drillDown(D dimension);
}
