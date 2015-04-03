package io.analytica.hcube;

public class HCubeStoreException extends Exception {
	private static final long serialVersionUID = 1L;

	public HCubeStoreException(final String message) {
		super(message);
	}

	public HCubeStoreException(final String message, final Throwable t) {
		super(message, t);
	}
}
