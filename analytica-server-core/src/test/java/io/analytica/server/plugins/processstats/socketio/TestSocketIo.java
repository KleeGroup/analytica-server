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

import io.socket.SocketIO;

import org.json.JSONArray;

public class TestSocketIo {

	public static void main(final String[] args) throws Exception {
		final SocketIO socket = new SocketIO("http://npiedeloup1:8090");
		// This line is cached until the connection is establisched.
		socket.connect(new EmptyIOCallback());
		Thread.sleep(2000);
		for (int i = 0; i < 50000; i++) {
			Thread.sleep(150);
			socket.emit("ping", new JSONArray("[" + i * 10 % 360 + ", " + i % 250 + ", 3]"));
		}
		socket.disconnect();
	}

}
