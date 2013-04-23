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
package com.kleegroup.analyticaimpl.hcube;

import java.util.List;

import javax.inject.Inject;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.hcube.HCubeManager;
import com.kleegroup.analytica.hcube.cube.HCube;
import com.kleegroup.analytica.hcube.query.HQuery;
import com.kleegroup.analytica.hcube.result.HResult;

/**
 * @author pchretien, npiedeloup
 * @version $Id: ServerManagerImpl.java,v 1.22 2013/01/14 16:35:19 npiedeloup Exp $
 */
public final class HCubeManagerImpl implements HCubeManager {
	private final ProcessEncoder processEncoder;
	private final CubeStorePlugin cubeStorePlugin;

	/**
	 * Constructeur.
	 * @param cubeStorePlugin Plugin de stockage des Cubes
	 */
	@Inject
	public HCubeManagerImpl(final CubeStorePlugin cubeStorePlugin) {
		Assertion.notNull(cubeStorePlugin);
		//-----------------------------------------------------------------
		this.processEncoder = new ProcessEncoder();
		this.cubeStorePlugin = cubeStorePlugin;
	}

	/** {@inheritDoc} */
	public void push(final KProcess process) {
		List<HCube> cubes = processEncoder.encode(process);
		for (HCube cube : cubes) {
			cubeStorePlugin.merge(cube);
		}
	}

	/** {@inheritDoc} */
	public HResult execute(HQuery query) {
		return new HResult(query, cubeStorePlugin.findAll(query));
	}
}
