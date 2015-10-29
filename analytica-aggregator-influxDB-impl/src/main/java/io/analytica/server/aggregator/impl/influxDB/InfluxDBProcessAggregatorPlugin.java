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

import io.analytica.api.KProcess;
import io.analytica.server.aggregator.ProcessAggregatorException;
import io.analytica.server.aggregator.ProcessAggregatorPlugin;
import io.analytica.server.aggregator.ProcessAggregatorQuery;
import io.analytica.server.aggregator.ProcessAggregatorResult;
import io.analytica.server.store.Identified;

public class InfluxDBProcessAggregatorPlugin implements ProcessAggregatorPlugin {

	private final Map<String,InfluxDBProcessAggregatorConnector> connectors = new HashMap<>();
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
	public void push(Identified<KProcess> process) {
		try {
			getConnector(process.getData().getAppName()).add(process);
		} catch (ProcessAggregatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getLastInsertedProcess(String appName) {
		try {
			return getConnector(appName).getLastInsertedProcess();
		} catch (ProcessAggregatorException e) {
			return null;
		}
	}

	@Override
	public ProcessAggregatorResult execute(ProcessAggregatorQuery aggregatorQuery) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private InfluxDBProcessAggregatorConnector getConnector(String applicationName) throws ProcessAggregatorException{
		if(!connectors.containsKey(applicationName)){
			connectors.put(applicationName, new InfluxDBProcessAggregatorConnector(applicationName,influxDB,flushMinSize));
		}
		return connectors.get(applicationName);
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
	
	private static boolean ping(final String host) {
		try {
			final InetAddress inet = InetAddress.getByName(host);
			return inet.getAddress() != null;
		} catch (final IOException e) {
			return false;
		}
	}
}
