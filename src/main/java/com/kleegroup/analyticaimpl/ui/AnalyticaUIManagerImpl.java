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
package com.kleegroup.analyticaimpl.ui;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.core.KProcess;
import com.kleegroup.analytica.server.ServerManager;
import com.kleegroup.analytica.ui.AnalyticaUIManager;

/**
 * @version $Id: AnalyticaUIManagerImpl.java,v 1.9 2012/11/26 16:11:06 npiedeloup Exp $
 * @author npiedeloup
 */
public class AnalyticaUIManagerImpl implements AnalyticaUIManager {
	//	private final ServerManager serverManager;

	//	private final Timer timer;

	/**
	 * Constructeur.
	 * @param serverManager ServerManager d'analytica
	 */
	@Inject
	public AnalyticaUIManagerImpl(final ServerManager serverManager) {
		Assertion.notNull(serverManager);
		//-----------------------------------------------------------------
		//this.serverManager = serverManager;
		//		timer = new Timer("AnalyticaUIManagerRenderer", true);
	}

	//	/** {@inheritDoc} */
	//	@Override
	//	public void start() {
	//		//		final TimerTask task = new TimerTask() {
	//		//			/** {@inheritDoc} */
	//		//			@Override
	//		//			public void run() {
	//		//				System.out.println(AnalyticaUIManagerImpl.this.toString(serverManager.getProcesses()));
	//		//			}
	//		//
	//		//		};
	//		//timer.scheduleAtFixedRate(task, 20 * 1000L, 30 * 1000L);
	//	}
	//
	//	/** {@inheritDoc} */
	//	@Override
	//	public void stop() {
	//		//	timer.cancel();
	//	}

	/** {@inheritDoc} */
	public String toString(final List<KProcess> processes) {
		for (final KProcess process : processes) {
			display(process, "\n", System.out);
		}
		return "";
	}

	private static void display(final KProcess process, final String prefix, final PrintStream out) {
		out.print(prefix);
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		out.print(sdf.format(process.getStartDate()));
		out.print(" [");
		out.print(process.getType());
		out.print("] ");
		out.print(process.getName());
		out.print(" ");
		out.print(process.getMeasures().get(KProcess.DURATION));
		out.print("ms  {");
		for (final Map.Entry<String, Double> measure : process.getMeasures().entrySet()) {
			display(measure.getKey(), measure.getValue(), prefix + " M:", out);
		}
		for (final Map.Entry<String, String> metaData : process.getMetaDatas().entrySet()) {
			display(metaData.getKey(), metaData.getValue(), prefix + " MD:", out);
		}
		for (final KProcess subProcess : process.getSubProcesses()) {
			display(subProcess, prefix + "     ", out);
		}
		out.print(prefix);
		out.print("}");

	}

	private static void display(final String measureType, final Double value, final String prefix, final PrintStream out) {
		out.print(prefix);
		out.print(measureType);
		out.print(" value:");
		out.print(value);
	}

	private static void display(final String metaDataName, final String value, final String prefix, final PrintStream out) {
		out.print(prefix);
		out.print(metaDataName);
		out.print(" value:");
		out.print(value);
	}

}
