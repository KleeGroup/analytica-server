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
package io.analytica.server.plugins.processstore.berkeley;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

import io.analytica.api.AProcess;
import io.analytica.api.KProcessBuilder;
import io.vertigo.lang.Assertion;

/**
 * Classe qui pour un DtObject permet de lire/écrire un tuple.
 * Le binding est indépendant de la DtDefinition.
 *
 * @author pchretien
 * @version $Id: AProcessBinding.java,v 1.9 2012/11/08 17:07:40 pchretien Exp $
 */
final class AProcessBinding extends TupleBinding {
	private static final String PROCESS_BINDING_PREFIX = "ProcessBinding";
	private static final String PROCESS_BINDING_V1 = PROCESS_BINDING_PREFIX + "V1";

	/** {@inheritDoc} */
	@Override
	public Object entryToObject(final TupleInput ti) {
		try {
			//			/*final UUID uuid =*/UUID.fromString(ti.readString());
			final String version = detectVersion(ti);
			ti.reset();
			if (PROCESS_BINDING_V1.equals(version)) {
				return doEntryToProcessV1(ti);
			} else {
				throw new java.lang.IllegalArgumentException("Version d'encodage de process non reconnu : " + version);
			}

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String detectVersion(final TupleInput ti) {
		final String version = ti.readString();
		if (version.startsWith(PROCESS_BINDING_PREFIX)) {
			return version;
		}
		return PROCESS_BINDING_V1;//La v1 ne précisait pas sa version on la déduit
	}

	/** {@inheritDoc} */
	@Override
	public void objectToEntry(final Object object, final TupleOutput to) {
		try {
			doProcessToEntry((AProcess) object, to);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private AProcess doEntryToProcessV1(final TupleInput ti) throws Exception {
		final String version = ti.readString();
		Assertion.checkArgument(PROCESS_BINDING_V1.equals(version), "Version de l'encodage du process incompatible. lu: {0}, attendu : {1}", version, PROCESS_BINDING_V1);
		//---------------------------------------------------------------------
		final String type = ti.readString();
		final String category = ti.readString();
		final String location = ti.readString();
		final String appName = ti.readString();
		final Date startDate = new Date(ti.readLong());
		final Double duration = ti.readDouble();
		final KProcessBuilder processBuilder = new KProcessBuilder(appName, type, startDate, duration);
		while (ti.available() > 0) {
			final String subInfoType = ti.readString();
			if ("Measure".equals(subInfoType)) {
				final String mName = ti.readString();
				final double mValue = ti.readDouble();
				//Passer par un NS
				if (mName != AProcess.SUB_DURATION) { //subDuration is computed from subProcesses durations instead
					processBuilder.setMeasure(mName, mValue);
				}
			} else if ("MetaData".equals(subInfoType)) {
				final String mdName = ti.readString();
				final String mdValue = ti.readString();
				//Passer par un NS
				processBuilder.addMetaData(mdName, mdValue);
			} else if ("SubProcess".equals(subInfoType)) {
				final AProcess subProcess = doEntryToProcessV1(ti);
				processBuilder.addSubProcess(subProcess, false);
			} else if ("P-END".equals(subInfoType)) {
				break; //on a terminé
			} else {
				//On laisse tomber.
			}
		}
		return processBuilder.withCategory(category).withLocation(location).build();
	}

	private void doProcessToEntry(final AProcess process, final TupleOutput to) {
		to.writeString(PROCESS_BINDING_V1);//Marqueur de version
		to.writeString(process.getType());
		to.writeString(process.getCategory());
		to.writeString(process.getLocation());
		to.writeString(process.getAppName());
		to.writeLong(process.getStartDate().getTime());
		to.writeDouble(process.getMeasures().get(AProcess.DURATION));

		for (final Entry<String, Double> measure : process.getMeasures().entrySet()) {
			if (measure.getKey() != AProcess.SUB_DURATION) { //subDuration is computed from subProcesses durations instead
				to.writeString("Measure");
				to.writeString(measure.getKey());
				to.writeDouble(measure.getValue());
			}
		}

		for (final Map.Entry<String, String> entry : process.getMetaDatas().entrySet()) {
			to.writeString("MetaData");
			to.writeString(entry.getKey());
			to.writeString(entry.getValue());
		}

		for (final AProcess subProcess : process.getSubProcesses()) {
			to.writeString("SubProcess");
			doProcessToEntry(subProcess, to);
		}

		to.writeString("P-END"); //On place un marqueur de fin (pour la lecture des sous process)
	}

}
