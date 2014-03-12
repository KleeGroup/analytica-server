/**
 * Kasper-kernel - v6 - Simple Java Framework
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
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
package io.analytica;

import io.vertigo.kernel.Home;
import io.vertigo.kernel.component.Container;
import io.vertigo.kernel.di.injector.Injector;
import io.vertigo.kernel.lang.Option;

import java.util.Properties;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Charge l'environnement de test par defaut.
 * @author pchretien
 */
public abstract class AbstractTestCaseJU4Rule {

	@Rule
	public ContainerResource resource = new ContainerResource(this);

	/**
	 * @return fichier managers.xml (par defaut managers-test.xml)
	 */
	protected String getManagersXmlFileName() {
		return "./managers-test.xml";
	}

	/**
	 * @return fichier properties de paramétrage des managers (par defaut Option.none())
	 */
	protected Option<String> getPropertiesFileName() {
		return Option.none(); //par défaut pas de properties
	}

	/**
	 * JUnitRule représentant la resource de Container.
	 * @author npiedeloup
	 * @version $Id: $
	 */
	static class ContainerResource implements TestRule {

		private final AbstractTestCaseJU4Rule testCaseInstance;

		/**
		 * Constructeur.
		 * @param testCaseInstance instance du testCase
		 */
		ContainerResource(final AbstractTestCaseJU4Rule testCaseInstance) {
			this.testCaseInstance = testCaseInstance;
		}

		/** {@inheritDoc} */
		@Override
		public Statement apply(final Statement base, final Description description) {
			return new ContainerStatement(base, testCaseInstance);
		}

		/**
		 * Statement de la resource ContainerResource.
		 * @author npiedeloup
		 * @version $Id: $
		 */
		static class ContainerStatement extends Statement {
			private final AbstractTestCaseJU4Rule testCaseInstance;
			private final Injector injector = new Injector();
			private Starter starter;

			private final Statement base;

			/**
			 * @param base evaluation du test
			 * @param testCaseInstance instance du testCase
			 */
			public ContainerStatement(final Statement base, final AbstractTestCaseJU4Rule testCaseInstance) {
				this.base = base;
				this.testCaseInstance = testCaseInstance;
			}

			/** {@inheritDoc} */
			@Override
			public void evaluate() throws Throwable {
				starter = new Starter(testCaseInstance.getManagersXmlFileName(), testCaseInstance.getPropertiesFileName(), testCaseInstance.getClass(), Option.<Properties> none(), 0L);
				starter.start();

				//On injecte les managers sur la classe de test.
				injector.injectMembers(testCaseInstance, getContainer());
				try {
					base.evaluate();
				} finally {
					starter.stop();
				}
			}

			/**
			 * Fournit le container utilisé pour l'injection.
			 * @return Container de l'injection
			 */
			private Container getContainer() {
				return Home.getComponentSpace();
			}
		}
	}
}
