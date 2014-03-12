/**
 * Analytica - beta version - Systems Monitoring Tool
 *
 * Copyright (C) 2013, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidière - BP 159 - 92357 Le Plessis Robinson Cedex - France
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

import io.analytica.api.KProcess;
import io.analytica.api.KProcessBuilder;
import io.vertigo.kernel.lang.Assertion;

import java.util.Date;
import java.util.Map.Entry;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Classe qui pour un DtObject permet de lire/écrire un tuple.
 * Le binding est indépendant de la DtDefinition.
 *
 * @author pchretien
 * @version $Id: KProcessBinding.java,v 1.9 2012/11/08 17:07:40 pchretien Exp $
 */
final class KProcessBinding extends TupleBinding {
	private static final String PROCESS_BINDING_PREFIX = "ProcessBinding";
	private static final String PROCESS_BINDING_V1 = PROCESS_BINDING_PREFIX + "V1";
	private static final String PROCESS_BINDING_V2 = PROCESS_BINDING_PREFIX + "V2";

	/** {@inheritDoc} */
	@Override
	public Object entryToObject(final TupleInput ti) {
		try {
			//			/*final UUID uuid =*/UUID.fromString(ti.readString());
			final String version = detectVersion(ti);
			ti.reset();
			if (PROCESS_BINDING_V1.equals(version)) {
				return doEntryToProcessV1(ti);
			} else if (PROCESS_BINDING_V2.equals(version)) {
				return doEntryToProcessV2(ti);
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
			//			final UUID uuid = UUID.randomUUID();
			//			to.writeString(uuid.toString());
			doProcessToEntry((KProcess) object, to);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private KProcess doEntryToProcessV2(final TupleInput ti) throws Exception {
		final String version = ti.readString();
		Assertion.checkArgument(PROCESS_BINDING_V2.equals(version), "Version de l'encodage du process incompatible. lu: {0}, attendu : {1}", version, PROCESS_BINDING_V2);
		//---------------------------------------------------------------------
		final String type = ti.readString();
		final int nbNames = ti.readInt();
		final String[] names = new String[nbNames];
		for (int i = 0; i < nbNames; i++) {
			names[i] = ti.readString();
		}
		final Date startDate = new Date(ti.readLong());
		final Double duration = ti.readDouble();
		final KProcessBuilder processBuilder = new KProcessBuilder(startDate, duration, type, names);
		while (ti.available() > 0) {
			final String subInfoType = ti.readString();
			if ("Measure".equals(subInfoType)) {
				final String mName = ti.readString();
				final double mValue = ti.readDouble();
				//Passer par un NS
				if (mName != KProcess.SUB_DURATION) { //subDuration is computed from subProcesses durations instead 
					processBuilder.setMeasure(mName, mValue);
				}
			} else if ("MetaData".equals(subInfoType)) {
				final String mdName = ti.readString();
				final String mdValue = ti.readString();
				//Passer par un NS
				processBuilder.setMetaData(mdName, mdValue);
			} else if ("SubProcess".equals(subInfoType)) {
				final KProcess subProcess = doEntryToProcessV2(ti);
				processBuilder.addSubProcess(subProcess);
			} else if ("P-END".equals(subInfoType)) {
				break; //on a terminé
			} else {
				//On laisse tomber.
			}
		}
		return processBuilder.build();
	}

	private void doProcessToEntry(final KProcess process, final TupleOutput to) {
		to.writeString(PROCESS_BINDING_V2);//Marqueur de version
		to.writeString(process.getType());

		to.writeInt(process.getSubTypes().length);
		for (final String namePart : process.getSubTypes()) {
			to.writeString(namePart);
		}
		to.writeLong(process.getStartDate().getTime());
		to.writeDouble(process.getMeasures().get(KProcess.DURATION));
		for (final Entry<String, Double> measure : process.getMeasures().entrySet()) {
			if (measure.getKey() != KProcess.SUB_DURATION) { //subDuration is computed from subProcesses durations instead 				
				to.writeString("Measure");
				to.writeString(measure.getKey());
				to.writeDouble(measure.getValue());
			}
		}
		for (final Entry<String, String> metaData : process.getMetaDatas().entrySet()) {
			to.writeString("MetaData");
			to.writeString(metaData.getKey());
			to.writeString(metaData.getValue());
		}
		for (final KProcess subProcess : process.getSubProcesses()) {
			to.writeString("SubProcess");
			doProcessToEntry(subProcess, to);
		}
		to.writeString("P-END"); //On place un marqueur de fin (pour la lecture des sous process)
	}

	private KProcess doEntryToProcessV1(final TupleInput ti) throws Exception {
		final String type = ti.readString();
		final String name = ti.readString();
		final Date startDate = new Date(ti.readLong());
		final Double duration = ti.readDouble();
		final KProcessBuilder processBuilder = new KProcessBuilder(startDate, duration, type, name.split("/"));
		while (ti.available() > 0) {
			final String subInfoType = ti.readString();
			if ("Measure".equals(subInfoType)) {
				final String mName = ti.readString();
				final double mValue = ti.readDouble();
				//Passer par un NS
				processBuilder.setMeasure(mName, mValue);
			} else if ("MetaData".equals(subInfoType)) {
				final String mdName = ti.readString();
				final String mdValue = ti.readString();
				//Passer par un NS
				processBuilder.setMetaData(mdName, mdValue);
			} else if ("SubProcess".equals(subInfoType)) {
				final KProcess subProcess = doEntryToProcessV1(ti);
				processBuilder.addSubProcess(subProcess);
			} else if ("P-END".equals(subInfoType)) {
				break; //on a terminé
			} else {
				//On laisse tomber.
			}
		}
		return processBuilder.build();
	}

}
