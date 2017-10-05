/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2016, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
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
package io.vertigo.analytics.server;

import java.util.Objects;

/**
 * Assertions have been introduced by  B.Meyer with a language called Eiffel.
 *
 * An assertion allows you to design by contract.
 * Each time an assetion fails, an specific exception is thrown.
 * - checkNotNull     throws NullPointerException
 * - checkArgument    throws IllegalArgumentException
 * - checkArgNotEmpty throws IllegalArgumentException
 * - checkState       throws IllegalStateException
 *
 * Assertion can define a message and args.
 * "hello {0}, an error occured on '{1}'", "foo", "bar"
 *  hello foo, an error occured on 'bar'
 *
 * You can use ' inside the message.
 *
 *
 * @author fconstantin
 */
public final class Assertion {
	private Assertion() {
		//private constructor
	}

	/**
	 * Check if an object is not null.
	 * If not a generic exception is thrown.
	 * @param o Object object  that must be not null
	 */
	public static void checkNotNull(final Object o) {
		Objects.requireNonNull(o);
	}

	/**
	 * Check if an object is not null.
	 * If not an exception with a contextual message is thrown.
	 *
	 * @param o Object object that must be not null
	 * @param msg Error message
	 */
	public static void checkNotNull(final Object o, final String msg) {
		//Attention si o est un Boolean : il peut s'agir du resultat d'un test (boolean) qui a été autoboxé en Boolean
		if (o == null) {
			throw new NullPointerException(msg);
		}
	}

}
