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
package io.analytica;

import java.util.Optional;
import java.util.Properties;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.vertigo.app.Home;
import io.vertigo.core.component.di.injector.DIInjector;
import io.vertigo.lang.Container;

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
	protected Optional<String> getPropertiesFileName() {
		return Optional.empty(); //par défaut pas de properties
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
				DIInjector.injectMembers(testCaseInstance, getContainer());
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
				return Home.getApp().getComponentSpace();
			}
		}
	}
}
