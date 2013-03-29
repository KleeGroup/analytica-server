package com.kleegroup.analytica.server.query;

/**
 * Une selection permet de définir un ensemble de position sur une dimension donnée.
 * @author npiedeloup, pchretien
 */
public interface Selection<D extends Dimension<D>> {

	/**
	 * @return Dimension de la selection
	 */
	D getDimension();
}
