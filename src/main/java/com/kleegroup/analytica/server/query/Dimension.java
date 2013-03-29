package com.kleegroup.analytica.server.query;

/**
 * Une dimension ou axe
 * Une dimension est définie sur plusieurs niveaux navigable par drillUp ou drillDown.
 * @author npiedeloup, pchretien
 */
public interface Dimension<D extends Dimension<D>> {
	/**
	 * @return Nom de l'axe
	 */
	String getLabel();

	/**
	 * @return Niveau supérieur ou null.
	 */
	D drillUp();

	/**
	 * @return Niveau inférieur ou null.
	 */
	D drillDown();
}
