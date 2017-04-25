package io.vertigo.analytics.vm.services;

import java.util.Date;
import java.util.Optional;

import io.vertigo.lang.Component;

public interface InfluxDbFeedServices extends Component {

	void feedInfluxDb(Optional<Date> previousDate);

}
