package io.analytica.museum;

import io.analytica.api.KProcess;

public interface PageListener {
	/**
	 * Ajout d'une page.
	 * @param process Process � ajouter 
	 */
	void onPage(KProcess process);
}
