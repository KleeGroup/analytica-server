package io.analytica.hcube.query;

import io.analytica.hcube.dimension.HTime;

import java.util.List;

public interface HTimeSelector {
	List<HTime> findTimes(HTimeSelection timeSelection);
}
