package com.kleegroup.analyticaimpl.server.cube;

import com.kleegroup.analytica.server.query.Dimension;

/**
 * Une position est définie sur une dimension (ou axe).
 * Une position peut "contenir" d'autres positions (exemple des heures contenues dans les jours).  

 * @author pchretien
 */
public interface Position<D extends Dimension<D>, P extends Position<D, P>> {
	D getDimension();

	P drillUp();
}
