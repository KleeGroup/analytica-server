package io.vertigo.analytics.vm.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult.Result;

import io.vertigo.analytics.vm.VmInfos;
import io.vertigo.app.Home;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.task.TaskManager;
import io.vertigo.dynamo.task.metamodel.TaskDefinition;
import io.vertigo.dynamo.task.model.Task;
import io.vertigo.dynamo.task.model.TaskBuilder;
import io.vertigo.dynamo.task.model.TaskResult;
import io.vertigo.dynamo.transaction.VTransactionManager;
import io.vertigo.dynamo.transaction.VTransactionWritable;
import io.vertigo.lang.Activeable;
import io.vertigo.lang.Assertion;

public class InfluxDbFeedServicesImpl implements InfluxDbFeedServices, Activeable {

	private static final String CPU_PERCENT = "cpu_percent";
	private static final String MEMORY_PERCENT = "memory_percent";
	private static final String VM_NAME = "vm_name";

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private Optional<Date> lastPoll = Optional.empty();

	private final InfluxDB influxDB;
	private final String dbName;
	private final TaskManager taskManager;
	private final VTransactionManager transactionManager;

	@Inject
	public InfluxDbFeedServicesImpl(
			@Named("serverUrl") final String serverUrl,
			@Named("login") final String login,
			@Named("password") final String password,
			@Named("dbName") final String dbName,
			final TaskManager taskManager,
			final VTransactionManager transactionManager) {
		Assertion.checkNotNull(taskManager);
		Assertion.checkNotNull(transactionManager);
		//---
		Assertion.checkArgNotEmpty(serverUrl);
		Assertion.checkArgNotEmpty(login);
		Assertion.checkArgNotEmpty(password);
		Assertion.checkArgNotEmpty(dbName);
		// ---
		this.taskManager = taskManager;
		this.transactionManager = transactionManager;
		//---
		influxDB = InfluxDBFactory.connect(serverUrl, login, password);
		this.dbName = dbName;
		if (!influxDB.describeDatabases().contains(dbName)) {
			influxDB.createDatabase(dbName);
		}
	}

	@Override
	public void start() {
		executorService.scheduleAtFixedRate(() -> feedInfluxDb(lastPoll), 0, 3, TimeUnit.MINUTES);

	}

	@Override
	public void stop() {
		executorService.shutdownNow();

	}

	@Override
	public void feedInfluxDb(final Optional<Date> previousDate) {
		// we need to retrieve in influx db the last element
		final Date lastDateToUse = previousDate.orElseGet(this::retrieveLastPollInfluxDb);

		final Task task = new TaskBuilder(Home.getApp().getDefinitionSpace().resolve("TK_GET_VM_INFOS", TaskDefinition.class))
				.addValue("LAST_TIMESTAMP", lastDateToUse)
				.build();

		final TaskResult taskResult;
		try (VTransactionWritable transactionWritable = transactionManager.createCurrentTransaction()) {
			taskResult = taskManager.execute(task);
		}

		final DtList<VmInfos> result = taskResult.<DtList<VmInfos>> getResult();

		if (!result.isEmpty()) {

			influxDB.enableBatch(1000, 1, TimeUnit.MINUTES);
			//---
			final VmInfos last = result.get(result.size() - 1);
			lastPoll = Optional.of(last.getTimestamp());
			result
					.stream()
					.filter(vmInfos -> {
						return vmInfos.getTimestamp().after(last.getTimestamp()) || vmInfos.getTimestamp().equals(last.getTimestamp());
					})
					.forEach(vmInfos -> insertIntoInfluxdb(vmInfos));
			//---
			//we flush at each iteration
			influxDB.disableBatch();
		}
	}

	private Date retrieveLastPollInfluxDb() {

		final Query query = new Query("select last(\"cpu_percent\"), time from usage ", dbName);
		final List<Result> results = influxDB.query(query).getResults();
		if (!results.isEmpty()) {
			final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			try {
				return dateFormat.parse((String) results.get(0).getSeries().get(0).getValues().get(0).get(0));
			} catch (final Exception e) {
				// we do nothing
			}
		}
		final Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(ZonedDateTime.of(LocalDateTime.now(ZoneOffset.UTC), ZoneId.of("Europe/Paris")).toInstant().toEpochMilli());
		cal.add(Calendar.HOUR, -1);
		return cal.getTime();

	}

	private void insertIntoInfluxdb(final VmInfos vmInfos) {

		final long timeInMillis = ZonedDateTime.of(LocalDateTime.ofInstant(Instant.ofEpochMilli(vmInfos.getTimestamp().getTime()), ZoneId.of("Europe/Paris")), ZoneOffset.UTC).toInstant().toEpochMilli();
		final Point point = Point.measurement("usage")
				.time(timeInMillis, TimeUnit.MILLISECONDS)
				// fields
				.addField(CPU_PERCENT, vmInfos.getCpu())
				.addField(MEMORY_PERCENT, vmInfos.getMemory())
				.addField(VM_NAME, vmInfos.getVmName())
				// tags
				.tag(VM_NAME, vmInfos.getVmName())
				.tag(CPU_PERCENT, vmInfos.getCpu().toString())
				.tag(MEMORY_PERCENT, vmInfos.getMemory().toString())
				.build();
		influxDB.write(dbName, "autogen", point);
	}
}
