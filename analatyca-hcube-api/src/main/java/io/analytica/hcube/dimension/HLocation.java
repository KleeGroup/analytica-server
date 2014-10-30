package io.analytica.hcube.dimension;

import io.vertigo.lang.Assertion;

public final class HLocation {
	private final String location;

	public HLocation(final String location) {
		Assertion.checkArgNotEmpty(location);
		//---------------------------------------------------------------------
		this.location = location;
	}

	public HLocation drillUp() {
		return null;
	}

	@Override
	public int hashCode() {
		return location.hashCode();
	}

	@Override
	public boolean equals(final Object value) {
		if (value == this) {
			return true;
		} else if (value instanceof HLocation) {
			return location.equals(((HLocation) value).location);
		}
		return false;
	}
}
