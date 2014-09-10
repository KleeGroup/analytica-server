package io.analytica.hcube.plugins.store.lucene;

import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.cube.HCubeBuilder;
import io.analytica.hcube.cube.HMetric;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HTime;
import io.analytica.hcube.dimension.HTimeDimension;
import io.analytica.hcube.query.HTimeSelection;

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

	public void addDocuments(final List<Document> documents) {
		try (final IndexWriter indexWriter = createIndexWriter()) {
			System.out.println("add doc");
			for (final Document document : documents) {
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

	public Set<String> terms(final String field) {
		try (final IndexReader indexReader = createIndexReader()) {
			System.out.println("terms :" + field);
			final Fields fields = MultiFields.getFields(indexReader);
			if (fields == null) {
				return Collections.emptySet();
			}
			final Terms terms = fields.terms(field);
			final Set<String> uniqueTerms = new HashSet<>();
			final TermsEnum iterator = terms.iterator(null);
			BytesRef byteRef = null;
			while ((byteRef = iterator.next()) != null) {
				final String term = new String(byteRef.bytes, byteRef.offset, byteRef.length);
				uniqueTerms.add(term);
			}
			return uniqueTerms;
		} catch (final IOException e) {
			throw new VRuntimeException(e);
		}
	}

	public List<HCube> findAll(final HCategory category, final HTimeSelection timeSelection) {
		//!!!!!!
		//!!!!!!
		//!hitsPerPage
		//!!!!!!
		//!!!!!!
		final int hitsPerPage = 100000;

		try (final IndexReader indexReader = createIndexReader()) {
			System.out.println("index numdocs" + indexReader.numDocs());
			final Document doc = indexReader.document(1);
			System.out.println("doc" + doc);
			//--
			final IndexSearcher searcher = new IndexSearcher(indexReader);
			final String queryText = category.getId();
			final Query query1 = new QueryBuilder(analyzer).createPhraseQuery("rootCategory", queryText);
			final Query query2 = new QueryBuilder(analyzer).createPhraseQuery("timeDimension", HTimeDimension.SixMinutes.name());

			final BooleanQuery query = new BooleanQuery();
			query.add(query1, Occur.MUST);
			query.add(query2, Occur.MUST);

			final TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
			searcher.search(query, collector);
			final ScoreDoc[] hits = collector.topDocs().scoreDocs;

			// results
			System.out.println("Found " + hits.length + " hits.");
			final List<HCube> cubes = new ArrayList<>();
			for (final ScoreDoc hit : hits) {
				final int docId = hit.doc;
				final Document document = searcher.doc(docId);
				//			document .get(name)
				final HTimeDimension timeDimension = HTimeDimension.valueOf(document.get("timeDimension"));
				final Date date = new Date(Long.valueOf(document.get("time")));
				final HTime time = new HTime(date, timeDimension);
				final HKey cubeKey = new HKey(time, category);

				final long metrics = Long.valueOf(document.get("metrics"));
				final HCubeBuilder cubeBuilder = new HCubeBuilder(cubeKey);
				for (int m = 0; m < metrics; m++) {
					final HMetricKey metricKey = new HMetricKey(document.get(m + ":metric"), false);
					final long count = Long.valueOf(document.get(m + ":count"));
					final double sum = Double.valueOf(document.get(m + ":sum"));
					final double min = Double.valueOf(document.get(m + ":min"));
					final double max = Double.valueOf(document.get(m + ":max"));
					final double sqrSum = Double.valueOf(document.get(m + ":sqrSum"));
					final HMetric metric = new HMetric(metricKey, count, min, max, sum, sqrSum, null);
					cubeBuilder.withMetric(metric);
				}
				final HCube cube = cubeBuilder.build();
				cubes.add(cube);
				System.out.println("reload cube :" + cube);
			}
			return cubes;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
