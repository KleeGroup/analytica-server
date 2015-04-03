package io.analytica.hcube.dimension;

import io.vertigo.lang.Assertion;

import java.util.regex.Pattern;

public final class HLocation {
	public static final Pattern NAME_REGEX = Pattern.compile("[a-z][a-zA-Z]*");
	private final static String SEPARATOR = ".";
	private final String[] locationTerms;
	private final String locationPath;

	public HLocation(final String... locationTerms) {
		Assertion.checkNotNull(locationTerms);
		for (final String locationTerm : locationTerms) {
			if (!NAME_REGEX.matcher(locationTerm).matches()) {
				throw new IllegalArgumentException("locationTerm" + locationTerm + " must match regex :" + NAME_REGEX);
			}
		}
		//--------------------------------
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String locationTerm : locationTerms) {
			if (!first) {
				sb.append(SEPARATOR);
			}
			sb.append(locationTerm);
			first = false;
		}
		this.locationTerms = locationTerms.clone();
		this.locationPath = sb.toString();
	}

	public HLocation drillUp() {
		return null;
	}

	@Override
	public int hashCode() {
		return locationTerms.hashCode();
	}

	@Override
	public boolean equals(final Object value) {
		if (value == this) {
			return true;
		} else if (value instanceof HLocation) {
			return locationPath.equals(((HLocation) value).locationPath);
		}
		return false;
	}

	public String getPath() {
		return locationPath;
	}

	public String[] getlocationTerms() {
		return locationTerms;
	}
}
