package com.kleegroup.museum;

import java.util.Date;

import com.kleegroup.analytica.core.KProcess;

public interface PageBuilder {
	KProcess createPage(final Date dateVisit);

}
