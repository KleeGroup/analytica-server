package io.analytica.hcube.dimension;

import io.vertigo.lang.Assertion;

import java.util.regex.Pattern;

public final class HLocation {
	public static final Pattern NAME_REGEX = Pattern.compile("[a-z][a-zA-Z]*");
	private final String[] location;

	public HLocation(final String[] location) {
		Assertion.checkNotNull(location);
//		Assertion.checkState(location.length!=0, "");
		// TODO A REIMPLEMENTER
//		if (!NAME_REGEX.matcher(location).matches()) {
//			throw new IllegalArgumentException("location " + location + " must match regex :" + NAME_REGEX);
//		}
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

	public String[] getPath() {
		return location;
	}
}
