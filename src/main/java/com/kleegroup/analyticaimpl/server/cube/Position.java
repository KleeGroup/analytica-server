package com.kleegroup.analyticaimpl.server.cube;

import com.kleegroup.analytica.server.query.Dimension;

/**
 * Une position est définie sur une dimension (ou axe).
 * Une position peut "contenir" d'autres positions (exemple des heures contenues dans les jours).  

 * @author pchretien
 */
public interface Position<D extends Dimension<D>, P extends Position<D, P>> {
	/**
	 * Vérifie si la position est contenue dans une autre autre.
	 * Une position A est contenue dans une position B  
	 * Si A = B
	 * Si B peut être obtenu par drillUp successifs sur A.
	 * @param otherTime
	 * @return
	 */
	boolean isIn(final P otherPosition);

	D getDimension();

	P drillUp();
}
