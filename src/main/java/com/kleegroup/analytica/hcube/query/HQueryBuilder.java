package com.kleegroup.analytica.hcube.query;

import java.util.Date;

import kasper.kernel.lang.Builder;
import kasper.kernel.util.Assertion;

import com.kleegroup.analytica.hcube.HCubeManager;
import com.kleegroup.analytica.hcube.dimension.HCategory;
import com.kleegroup.analytica.hcube.dimension.HTimeDimension;

/**
 * Builder de la requête.
 * @author npiedeloup, pchretien
 */
public final class HQueryBuilder implements Builder<HQuery> {
	private HCubeManager cubeManager;
	private HTimeDimension timeDimension;
	private Date from;
	private Date to;
	//----
	private HCategory category;
	private boolean children;

	public HQueryBuilder(final HCubeManager cubeManager) {
		Assertion.notNull(cubeManager);
		//---------------------------------------------------------------------
		this.cubeManager = cubeManager;
	}

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

	public HQueryBuilder withChildren(final String type, String... subCategories) {
		return doWith(type, subCategories, true);
	}

	public HQueryBuilder with(final String type, String... subCategories) {
		return doWith(type, subCategories, false);
	}

	private HQueryBuilder doWith(final String type, String[] subTypes, boolean children) {
		Assertion.notNull(type);
		Assertion.isNull(this.category);
		//---------------------------------------------------------------------
		this.category = new HCategory(type, subTypes);
		this.children = children;
		return this;
	}

	/** {@inheritDoc} */
	public HQuery build() {
		return new HQuery(new HTimeSelection(timeDimension, from, to), new HCategorySelection(cubeManager.getCategoryDictionary(), category, children));
	}
}
