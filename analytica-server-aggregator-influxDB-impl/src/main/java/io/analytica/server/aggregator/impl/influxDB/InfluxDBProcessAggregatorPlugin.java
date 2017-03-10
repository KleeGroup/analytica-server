package io.analytica.server.aggregator.impl.influxDB;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

import io.analytica.api.Assertion;
import io.analytica.api.KProcess;
import io.analytica.server.aggregator.ProcessAggegatorConstants;
import io.analytica.server.aggregator.ProcessAggregatorDto;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.aggregator.ProcessAggregatorPlugin;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBDataQuery;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQueryFactory;
import io.analytica.server.aggregator.impl.influxDB.query.InfluxDBQuery;
import io.analytica.server.aggregator.impl.influxDB.query.impl.InfluxDBMeasurementsQuery;
import io.analytica.server.aggregator.impl.influxDB.query.impl.InfluxDBSimpleDataQuery;
import io.analytica.server.aggregator.impl.influxDB.query.impl.InfluxDBTagQuery;
import io.analytica.server.store.Identified;

public class InfluxDBProcessAggregatorPlugin implements ProcessAggregatorPlugin {

	private final Map<String,Connector> connectors = new HashMap<>();
	private final String httpAddresse;
	private final String port;
	private final String username;
	private final String password;
	private final int flushMinSize;
	private InfluxDB influxDB;
	
	@Inject
	public InfluxDBProcessAggregatorPlugin(@Named("httpAddresse")final String httpAddresse, @Named("port")final String port, @Named("username")final String username,@Named("password") final String password,@Named("flushMinSize") final int flushMinSize) {
		
		this.httpAddresse=httpAddresse;
		this.password=password;
		this.port=port;
		this.username=username;
		this.flushMinSize=flushMinSize;
		try {
			influxDB = getDB(httpAddresse, port, username, password);
			List<String> databases = influxDB.describeDatabases();
			for (String database : databases) {
				getConnector(database);
			}
		} catch (ProcessAggregatorException e) {
			influxDB =null;
			
		}
	}
	
	@Override
	public void push(Identified<KProcess> process) throws ProcessAggregatorException {
			getConnector(process.getData().getAppName()).add(process);
	}

	@Override
	public String getLastInsertedProcess(final ProcessAggregatorQuery aggregatorQuery) {
		try {
			InfluxDBSimpleDataQuery dataQuery = new InfluxDBSimpleDataQuery(aggregatorQuery);
			Connector connector = getConnector(aggregatorQuery.getAggregatorDataFilter().getApplicationName());
			connector.flush();
			List<ProcessAggregatorDto> results=connector.execute(dataQuery);
			boolean databaseIsEmpty = results.isEmpty();
			if(databaseIsEmpty){
				return null;
			}
			Assertion.checkArgument(results.size()==1 , "InfluxDB error. Unable to identify the last inserted process");
			return results.get(0).getMeasure(ProcessAggegatorConstants.LAST_INSERTED_PROCESS);
		} catch (ProcessAggregatorException e) {
			return null;
		}
	}

	@Override
	public List<ProcessAggregatorDto> findAllLocations(final ProcessAggregatorQuery aggregatorQuery) throws ProcessAggregatorException {
		InfluxDBTagQuery tagQuery = new InfluxDBTagQuery(aggregatorQuery, InfluxDBQuery.TAG_LOCATION,aggregatorQuery.getAggregatorDataSelector());
		return getConnector(aggregatorQuery.getAggregatorDataFilter().getApplicationName()).execute(tagQuery);
	}

	@Override
	public List<ProcessAggregatorDto> findAllTypes(final ProcessAggregatorQuery aggregatorQuery) throws ProcessAggregatorException{
		InfluxDBMeasurementsQuery measurementsQuery = new InfluxDBMeasurementsQuery(aggregatorQuery.getAggregatorDataSelector());
		return getConnector(aggregatorQuery.getAggregatorDataFilter().getApplicationName()).execute(measurementsQuery);
	}

	@Override
	public List<ProcessAggregatorDto> findAllCategories(final ProcessAggregatorQuery aggregatorQuery)
			throws ProcessAggregatorException {
		InfluxDBTagQuery tagQuery = new InfluxDBTagQuery(aggregatorQuery, InfluxDBQuery.TAG_CATEGORY,aggregatorQuery.getAggregatorDataSelector());
		return getConnector(aggregatorQuery.getAggregatorDataFilter().getApplicationName()).execute(tagQuery);
	}

	@Override
	public List<ProcessAggregatorDto> findCategories(final ProcessAggregatorQuery aggregatorQuery)
			throws ProcessAggregatorException {
		InfluxDBTagQuery tagQuery = new InfluxDBTagQuery(aggregatorQuery, InfluxDBQuery.TAG_CATEGORY,aggregatorQuery.getAggregatorDataSelector());
		return getConnector(aggregatorQuery.getAggregatorDataFilter().getApplicationName()).execute(tagQuery);
	}

	@Override
	public List<ProcessAggregatorDto> getTimeLine(final ProcessAggregatorQuery aggregatorQuery) throws ProcessAggregatorException {
		InfluxDBDataQuery dataQuery = InfluxDBQueryFactory.getDataQuery(aggregatorQuery);
		return getConnector(aggregatorQuery.getAggregatorDataFilter().getApplicationName()).execute(dataQuery);
		//	return getConnector(aggregatorQuery.getAggregatorDataFilter().getApplicationName()).getTimeLine(timeFrom,timeTo,timeDim,type,subCategories,location,datas);
	}
	
	public static InfluxDB getDB(final String httpAddresse, final String port, final String username, final String password) throws ProcessAggregatorException{
		final String httpCompleteAdresse="http://"+httpAddresse+":"+port;
		if (ping(httpAddresse)){
			InfluxDB influxDB= InfluxDBFactory.connect(httpCompleteAdresse, username, password);
			influxDB.enableBatch(2000, 5000, TimeUnit.MILLISECONDS);
			return influxDB;
		}
		throw new ProcessAggregatorException("Unable to connect to InfluDB at "+httpCompleteAdresse+" with the username "+username +" and the password "+password);
	}
	
	
	private Connector getConnector(String applicationName) throws ProcessAggregatorException{
		if(!connectors.containsKey(applicationName)){
			connectors.put(applicationName, new Connector(applicationName,influxDB,flushMinSize));
		}
		return connectors.get(applicationName);
	}
	
	private static boolean ping(final String host) {
		try {
			final InetAddress inet = InetAddress.getByName(host);
			return inet.getAddress() != null;
		} catch (final IOException e) {
			return false;
		}
	}
}
