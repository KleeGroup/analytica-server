/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses>
 */
package io.analytica.hcube;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.cube.HMetricBuilder;
import io.analytica.hcube.cube.HMetricDefinition;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;
import io.analytica.hcube.impl.HCubeManagerImpl;
import io.analytica.hcube.plugins.store.memory.MemoryHCubeStorePlugin;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HQueryBuilder;
import io.analytica.hcube.result.HResult;
import io.vertigo.core.Home;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test.
 *
 *  - a request ==> page, duration, status
 *
 * @author pchretien
 */
public final class SimpleTest {
	private static final String APP_NAME = "MY_FANCY_APP";
	private static final String HM_PERF = "HM_PERF";
	private final HCubeManager cubeManager = new HCubeManagerImpl(new MemoryHCubeStorePlugin());
	private final HApp app = cubeManager.getApp(APP_NAME);

	@Before
	public void before() {
		final HMetricDefinition perf = new HMetricDefinition(HM_PERF, true);
		cubeManager.register(perf);
	}

	@After
	public void after() {
		Home.getDefinitionSpace().stop();
	}

	@Test
	public void test() {
		final HTime time = new HTime(new Date(), HTimeDimension.Minute);
		//--------
		final HMetric workingHours = new HMetricBuilder(HM_PERF)
				.withValue(10)
				.withValue(17)
				.build();

		final HCube cube = new HCubeBuilder()
				.withMetric(workingHours)
				.build();

		app.push(new HKey("PAGES", time, "www"), cube);

		final HQuery query = new HQueryBuilder()
				.onType("PAGES")
				.on(HTimeDimension.SixMinutes)
				.between("NOW", "NOW")
				//	.whereCategoryEquals(PAGES)
				.build();

		final HResult result = app.execute(query);
		System.out.println(">>> " + result.getSerie(""));
	}
}
