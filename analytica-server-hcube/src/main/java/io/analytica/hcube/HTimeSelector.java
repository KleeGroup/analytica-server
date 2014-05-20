package io.analytica.hcube;

import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.query.HTimeSelection;

import java.util.List;

public interface HTimeSelector {
	List<HTime> findTimes(HTimeSelection timeSelection);
}
