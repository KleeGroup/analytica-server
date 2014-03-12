package io.analytica.hcube.dimension;

/**
 * Une position est définie sur une dimension (ou axe).
 * Une position peut "contenir" d'autres positions (exemple des heures contenues dans les jours).  

 * @author pchretien
 */
interface HPosition<P extends HPosition<P>> {
	/**
	 * @return Position dans la dimension de niveau supérieure, ou null si plus de niveau au dessus.
	 */
	P drillUp();
}
