package io.analytica.hcube.plugins.store.lucene;

import io.analytica.hcube.cube.HCounterType;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.impl.HCubeStorePlugin;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HResult;
import io.analytica.hcube.result.HSerie;
import io.vertigo.core.lang.Assertion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public final class LuceneHCubeStorePlugin implements HCubeStorePlugin {
	private final RamLuceneIndex index;

	//private final RamLuceneIndex categoriesIndex;

	public LuceneHCubeStorePlugin() {
		index = new RamLuceneIndex();

	}

	public void push(final String appName, final HCube cube) {
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		//	addCategory(cube.getKey().getCategory());

		final List<Document> documents = new ArrayList<>();
		for (final HKey cubeKey : cube.getKey().drillUp()) {
			final Document document = new Document();
			document.add(new LongField("time", cubeKey.getTime().inMillis(), Field.Store.YES));
			document.add(new TextField("timeDimension", cubeKey.getTime().getDimension().name(), Field.Store.YES));
			document.add(new TextField("rootCategory", cubeKey.getCategory().getRoot().getId(), Field.Store.YES));
			//			System.out.println("   root :" + cubeKey.getCategory().getRoot().getId());
			document.add(new StringField("category", cubeKey.getCategory().getId(), Field.Store.YES));
			//			System.out.println("   category :" + cubeKey.getCategory().getId());
			document.add(new LongField("metrics", cube.getMetrics().size(), Field.Store.YES));
			//adding metrics
			int m = 0;
			for (final HMetric metric : cube.getMetrics()) {
				document.add(new StringField(m + ":metric", metric.getKey().getName(), Field.Store.YES));
				document.add(new LongField(m + ":count", metric.getCount(), Field.Store.YES));
				document.add(new DoubleField(m + ":sum", metric.get(HCounterType.sum), Field.Store.YES));
				document.add(new DoubleField(m + ":min", metric.get(HCounterType.min), Field.Store.YES));
				document.add(new DoubleField(m + ":max", metric.get(HCounterType.max), Field.Store.YES));
				document.add(new DoubleField(m + ":sqrSum", metric.get(HCounterType.sqrSum), Field.Store.YES));
				if (metric.getKey().hasDistribution()) {
					for (final Entry<Double, Long> entry : metric.getDistribution().getData().entrySet()) {
						document.add(new LongField(m + ":distribution:" + entry.getKey(), entry.getValue(), Field.Store.YES));
					}
				}
				m++;
			}
			documents.add(document);
		}
		index.addDocuments(documents);
	}

	public Set<HCategory> getAllSubCategories(final String appName, final HCategory category) {
		return getAllSubCategories(appName, "category", category);

	}

	private Set<HCategory> getAllSubCategories(final String appName, final String field, final HCategory categoryFilter) {
		//	Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		final Set<HCategory> categories = new HashSet<>();
		for (final String term : index.terms(field)) {
			//			System.out.println("  field : " + field);
			//			System.out.println("  term : " + term);
			if (categoryFilter == null) {
				categories.add(new HCategory(term));
			} else if (term.startsWith(categoryFilter.getId() + "/")) {
				//On filtre les catégories comnecant par la catégorie
				//				System.out.println("  categoryFilter : " + category.getId());
				categories.add(new HCategory(term));
			}
		}
		System.out.println("CCCC [" + field + "]>>" + categories);
		return categories;
	}

	public Set<HCategory> getAllRootCategories(final String appName) {
		return getAllSubCategories(appName, "rootCategory", null);

	}

	public long count(final String appName) {
		return index.numDocs();
	}

	//	private void addCategory(HCategory category) {
	//		Assertion.checkNotNull(category);
	//		//---------------------------------------------------------------------
	//		HCategory currentCategory = category;
	//		HCategory parentCategory;
	//		boolean drillUp;
	//		do {
	//			parentCategory = currentCategory.drillUp();
	//			//Optim :Si la catégorie existe déjà alors sa partie gauche aussi !!
	//			//On dispose donc d'une info pour savoir si il faut remonter
	//			drillUp = doPut(parentCategory, currentCategory);
	//			currentCategory = parentCategory;
	//		} while (drillUp);
	//	}

	public HResult execute(final String appName, final HQuery query) {
		return new HResult(query, HQueryUtil.findCategories(appName, query.getCategorySelection(), this), findAll(appName, query));
	}

	private Map<HCategory, HSerie> findAll(final String appName, final HQuery query) {
		Assertion.checkNotNull(query);
		//		Assertion.checkNotNull(cubeStore);
		//---------------------------------------------------------------------
		//On itère sur les séries indexées par les catégories de la sélection.
		final Map<HCategory, HSerie> cubeSeries = new HashMap<>();

		for (final HCategory category : HQueryUtil.findCategories(appName, query.getCategorySelection(), this)) {
			//			final List<HCube> cubes = new ArrayList<>();
			System.out.println(">>>>findAll " + query);
			final List<HCube> cubes = index.findAll(category, query.getTimeSelection());
			cubeSeries.put(category, new HSerie(category, cubes));
		}
		return cubeSeries;
	}
}
