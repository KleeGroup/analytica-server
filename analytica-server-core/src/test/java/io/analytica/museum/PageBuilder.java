package io.analytica.museum;

import io.analytica.api.KProcess;

import java.util.Date;

interface PageBuilder {
	KProcess createPage(final Date dateVisit);

}
