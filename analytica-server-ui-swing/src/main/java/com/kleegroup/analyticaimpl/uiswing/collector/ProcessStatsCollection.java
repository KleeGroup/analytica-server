package com.kleegroup.analyticaimpl.uiswing.collector;

import java.io.Serializable;
import java.util.Map;

public interface ProcessStatsCollection<P extends Serializable> extends Serializable {

	/**
	 * Retourne une map, ou la cl� est le nom d'une m�thode.
	 * La valeur et la technique a utiliser pour traiter les r�sultats d�pends de l'impl�mentation.
	 * Mais dans tous les cas les donn�es statistiques sont dans des ProcessStats
	 * @return Map
	 */
	Map<String, P> getResults();

	/**
	 * Permet de fusionner deux collections de resultats
	 * @param other ProcessStatsCollection
	 */
	void merge(ProcessStatsCollection<P> other);
}
