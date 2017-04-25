package io.vertigo.analytics.vm;

import java.util.Optional;

import io.vertigo.app.App;
import io.vertigo.app.AutoCloseableApp;

public class FeederStart {

	private static App app;

	public static void main(final String[] args) {
		switch (args.length) {
			case 0:
				app = new AutoCloseableApp(MyAppConfig.config(Optional.empty()));
				break;
			case 1:
				app = new AutoCloseableApp(MyAppConfig.config(Optional.of(args[0])));
				break;
			default:
				throw new RuntimeException("Only one optional parameter is possible (properties config file)");
		}

	}

}
