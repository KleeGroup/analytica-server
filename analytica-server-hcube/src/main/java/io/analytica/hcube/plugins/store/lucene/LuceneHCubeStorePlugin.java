package io.analytica.hcube.plugins.store.lucene;

import io.analytica.hcube.cube.HCounterType;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HCubeKey;
import io.analytica.hcube.impl.HCubeStorePlugin;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HResult;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.lang.Assertion;

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

	public void push(String appName, HCube cube) {
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		//	addCategory(cube.getKey().getCategory());

		List<Document> documents = new ArrayList<>();
		for (final HCubeKey cubeKey : cube.getKey().drillUp()) {
			final Document document = new Document();
			document.add(new LongField("time", cubeKey.getTime().inMillis(), Field.Store.YES));
			document.add(new StringField("timeDimension", cubeKey.getTime().getDimension().name(), Field.Store.YES));
			document.add(new TextField("rootCategory", cubeKey.getCategory().getRoot().getId(), Field.Store.YES));
			//			System.out.println("   root :" + cubeKey.getCategory().getRoot().getId());
			document.add(new StringField("category", cubeKey.getCategory().getId(), Field.Store.YES));
			//			System.out.println("   category :" + cubeKey.getCategory().getId());
			document.add(new LongField("metrics", cube.getMetrics().size(), Field.Store.YES));
			//adding metrics
			int m = 0;
			for (final HMetric metric : cube.getMetrics()) {
				document.add(new StringField("metric:" + m, metric.getKey().getName(), Field.Store.YES));
				document.add(new LongField(m + ":count", metric.getCount(), Field.Store.YES));
				document.add(new DoubleField(m + ":sum", metric.get(HCounterType.sum), Field.Store.YES));
				document.add(new DoubleField(m + ":min", metric.get(HCounterType.min), Field.Store.YES));
				document.add(new DoubleField(m + ":max", metric.get(HCounterType.max), Field.Store.YES));
				document.add(new DoubleField(m + ":sqrSum", metric.get(HCounterType.sqrSum), Field.Store.YES));
				if (metric.getKey().hasDistribution()) {
					for (Entry<Double, Long> entry : metric.getDistribution().getData().entrySet()) {
						document.add(new LongField(m + ":distribution:" + entry.getKey(), entry.getValue(), Field.Store.YES));
					}
				}
				m++;
			}
			documents.add(document);
		}
		index.addDocuments(documents);
	}

	public Set<HCategory> getAllSubCategories(String appName, HCategory category) {
		return getAllSubCategories(appName, "category", category);

	}

	private Set<HCategory> getAllSubCategories(String appName, String field, HCategory categoryFilter) {
		//	Assertion.checkNotNull(category);
		//---------------------------------------------------------------------
		Set<HCategory> categories = new HashSet<>();
		for (String term : index.terms(field)) {
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

	public Set<HCategory> getAllRootCategories(String appName) {
		return getAllSubCategories(appName, "rootCategory", null);

	}

	public long count(String appName) {
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

	public HResult execute(String appName, HQuery query) {
		return new HResult(query, query.getAllCategories(appName, this), findAll(appName, query));
	}

	private Map<HCategory, HSerie> findAll(String appName, final HQuery query) {
		Assertion.checkNotNull(query);
		//		Assertion.checkNotNull(cubeStore);
		//---------------------------------------------------------------------
		//On itère sur les séries indexées par les catégories de la sélection.
		final Map<HCategory, HSerie> cubeSeries = new HashMap<>();

		for (final HCategory category : query.getAllCategories(appName, this)) {
			//			final List<HCube> cubes = new ArrayList<>();
			System.out.println(">>>>findAll " + query);
			final List<HCube> cubes = index.findAll(category, query.getTimeSelection());
			cubeSeries.put(category, new HSerie(category, cubes));
		}
		return cubeSeries;
	}
}
