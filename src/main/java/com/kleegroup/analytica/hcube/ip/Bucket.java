package com.kleegroup.analytica.hcube.ip;

import com.kleegroup.analytica.hcube.dimension.Dimension;

/**
 * Un bucket contient des cubes.
 * @author npiedeloup, pchretien
 * @version $Id: Cube.java,v 1.6 2012/10/16 13:34:49 pchretien Exp $
 */
public interface Bucket {
	/**
	 * @return Nom du bucket.
	 */
	String getName();

	/**
	 * Liste des dimensions gérées par le bucket. 
	 */
	Dimension[] getDimensions();
}
