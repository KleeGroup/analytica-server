package com.kleegroup.analytica.hcube.trash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.query.TimeSelection;

public class Snippet {
	private static final long HOUR_TIME_MILLIS = 60 * 60 * 1000;

	/**
	 * @param timeSelection TimeSelection parent
	 * @return Liste des TimeSelections sous jacentes
	 */
	/** {@inheritDoc} */
	public List<TimeSelection> getSubTimeSelections(final TimeSelection timeSelection) {
		final List<TimeSelection> timeSelections;
		final TimeDimension subTimeDimension;
		//final TimeDimension subTimeDimension = getSubTimeDimension(timeSelection.getDimension());
		switch (timeSelection.getDimension()) {
			case Year:
				subTimeDimension = TimeDimension.Month;
				timeSelections = createSubTimeSelection(timeSelection, subTimeDimension, 3L * 31 * 24 * HOUR_TIME_MILLIS);
				break;
			case Month:
				subTimeDimension = TimeDimension.Day;
				timeSelections = createSubTimeSelection(timeSelection, subTimeDimension, 7L * 24 * HOUR_TIME_MILLIS);
				break;
			case Day:
				subTimeDimension = TimeDimension.Hour;
				timeSelections = createSubTimeSelection(timeSelection, subTimeDimension, 24L * HOUR_TIME_MILLIS);
				break;
			case Hour:
				subTimeDimension = TimeDimension.Minute;
				timeSelections = createSubTimeSelection(timeSelection, subTimeDimension, 6L * HOUR_TIME_MILLIS);
				break;
			case Minute:
				timeSelections = Collections.emptyList();
				break;
			default:
				throw new IllegalArgumentException("TimeDimension inconnue : " + timeSelection.getDimension());
		}
		return timeSelections;
	}

	private List<TimeSelection> createSubTimeSelection(final TimeSelection timeSelection, final TimeDimension subTimeDimension, final long timeStepMillis) {
		final List<TimeSelection> result = new ArrayList<TimeSelection>();
		Date currentMaxDate;
		for (Date currentMinDate = timeSelection.getMinValue(); currentMinDate.before(timeSelection.getMaxValue()); currentMinDate = currentMaxDate) {
			currentMaxDate = new Date(currentMinDate.getTime() + timeStepMillis);
			final TimeSelection newTimeSelection = new TimeSelection(subTimeDimension, currentMinDate, currentMaxDate);
			result.add(newTimeSelection);
		}
		return result;
	}

	/**
	 * @param query  Requête précisant les selections
	 * @return Liste des WhatSelection sous jacentes sur une durée donnée
	 */
	//	public List<WhatSelection> getSubWhatSelections(final Query query) {
	//		final List<WhatPosition> subWhatPositions = cubeStorePlugin.loadSubWhatPositions(query);
	//		final List<WhatSelection> result = new ArrayList<WhatSelection>();
	//		for (final WhatPosition subWhatPosition : subWhatPositions) {
	//			result.add(new WhatSelection(subWhatPosition.getDimension(), subWhatPosition.getValue()));
	//		}
	//		return result;
	//	}

	/**
	 * @param query  Requête précisant les selections
	* @return Liste des DataKey possibles
	*/
	//	public List<DataKey> getSubDataKeys(final Query query) {
	//		return cubeStorePlugin.loadDataKeys(query);
	//	}

}
