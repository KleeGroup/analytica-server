package com.kleegroup.analytica.hcube.query;

import java.util.Date;
import java.util.List;

import kasper.kernel.lang.Builder;

import com.kleegroup.analytica.hcube.cube.DataKey;
import com.kleegroup.analytica.hcube.dimension.TimeDimension;
import com.kleegroup.analytica.hcube.dimension.WhatDimension;

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
	private WhatDimension whatDimension;
	private String[] whatValues;
	//---
	private List<DataKey> keys;

	public QueryBuilder(List<DataKey> keys) {
		this.keys = keys;
	}

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

	public QueryBuilder on(WhatDimension whatDimension) {
		this.whatDimension = whatDimension;
		return this;
	}

	public QueryBuilder with(final String... whatValues) {
		this.whatValues = whatValues;
		return this;

	}

	@Override
	public Query build() {
		return new Query(new TimeSelection(timeDimension, from, to), new WhatSelection(whatDimension, whatValues), keys);
	}

}
