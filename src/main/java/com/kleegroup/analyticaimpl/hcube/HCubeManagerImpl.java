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

import vertigo.kernel.lang.Assertion;
import vertigo.kernel.lang.Option;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.hcube.HCategoryDictionary;
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
	private final HCategoryDictionary categoryDictionary = new HCategoryDictionaryImpl();
	private final Option<ProcessStatsPlugin> processStatsPlugin;

	/**
	 * Constructeur.
	 * @param cubeStorePlugin Plugin de stockage des Cubes
	 * @param processStatsPlugin Plugin de statistique des process
	 */
	@Inject
	public HCubeManagerImpl(final CubeStorePlugin cubeStorePlugin, final Option<ProcessStatsPlugin> processStatsPlugin) {
		Assertion.checkNotNull(cubeStorePlugin);
		Assertion.checkNotNull(processStatsPlugin);
		//-----------------------------------------------------------------
		processEncoder = new ProcessEncoder();
		this.cubeStorePlugin = cubeStorePlugin;
		//this.processStatsPlugin = Option.option(processStatsPlugin);
		this.processStatsPlugin = processStatsPlugin;
	}

	/** {@inheritDoc} */
	public void push(final KProcess process) {
		final List<HCube> cubes = processEncoder.encode(process);
		if (processStatsPlugin.isDefined()) {
			processStatsPlugin.get().merge(process);
		}
		//---Alimentation du dictionnaire des catégories puis des cubes
		for (final HCube cube : cubes) {
			categoryDictionary.add(cube.getKey().getCategory());
			cubeStorePlugin.merge(cube);
		}
	}

	/** {@inheritDoc} */
	public HResult execute(final HQuery query) {
		return new HResult(query, query.getAllCategories(categoryDictionary), cubeStorePlugin.findAll(query, categoryDictionary));
	}

	/**
	 * @return the cubeStorePlugin
	 */
	public CubeStorePlugin getCubeStorePlugin() {
		return cubeStorePlugin;
	}

	/** {@inheritDoc} */
	public HCategoryDictionary getCategoryDictionary() {
		return categoryDictionary;
	}
}
