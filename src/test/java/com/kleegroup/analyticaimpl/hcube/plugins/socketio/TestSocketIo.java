package com.kleegroup.analyticaimpl.hcube.plugins.socketio;

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
