package com.kleegroup.analytica.hcube.dimension;


/**
 * Une position est d�finie sur une dimension (ou axe).
 * Une position peut "contenir" d'autres positions (exemple des heures contenues dans les jours).  

 * @author pchretien
 */
public interface Position<D extends Dimension<D>, P extends Position<D, P>> {
	/**
	 * 
	 * @return Dimension
	 */
	D getDimension();

	/**
	 * @return Position dans la dimension de niveau sup�rieure, ou null si plus de niveau au dessus.
	 */
	P drillUp();
}
