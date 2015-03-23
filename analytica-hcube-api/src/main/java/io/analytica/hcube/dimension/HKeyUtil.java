package io.analytica.hcube.dimension;

import java.util.ArrayList;
import java.util.List;

public class HKeyUtil {
	/**
	 * Calcule la liste de tous les cubes auxquels le pr�sent cube appartient
	 * Cette m�thode permet de pr�parer toutes les agr�gations.
	 * @return Liste de tous les cubes auxquels le pr�sent cube appartient
	 */
	public static List<HKey> drillUp(final HKey key) {
		final List<HKey> upperKeys = new ArrayList<>();
		//on remonte les axes, le premier sera le plus bas niveau

		for (HTime time = key.getTime(); time != null; time = time.drillUp()) {
			for (HCategory category = key.getCategory(); category != null; category = category.drillUp()) {
				for (HLocation location = key.getLocation(); location != null; location = location.drillUp()) {
					upperKeys.add(new HKey(location, time, category));
				}
			}
		}
		return upperKeys;
	}
}
