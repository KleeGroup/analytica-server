package io.analytica.hcube.plugins.store.lucene;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.cube.HMetricKey;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;
import io.analytica.hcube.query.HTimeSelection;
import io.vertigo.kernel.exception.VRuntimeException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;

/**
 * Implémentation Ram de l'index Lucene.
 * Il existe une seule instance par JVM.
 * Il ne doit aussi exister qu'un seul writer.
 *
 * @author  pchretien, npiedeloup
 * @version $Id: RamLuceneIndex.java,v 1.6 2014/01/24 17:59:57 pchretien Exp $
 */
final class RamLuceneIndex {
	private final Directory directory;
	private final Analyzer analyzer;

	/**
	 * @param dtDefinition DtDefinition des objets indexés
	 * @param analyzer Analyzer à utiliser
	 * @throws IOException Exception I/O
	 */
	RamLuceneIndex() {
		analyzer = new SimpleAnalyzer(Version.LUCENE_40);
		directory = new RAMDirectory();
		buildIndex();
	}

	private void buildIndex() {
		try (final IndexWriter indexWriter = createIndexWriter()) {
			// we are creating an empty index if it does not exist 
			System.out.println("build index");
		} catch (final IOException e) {
			throw new VRuntimeException(e);
		}
	}

	/** {@inheritDoc} */
	private IndexWriter createIndexWriter() throws IOException {
		final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		return new IndexWriter(directory, config);
	}

	private IndexReader createIndexReader() throws IOException {
		return IndexReader.open(directory);
	}

	public void addDocuments(List<Document> documents) {
		try (final IndexWriter indexWriter = createIndexWriter()) {
			System.out.println("add doc");
			for (Document document : documents) {
				indexWriter.addDocument(document);
			}
		} catch (final IOException e) {
			throw new VRuntimeException(e);
		}
	}

	public int numDocs() {
		try (final IndexReader indexReader = createIndexReader()) {
			System.out.println("num docs");
			return indexReader.numDocs();
		} catch (final IOException e) {
			throw new VRuntimeException(e);
		}

	}

	public Set<String> terms(String field) {
		try (final IndexReader indexReader = createIndexReader()) {
			System.out.println("terms :" + field);
			Fields fields = MultiFields.getFields(indexReader);
			if (fields == null) {
				return Collections.emptySet();
			}
			Terms terms = fields.terms(field);
			Set<String> uniqueTerms = new HashSet<>();
			TermsEnum iterator = terms.iterator(null);
			BytesRef byteRef = null;
			while ((byteRef = iterator.next()) != null) {
				String term = new String(byteRef.bytes, byteRef.offset, byteRef.length);
				uniqueTerms.add(term);
			}
			return uniqueTerms;
		} catch (final IOException e) {
			throw new VRuntimeException(e);
		}
	}

	public List<HCube> findAll(HCategory category, HTimeSelection timeSelection) {
		//!!!!!!
		//!!!!!!
		//!hitsPerPage
		//!!!!!!
		//!!!!!!
		final int hitsPerPage = 100000;

		try (final IndexReader indexReader = createIndexReader()) {
			System.out.println("index numdocs" + indexReader.numDocs());
			Document doc = indexReader.document(1);
			System.out.println("doc" + doc);
			//--
			final IndexSearcher searcher = new IndexSearcher(indexReader);
			String queryText = category.getId();
			Query query1 = new QueryBuilder(analyzer).createPhraseQuery("rootCategory", queryText);
			Query query2 = new QueryBuilder(analyzer).createPhraseQuery("timeDimension", HTimeDimension.SixMinutes.name());

			BooleanQuery query = new BooleanQuery();
			query.add(query1, Occur.MUST);
			query.add(query2, Occur.MUST);

			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			// results
			System.out.println("Found " + hits.length + " hits.");
			List<HCube> cubes = new ArrayList<>();
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document document = searcher.doc(docId);
				//			document .get(name)
				HTimeDimension timeDimension = HTimeDimension.valueOf(document.get("timeDimension"));
				Date date = new Date(Long.valueOf(document.get("time")));
				HTime time = new HTime(date, timeDimension);
				HKey cubeKey = new HKey(time, category);

				long metrics = Long.valueOf(document.get("metrics"));
				HCubeBuilder cubeBuilder = new HCubeBuilder(cubeKey);
				for (int m = 0; m < metrics; m++) {
					HMetricKey metricKey = new HMetricKey(document.get(m + ":metric"), false);
					long count = Long.valueOf(document.get(m + ":count"));
					double sum = Double.valueOf(document.get(m + ":sum"));
					double min = Double.valueOf(document.get(m + ":min"));
					double max = Double.valueOf(document.get(m + ":max"));
					double sqrSum = Double.valueOf(document.get(m + ":sqrSum"));
					HMetric metric = new HMetric(metricKey, count, min, max, sum, sqrSum, null);
					cubeBuilder.withMetric(metric);
				}
				HCube cube = cubeBuilder.build();
				cubes.add(cube);
				System.out.println("reload cube :" + cube);
			}
			return cubes;
		} catch (final IOException e) {
			throw new VRuntimeException(e);
		}
	}
}
