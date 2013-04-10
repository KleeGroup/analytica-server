package com.kleegroup.analytica.hcube.query;

import com.kleegroup.analytica.hcube.dimension.Dimension;

/**
 * Une selection permet de définir un ensemble de position sur une dimension donnée.
 * @author npiedeloup, pchretien
 */
interface Selection<D extends Dimension<D>> {

	/**
	 * @return Dimension de la selection
	 */
	D getDimension();
}
