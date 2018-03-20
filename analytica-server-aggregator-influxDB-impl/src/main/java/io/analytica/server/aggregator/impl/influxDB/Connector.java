package io.analytica.server.aggregator.impl.influxDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import io.analytica.api.AProcess;
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
 *
 * Linking this library statically or dynamically with other modules is making a combined work based on this library.
 * Thus, the terms and conditions of the GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you permission to link this library
 * with independent modules to produce an executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your choice, provided that you also meet,
 * for each linked independent module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version of the library,
 * but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your version.
 */
import io.analytica.api.Assertion;
import io.analytica.server.aggregator.ProcessAggegatorConstants;
import io.analytica.server.aggregator.ProcessAggregatorDto;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.aggregator.ProcessAggregatorUtil;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBMultipleQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBSingleQuery;
import io.analytica.server.store.Identified;

public final class Connector {

	private final String appName;
	private final List<Identified<AProcess>> processes = new ArrayList<>(); //buffer
	private final InfluxDB influxDB;

	private final int flushMinSize;

	public Connector(final String appName, final InfluxDB influxDB, final int flushMinSize) throws ProcessAggregatorException {
		Assertion.checkArgNotEmpty(appName);
		//-----
		this.appName = appName;
		this.influxDB = influxDB;
		this.flushMinSize = flushMinSize;
		influxDB.createDatabase(appName);
	}

	public synchronized void add(final Identified<AProcess> process) {
		if (influxDB != null) {
			processes.add(process);
		}
	}

	private List<ProcessAggregatorDto> executeSingleQuery(final InfluxDBSingleQuery singleQuery) throws ProcessAggregatorException {
		final QueryResult queryResult = influxDB.query(new Query(singleQuery.getQuery(), appName));
		return Util.parseResults(queryResult, singleQuery.getSelector(), singleQuery.getCategory());
	}

	private List<ProcessAggregatorDto> executeMultipleQuery(final InfluxDBMultipleQuery multipleQuery) throws ProcessAggregatorException {
		final List<List<ProcessAggregatorDto>> results = new ArrayList<>();
		for (final InfluxDBSingleQuery singleQuery : multipleQuery.getSingleQueries()) {
			results.add(executeSingleQuery(singleQuery));
		}
		return Util.aggregateResults(results);
	}

	public List<ProcessAggregatorDto> execute(final InfluxDBQuery dbQuery) throws ProcessAggregatorException {
		final boolean isSingleQuery = dbQuery instanceof InfluxDBSingleQuery;
		final boolean isMultipleQuery = dbQuery instanceof InfluxDBMultipleQuery;
		Assertion.checkArgument(isSingleQuery || isMultipleQuery, "Unable to execute query. Unknown type");
		//-----------------------------------------------------------------------------------------------
		if (isSingleQuery) {
			return executeSingleQuery((InfluxDBSingleQuery) dbQuery);
		}
		return executeMultipleQuery((InfluxDBMultipleQuery) dbQuery);
	}

	public synchronized void flush() {
		if (processes.isEmpty()) {
			return;
		}
		final BatchPoints batchPoints = BatchPoints
				.database(appName)
				.retentionPolicy("default")
				.consistency(ConsistencyLevel.ALL)
				.build();
		for (final Identified<AProcess> process : processes) {
			for (final AProcess flatProcess : ProcessAggregatorUtil.flatProcess(process.getData())) {
				batchPoints.point(processToPoint(flatProcess));
			}
		}
		influxDB.write(batchPoints);
		updateLastInsertedProcess(processes.get(processes.size() - 1));
		processes.clear();
	}

	private void updateLastInsertedProcess(final Identified<AProcess> process) {
		final BatchPoints batchPoints = BatchPoints
				.database(appName)
				.retentionPolicy("default")
				.consistency(ConsistencyLevel.ALL)
				.build();

		final Point lastInsertedProcess = Point.measurement(ProcessAggegatorConstants.LAST_INSERTED_PROCESS)
				.time(0, TimeUnit.MICROSECONDS)
				.field(ProcessAggegatorConstants.LAST_INSERTED_PROCESS, process.getKey())
				.build();
		batchPoints.point(lastInsertedProcess);
		influxDB.write(batchPoints);

	}

	private static Point processToPoint(final AProcess process) {
		final Map measures = process.getMeasures();
		return Point.measurement(process.getType())
				.time(process.getStartDate().getTime(), TimeUnit.MILLISECONDS)
				.tag(InfluxDBQuery.TAG_CATEGORY, process.getCategory())
				.tag(InfluxDBQuery.TAG_LOCATION, process.getLocation())
				.tag(process.getMetaDatas())
				.fields(measures)
				.build();
	}

	//	public List<ProcessAggregatorDto> findAllLocations() throws ProcessAggregatorException{
	//	QueryResult queryResult = influxDB.query(new Query("SHOW TAG VALUES WITH KEY = "+InfluxDBQuery.TAG_LOCATION, appName));
	//	return Util.getResults(queryResult,InfluxDBQuery.NO_CATEGORY);
	//}

	//public List<ProcessAggregatorDto> findAllTypes() throws ProcessAggregatorException{
	//	QueryResult queryResult = influxDB.query(new Query("SHOW MEASUREMENTS", appName));
	//	return Util.getResults(queryResult,InfluxDBQuery.NO_CATEGORY);
	//}

	//	public List<ProcessAggregatorDto> findAllCategories() throws ProcessAggregatorException {
	//		List<ProcessAggregatorDto> categories = new ArrayList<ProcessAggregatorDto>();
	//		List<ProcessAggregatorDto> types = findAllTypes();
	//		for (ProcessAggregatorDto type : types) {
	//			QueryResult queryResult = influxDB.query(new Query("SHOW TAG VALUES FROM "+type.getMeasure(InfluxDBQuery.MEASUREMENT)+" with key="+InfluxDBQuery.TAG_CATEGORY, appName));
	//			categories.addAll(Util.getResults(queryResult,InfluxDBQuery.NO_CATEGORY));
	//		}
	//		return categories;
	//	}

	//	public List<ProcessAggregatorDto> findCategories(String type, String subCategories,String location) throws ProcessAggregatorException {
	//		StringBuilder queryBuilder = new StringBuilder();
	//		queryBuilder.append("SHOW TAG VALUES FROM ").append(Util.getRegexMeasurement(type))
	//		.append(" with key=").append(InfluxDBQuery.TAG_CATEGORY)
	//		.append(" where ").append(InfluxDBQuery.TAG_CATEGORY).append("=").append(Util.getRegexTag(subCategories))
	//		.append(" and ").append(InfluxDBQuery.TAG_LOCATION).append("=").append(Util.getRegexTag(location));
	//		QueryResult queryResult = influxDB.query(new Query(queryBuilder.toString(), appName));
	//		return Util.getResults(queryResult,getCategory(type,subCategories), InfluxDBQuery.TAG_CATEGORY);
	//	}

	//	
	////TODO Create an object for data that will contain the metric, aggregationRule,
	//	public List<ProcessAggregatorDto> getTimeLine(String timeFrom, String timeTo,
	//			String timeDim, String type, String subCategories, String location, Map<String, String> datas) throws ProcessAggregatorException {
	//		StringBuilder queryBuilder = new StringBuilder();
	//		queryBuilder.append("SELECT ");
	//		 final List<Entry<String, String>> entries= new ArrayList<Entry<String, String>>(datas.entrySet());
	//		for (int i=0; i<entries.size();i++){
	//			final String aggretationRule=entries.get(i).getValue();
	//			final String metrics=entries.get(i).getKey();
	//			final String name=metrics+":"+aggretationRule;
	//			queryBuilder.append(aggretationRule).append("(\"").append(metrics).append("\") AS \"").append(name).append("\"");
	//			if(i<entries.size()-1){
	//				queryBuilder.append(",");
	//			}
	//			queryBuilder.append(" ");
	//		}
	//		queryBuilder.append(" FROM ")
	//		.append(Util.getRegexFilterMeasurement(type))
	//		.append(" where ").append(InfluxDBQuery.TAG_CATEGORY).append("=").append(Util.getRegexFilterTag(subCategories))
	//		.append(" and ").append(InfluxDBQuery.TAG_LOCATION).append("=").append(Util.getRegexFilterTag(location))
	//		.append(" and ").append(InfluxDBQuery.TAG_TIME).append(" > ").append(timeFrom)
	//		.append(" and ").append(InfluxDBQuery.TAG_TIME).append(" < ").append(timeTo)
	//		.append(" group by ").append(InfluxDBQuery.TAG_TIME).append("(").append(timeDim).append(")");
	//		QueryResult queryResult = influxDB.query(new Query(queryBuilder.toString(), appName));
	//		return Util.parseResults(queryResult,getCategory(type,subCategories));
	//	}

	//	private String getCategory(final String type, final String subCategories){
	//		if(subCategories==null||subCategories.isEmpty()){
	//			return type;
	//		}
	//		return type+ProcessAggregatorQuery.SEPARATOR+subCategories;
	//	}
}
