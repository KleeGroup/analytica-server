package io.analytica.server.aggregator.impl.influxDB;

/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidi�re - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
import io.analytica.api.KProcess;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.store.Identified;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.jmx.Agent;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

public final class InfluxDBProcessAggregatorConnector {
	private final String appName;
	private final List<Identified<KProcess>> processes = new ArrayList<Identified<KProcess>>(); //buffer
	private final InfluxDB influxDB;
	private final String LAST_INSERTED_PROCESS = "lastInsertedProcess";
	private final int flushMinSize;


	public InfluxDBProcessAggregatorConnector(final String appName,final InfluxDB influxDB, final int flushMinSize ) throws ProcessAggregatorException {
		Assertion.checkArgNotEmpty(appName);
		//-----
		this.appName = appName;
		this.influxDB = influxDB;
		this.flushMinSize=flushMinSize;
		influxDB.createDatabase(appName);
		
		
	}
	public String getLastInsertedProcess() throws ProcessAggregatorException{
		QueryResult queryResult = influxDB.query(new Query("SELECT "+LAST_INSERTED_PROCESS +" FROM "+LAST_INSERTED_PROCESS, appName));
		List<Result> results = queryResult.getResults();
		if (results.isEmpty() || (results.size()==1 && (results.get(0).getSeries()==null || results.get(0).getSeries().isEmpty())))
		{ 
			return null;
		}
		Assertion.checkArgument(results.size()==1 , "");
		List<Series> series = results.get(0).getSeries();
		for (Series serie : series) {
			if (serie.getValues()!=null){
				return (String) serie.getValues().get(0).get(1);
			}
		}
		throw new ProcessAggregatorException("InfluxDB error. Unable to identify the last inserted process");
	}
	
	public synchronized void add(final Identified<KProcess> process) {
		if (influxDB != null) {
			processes.add(process);
			flush();
//			if (processes.size() > flushMinSize) {
//				flush();
//			}
		}
	}
	
	private void flush() {
		final BatchPoints batchPoints = BatchPoints
				.database(appName)
				.retentionPolicy("default")
				.consistency(ConsistencyLevel.ALL)
				.build();
		for (final Identified<KProcess> process : processes) {
			batchPoints.point(processToPoint(process.getData()));
			for (final KProcess subProcess : process.getData().getSubProcesses()) {
				batchPoints.point(processToPoint(subProcess));
			}
		}
		influxDB.write(batchPoints);
		updateLastInsertedProcess(processes.get(processes.size()-1));
		processes.clear();
	}

	private void updateLastInsertedProcess(final Identified<KProcess> process){
		final BatchPoints batchPoints = BatchPoints
				.database(appName)
				.retentionPolicy("default")
				.consistency(ConsistencyLevel.ALL)
				.build();
	
			Point lastInsertedProcess = Point.measurement(LAST_INSERTED_PROCESS)
					.time(0,TimeUnit.MICROSECONDS)
					.field(LAST_INSERTED_PROCESS, process.getKey())
					.build();
			batchPoints.point(lastInsertedProcess);
			influxDB.write(batchPoints);
		
	}
	
	
	
	
	private static Point processToPoint(final KProcess process) {
		final Map measures = process.getMeasures();
		return Point.measurement(process.getType())
				.time(process.getStartDate().getTime(), TimeUnit.MILLISECONDS)
				.tag("category", process.getCategory())
				.tag("location", process.getLocation())
				.tag(process.getMetaDatas())
				.fields(measures)
				.build();
	}

}
