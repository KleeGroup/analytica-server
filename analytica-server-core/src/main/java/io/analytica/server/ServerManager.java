/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiére - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica.server;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;

import io.analytica.api.KProcess;
import io.analytica.server.aggregator.ProcessAggregatorDto;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.ProcessAggregatorResult;
import io.vertigo.lang.Component;

/**
 * Serveur de Analytica.
 * Réception des données collectées
 * 
 * @author pchretien, npiedeloup
 * @version $Id: ServerManager.java,v 1.8 2012/09/14 15:04:13 pchretien Exp $
 */
public interface ServerManager extends Component {
	/**
	 * Add a process.
	 * @param process Process to push
	 */
	void push(KProcess process);

	List<ProcessAggregatorDto> findAllLocations(final String appName) throws ProcessAggregatorException;
	
	List<ProcessAggregatorDto> findAllTypes(final String appName) throws ProcessAggregatorException;
	
	List<ProcessAggregatorDto> findAllCategories(final String appName)throws ProcessAggregatorException;

	List<ProcessAggregatorDto> findCategories(String appName, String type,String subCategories, String location) throws ProcessAggregatorException;

	List<ProcessAggregatorDto> getTimeLine(String appName, String timeFrom, String timeTo, String timeDim, String type, String subCategories, String location, Map<String, String> datas)throws ProcessAggregatorException;
}
