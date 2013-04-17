package com.kleegroup.analytica.hcube.query;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import kasper.kernel.lang.Builder;

import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.dimension.WhatPosition;

/**
 * Builder de la requête.
 * @author npiedeloup, pchretien
 */
public class QueryBuilder implements Builder<Query> {
	//---	
	private TimeDimension timeDimension;
	private Date from;
	private Date to;
	//----
	private List<String> what;

	public QueryBuilder on(TimeDimension timeDimension) {
		this.timeDimension = timeDimension;
		return this;
	}

	public QueryBuilder from(Date date) {
		from = date;
		return this;
	}

	public QueryBuilder to(Date date) {
		to = date;
		return this;
	}

	public QueryBuilder with(final String... what) {
		this.what = Arrays.asList(what);
		return this;
	}

	@Override
	public Query build() {
		return new Query(new TimeSelection(timeDimension, from, to), new WhatPosition(what));
	}
}
