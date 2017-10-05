package io.vertigo.analytics.server;

public class LogMessage<O> {

	private String appName;
	private String host;
	private O event;

	public String getAppName() {
		return appName;
	}

	public void setAppName(final String appName) {
		this.appName = appName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public O getEvent() {
		return event;
	}

	public void setEvent(final O event) {
		this.event = event;
	}

}
