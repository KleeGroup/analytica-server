package com.kleegroup.analyticaimpl.hcube.plugins.socketio;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import org.json.JSONObject;

final class EmptyIOCallback implements IOCallback {

	@Override
	public void onMessage(final String data, final IOAcknowledge ack) {
	}

	@Override
	public void onError(final SocketIOException socketIOException) {
		System.out.println("an Error occured");
		socketIOException.printStackTrace();
	}

	@Override
	public void onDisconnect() {
	}

	@Override
	public void onConnect() {
	}

	@Override
	public void on(final String event, final IOAcknowledge ack, final Object... args) {
		System.out.println(">>>>on : " + event);
	}

	@Override
	public void onMessage(final JSONObject json, final IOAcknowledge ack) {
	}
}
