package application.museum;

import com.kleegroup.analytica.core.KProcess;

public interface PageListener {
	/**
	 * Ajout d'une page.
	 * @param process Process à ajouter 
	 */
	void onPage(KProcess process);
}
