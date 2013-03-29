package com.kleegroup.analytica.server.query;

/**
 * Une selection permet de d�finir un ensemble de position sur une dimension donn�e.
 * @author npiedeloup, pchretien
 */
public interface Selection<D extends Dimension<D>> {

	/**
	 * @return Dimension de la selection
	 */
	D getDimension();
}
