package io.analytica.hcube.query;

public interface HSelector {
	HTimeSelector getTimeSelector();

	HCategorySelector getCategorySelector();
}
