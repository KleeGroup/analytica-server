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
package com.kleegroup.analytica.server.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kleegroup.analytica.hcube.cube.DataKey;

import kasper.kernel.util.Assertion;

/**
 * @author npiedeloup
 * @version $Id: DataSet.java,v 1.6 2012/04/17 09:15:26 pchretien Exp $
 */
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 
//COMMENTAIRES 

public final class DataSet<X, Y> {
	private final DataKey key;
	private final List<X> labels;
	private final List<Y> values;

	public DataSet(final DataKey key, final List<X> labels, final List<Y> values) {
		Assertion.notNull(key);
		Assertion.notNull(labels);
		Assertion.precondition(values.size() == labels.size(), "Le nombre de label ({0}) doit correspondre au nombre de valeur ({1})", labels.size(), values.size());
		//---------------------------------------------------------------------
		this.key = key;
		this.labels = Collections.unmodifiableList(new ArrayList<X>(labels));
		this.values = Collections.unmodifiableList(new ArrayList<Y>(values));
	}

	public DataKey getKey() {
		return key;
	}

	public List<X> getLabels() {
		return labels;
	}

	public List<Y> getValues() {
		return values;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(key).append("= {");
		for (int i = 0; i < labels.size(); i++) {
			if (i % 10 == 0) {
				sb.append("\n  ");
			}
			sb.append(" ").append(labels.get(i)).append("=").append(values.get(i)).append(",");
		}
		sb.append("}");
		return sb.toString();
	}
}
