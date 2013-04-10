package com.kleegroup.analytica.hcube.dimension;

/**
 * Une dimension ou axe
 * Une dimension est d�finie sur plusieurs niveaux navigable par drillUp ou drillDown.
 * @author npiedeloup, pchretien
 */
public interface Dimension<D extends Dimension<D>> {
	/**
	 * @return Niveau sup�rieur ou null.
	 */
	D drillUp();

	/**
	 * @return Niveau inf�rieur ou null.
	 */
	D drillDown();
}
