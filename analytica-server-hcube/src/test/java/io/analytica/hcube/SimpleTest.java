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
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;
import io.analytica.hcube.impl.HCubeManagerImpl;
import io.analytica.hcube.plugins.store.memory.MemoryHCubeStorePlugin;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HQueryBuilder;
import io.analytica.hcube.result.HResult;

import java.util.Date;

import org.junit.Test;

/**
 * Test.
 * 
 *  - a request ==> page, duration, status 
 * 
 * @author pchretien
 */
public final class SimpleTest {
	private static final String APP_NAME = "MY_APP";
	private final HCubeManager cubeManager = new HCubeManagerImpl(new MemoryHCubeStorePlugin());

	@Test
	public void test() {
		final HTime time = new HTime(new Date(), HTimeDimension.Minute);
		//--------		
		final HMetricKey workingKey = new HMetricKey("working", false);
		final HMetric workingHours = new HMetricBuilder(workingKey)//
				.withValue(17)//
				.build();

		final HCube cube = new HCubeBuilder()//
				.withMetric(workingKey, workingHours)//
				.build();

		cubeManager.push(APP_NAME, new HKey("WORK", time, "www"), cube);

		final HQuery query = new HQueryBuilder()//
				.onType("WORK")//
				.on(HTimeDimension.SixMinutes)//
				.between("NOW-1d", "NOW+1d")//
				//	.whereCategoryEquals(PAGES)//
				.build();

		final HResult result = cubeManager.execute(APP_NAME, query);
		System.out.println(">>> " + result.getSerie(""));
	}
}
