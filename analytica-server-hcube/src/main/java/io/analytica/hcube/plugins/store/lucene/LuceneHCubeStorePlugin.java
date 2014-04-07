package io.analytica.hcube.plugins.store.lucene;

import io.analytica.hcube.HCategoryDictionary;
import io.analytica.hcube.cube.HCounterType;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HCubeKey;
import io.analytica.hcube.impl.HCubeStorePlugin;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.result.HSerie;
import io.vertigo.kernel.exception.VRuntimeException;
import io.vertigo.kernel.lang.Assertion;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class LuceneHCubeStorePlugin implements HCubeStorePlugin {
	//	private final Directory directory;
	private final IndexWriter indexWriter;

	public LuceneHCubeStorePlugin() {
		Directory directory = new RAMDirectory();
		indexWriter = createIndexWriter(directory);
	}

	private static IndexWriter createIndexWriter(Directory directory) {
		try {
			Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_46);
			final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
			return new IndexWriter(directory, config);
		} catch (IOException e) {
			throw new VRuntimeException(e);
		}
	}

	public void merge(HCube cube) {
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		try {
			for (HCubeKey cubeKey : cube.getKey().drillUp()) {
				Document document = new Document();
				document.add(new LongField("time", cubeKey.getTime().inMillis(), Field.Store.YES));
				document.add(new TextField("category", cubeKey.getCategory().getId(), Field.Store.YES));

				for (HMetric metric : cube.getMetrics()) {
					document.add(new StringField("metric", metric.getKey().getName(), Field.Store.YES));
					document.add(new LongField(metric.getKey().getName() + ":count", metric.getCount(), Field.Store.YES));
					//document.add(new DoubleField(metric.getKey().getName() + ":sum", metric.getSum(), Field.Store.YES));
					document.add(new DoubleField(metric.getKey().getName() + ":min", metric.get(HCounterType.min), Field.Store.YES));
					document.add(new DoubleField(metric.getKey().getName() + ":max", metric.get(HCounterType.max), Field.Store.YES));
					document.add(new DoubleField(metric.getKey().getName() + ":sqrSum", metric.get(HCounterType.sqrSum), Field.Store.YES));
					if (metric.getKey().isClustered()) {
						//						document.add(new DoubleField(metric.getKey().getName() + ":distribution", metric.get(HCounterType.sqrSum), Field.Store.YES));
						//						document.add(new DoubleField(metric.getKey().getName() + ":distribution", metric.get(HCounterType.sqrSum), Field.Store.YES));
					}

				}
				indexWriter.addDocument(document);
			}
		} catch (IOException e) {
			throw new VRuntimeException(e);
		}

	}

	public Map<HCategory, HSerie> findAll(HQuery query, HCategoryDictionary categoryDictionary) {
		// TODO Auto-generated method stub
		return null;
	}

}
