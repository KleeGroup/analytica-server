package com.kleegroup.analytica.hcube.query;

import java.util.Date;

import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.dimension.HCategoryPosition;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;

/**
 * Builder de la requête.
 * @author npiedeloup, pchretien
 */
public final class HQueryBuilder implements Builder<HQuery> {
	private HTimeDimension timeDimension;
	private Date from;
	private Date to;
	//----
	private String type;
	private String[] subCategories;

	public HQueryBuilder on(HTimeDimension timeDimension) {
		Assertion.notNull(timeDimension);
		Assertion.isNull(this.timeDimension);
		//---------------------------------------------------------------------
		this.timeDimension = timeDimension;
		return this;
	}

	public HQueryBuilder from(Date date) {
		Assertion.notNull(date);
		Assertion.isNull(from);
		//---------------------------------------------------------------------
		from = date;
		return this;
	}

	public HQueryBuilder to(Date date) {
		Assertion.notNull(date);
		Assertion.isNull(to);
		//---------------------------------------------------------------------
		to = date;
		return this;
	}

	public HQueryBuilder with(final String type, String... subCategories) {
		Assertion.notNull(type);
		Assertion.isNull(this.type);
		//---------------------------------------------------------------------
		this.type = type;
		this.subCategories = subCategories;
		return this;
	}

	@Override
	public HQuery build() {
		return new HQuery(new HTimeSelection(timeDimension, from, to), new HCategoryPosition(type, subCategories));
	}
}
