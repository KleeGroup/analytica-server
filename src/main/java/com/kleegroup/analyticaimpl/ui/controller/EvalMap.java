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
package com.kleegroup.analyticaimpl.ui.controller;

import java.util.HashMap;

import kasper.kernel.util.Assertion;

final class EvalMap<IN, OUT> extends HashMap<IN, OUT> {

	private final Function<IN, OUT> function;
	private final Class<IN> inClazz;

	/**
	 * Constructeur.
	 * @param function Fonction d'évaluation
	 * @param inClazz Class du paramètre en entrée
	 */
	EvalMap(final Function<IN, OUT> function, final Class<IN> inClazz) {
		Assertion.notNull(function);
		Assertion.notNull(inClazz);
		//-----------------------------------------------------------------
		this.function = function;
		this.inClazz = inClazz;
	}

	/** {@inheritDoc} */
	@Override
	public OUT get(final Object bean) {
		Assertion.notNull(bean);
		Assertion.precondition(inClazz.isInstance(bean), "l'objet {0} doit être de la class {1}.", bean.getClass().getSimpleName(), inClazz.getSimpleName());
		// ---------------------------------------------------------------------
		return function.apply((IN) bean);
	}
}
