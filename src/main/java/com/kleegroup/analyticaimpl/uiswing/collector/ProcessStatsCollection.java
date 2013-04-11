package com.kleegroup.analyticaimpl.uiswing.collector;

import java.io.Serializable;
import java.util.Map;

public interface ProcessStatsCollection<P extends Serializable> extends Serializable {

	/**
	 * Retourne une map, ou la clé est le nom d'une méthode.
	 * La valeur et la technique a utiliser pour traiter les résultats dépends de l'implémentation.
	 * Mais dans tous les cas les données statistiques sont dans des ProcessStats
	 * @return Map
	 */
	Map<String, P> getResults();

	/**
	 * Permet de fusionner deux collections de resultats
	 * @param other ProcessStatsCollection
	 */
	void merge(ProcessStatsCollection<P> other);
}
