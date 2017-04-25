/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2017, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiere - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.analytics.vm;

import java.util.Optional;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import io.vertigo.analytics.vm.services.InfluxDbFeedServices;
import io.vertigo.analytics.vm.services.InfluxDbFeedServicesImpl;
import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.app.config.DefinitionProviderConfigBuilder;
import io.vertigo.app.config.ModuleConfigBuilder;
import io.vertigo.commons.impl.CommonsFeatures;
import io.vertigo.commons.plugins.cache.memory.MemoryCachePlugin;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.param.properties.PropertiesParamPlugin;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.core.plugins.resource.url.URLResourceResolverPlugin;
import io.vertigo.dynamo.impl.DynamoFeatures;
import io.vertigo.dynamo.impl.database.vendor.sqlserver.SqlServerDataBase;
import io.vertigo.dynamo.plugins.database.connection.c3p0.C3p0ConnectionProviderPlugin;
import io.vertigo.dynamo.plugins.environment.DynamoDefinitionProvider;
import io.vertigo.dynamo.plugins.store.datastore.sql.SqlDataStorePlugin;

public final class MyAppConfig {
	public static final int WS_PORT = 8088;

	public static AppConfigBuilder createAppConfigBuilder(final Optional<String> propertiesUrlOpt) {
		return new AppConfigBuilder().beginBoot()
				.withLocales("fr_FR")
				.addPlugin(ClassPathResourceResolverPlugin.class)
				.addPlugin(URLResourceResolverPlugin.class)
				.addPlugin(PropertiesParamPlugin.class,
						Param.create("url", propertiesUrlOpt.orElse("config.properties")))
				.endBoot()
				.addModule(new CommonsFeatures()
						.withCache(MemoryCachePlugin.class)
						.withScript()
						.build())
				.addModule(new DynamoFeatures()
						.withStore()
						.addDataStorePlugin(SqlDataStorePlugin.class)
						.withSqlDataBase()
						.addSqlConnectionProviderPlugin(C3p0ConnectionProviderPlugin.class,
								Param.create("dataBaseClass", SqlServerDataBase.class.getName()),
								Param.create("jdbcDriver", SQLServerDriver.class.getName()),
								Param.create("jdbcUrl", "${database.url}"))
						.build())
				.addModule(new ModuleConfigBuilder("mine")
						.addComponent(InfluxDbFeedServices.class, InfluxDbFeedServicesImpl.class,
								Param.create("serverUrl", "${influxdb.serverUrl}"),
								Param.create("login", "${influxdb.login}"),
								Param.create("password", "${influxdb.password}"),
								Param.create("dbName", "${influxdb.dbName}"))
						.addDefinitionProvider(new DefinitionProviderConfigBuilder(DynamoDefinitionProvider.class)
								.addDefinitionResource("kpr", "io/vertigo/analytics/vm/definitions.kpr")
								.build())
						.build());
	}

	public static AppConfig config(final Optional<String> propertiesUrlOpt) {
		// @formatter:off
		return createAppConfigBuilder(propertiesUrlOpt).build();
	}







}
