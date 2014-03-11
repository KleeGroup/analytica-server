package com.kleegroup.analytica.museum;

import java.util.Date;

import com.kleegroup.analytica.core.KProcess;

interface PageBuilder {
	KProcess createPage(final Date dateVisit);

}
