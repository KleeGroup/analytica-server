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
package io.analytica.server.plugins.processstats.socketio;

import io.analytica.api.KProcess;
import io.analytica.server.impl.ProcessStatsPlugin;
import io.socket.SocketIO;
import io.vertigo.lang.Activeable;
import io.vertigo.lang.Assertion;

import java.net.MalformedURLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Stockage des process, et conservation statistique de l'arbre.
 * 
 * Transformation d'un Process constitué de sous-process.
 * Chaque Process (et donc sous process) est transformé en Cube avec :
 * - une agregation des mesures de ce process
 * - une agregation des mesures des sous process 
 * 
 * 
 * @author npiedeloup
 * @version $Id: StandardProcessEncoderPlugin.java,v 1.16 2012/10/16 17:27:12 pchretien Exp $
 */
public final class SocketIoProcessStatsPlugin implements ProcessStatsPlugin, Activeable {

	private final String socketIoUrl;
	private SocketIO socket;

	/**
	 * @param socketIoUrl Chemin du serveur SocketIo.
	 */
	@Inject
	public SocketIoProcessStatsPlugin(@Named("socketIoUrl") final String socketIoUrl) {
		Assertion.checkArgNotEmpty(socketIoUrl);
		this.socketIoUrl = socketIoUrl;
	}

	/** {@inheritDoc} */
	public void merge(final KProcess process) {
		final long time = process.getStartDate().getTime();
		final double duration = process.getDuration() / 2000 * 300;
		final int size = process.getSubProcesses().size() + 1;
		try {
			socket.emit("ping", new JSONArray("[" + time / 1000 % 360 + ", " + duration + ", " + size + "]"));
		} catch (final JSONException e) {
			throw new RuntimeException("Erreur de publication SocketIo", e);
		}
		for (final KProcess subProcess : process.getSubProcesses()) {
			merge(subProcess);
		}
	}

	/** {@inheritDoc} */
	public void start() {
		try {
			socket = new SocketIO(socketIoUrl);
			socket.connect(new EmptyIOCallback());
		} catch (final MalformedURLException e) {
			throw new RuntimeException("Erreur de connection SocketIo", e);
		}
	}

	/** {@inheritDoc} */
	public void stop() {
		socket.disconnect();
	}
}
