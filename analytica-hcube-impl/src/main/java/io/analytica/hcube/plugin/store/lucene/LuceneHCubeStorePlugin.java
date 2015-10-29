package io.analytica.hcube.plugin.store.lucene;

import io.analytica.hcube.HCubeStoreException;
import io.analytica.hcube.cube.HCube;
import io.analytica.hcube.dimension.HCategory;
import io.analytica.hcube.dimension.HKey;
import io.analytica.hcube.dimension.HLocation;
import io.analytica.hcube.dimension.HTimeDimension;
import io.analytica.hcube.impl.HCubeStorePlugin;
import io.analytica.hcube.query.HCategorySelection;
import io.analytica.hcube.query.HLocationSelection;
import io.analytica.hcube.query.HQuery;
import io.analytica.hcube.query.HSelector;
import io.analytica.hcube.result.HSerie;
import io.vertigo.lang.Assertion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Implémentation disque lucene
 *
 * @author dslobozian
 */
public class LuceneHCubeStorePlugin implements HCubeStorePlugin {
	private final Set<String> appNames = new HashSet<>();
	private final Map<String/*appName*/, Directory/*appPath*/> dbPaths = new HashMap<>();
	private final Path dbPath;
	private final StandardAnalyzer analyzer = new StandardAnalyzer();

	private final String LUCENE_DOCUMENT_CATEGORY = "category";
	private final String LUCENE_DOCUMENT_DATE="date";
	private final String LUCENE_DOCUMENT_LOCATION = "location";
	private final String LUCENE_SETTINGS = "settings";
	private final String LUCENE_SETTINGS_VALUE = "settings_value";
	private final String LUCENE_LAST_DOCUMENT_ID = "last_id";
	private final String LUCENE_DOCUMENT_CUBE = "cube";
	private static final Gson gson = new GsonBuilder().create();

	/**
	 *
	 */
	@Inject
	public LuceneHCubeStorePlugin(@Named("path") final String path) {
		Assertion.checkArgument(Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS), "Le répertoire pour la base lucene est inexistant", path);
		dbPath = Paths.get(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getAppNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @throws HCubeStoreException
	 */
	@Override
	public List<HCategory> findCategories(final String appName, final HCategorySelection categorySelection) throws HCubeStoreException {
		Assertion.checkArgNotEmpty(appName);
		Assertion.checkNotNull(categorySelection);
		//---------------------------------------------------------------------
		if (!appNames.contains(appName)) {
			return Collections.emptyList();
		}
		//---------------------------------------------------------------------
		final List<HCategory> matchingCategories = new ArrayList<>();
		try (final IndexReader indexReader = DirectoryReader.open(getAppDirectory(appName))) {
			final Fields fields = MultiFields.getFields(indexReader);
			final Terms terms = fields.terms(LUCENE_DOCUMENT_CATEGORY);
			final TermsEnum iterator = terms.iterator();
			BytesRef byteRef = null;
			while ((byteRef = iterator.next()) != null) {
				final String categoryPath = new String(byteRef.bytes, byteRef.offset, byteRef.length);
				final HCategory category = new HCategory(categoryPath);
				if (categorySelection.matches(category)) {
					matchingCategories.add(category);
				}
			}
		} catch (final IndexNotFoundException e) {
			return Collections.emptyList();
		} catch (final IOException e) {
			throw new HCubeStoreException("Erreur lors de la recherche des fields " + LUCENE_DOCUMENT_CATEGORY, e);
		}

		return Collections.unmodifiableList(matchingCategories);
	}

	/**
	 * {@inheritDoc}
	 * @throws HCubeStoreException
	 */
	@Override
	public List<HLocation> findLocations(final String appName, final HLocationSelection locationSelection) throws HCubeStoreException {
		Assertion.checkArgNotEmpty(appName);
		Assertion.checkNotNull(locationSelection);
		//---------------------------------------------------------------------
		if (!appNames.contains(appName)) {
			return Collections.emptyList();
		}
		//---------------------------------------------------------------------
		final List<HLocation> matchingLocations = new ArrayList<>();
		try (final IndexReader indexReader = DirectoryReader.open(getAppDirectory(appName))) {
			final Fields fields = MultiFields.getFields(indexReader);
			final Terms terms = fields.terms(LUCENE_DOCUMENT_LOCATION);
			final TermsEnum iterator = terms.iterator();
			BytesRef byteRef = null;
			while ((byteRef = iterator.next()) != null) {
				final String locationPath = new String(byteRef.bytes, byteRef.offset, byteRef.length);
				final HLocation location = new HLocation(locationPath);
				if (locationSelection.matches(location)) {
					matchingLocations.add(location);
				}
			}
		} catch (final IndexNotFoundException e) {
			return Collections.emptyList();
		} catch (final IOException e) {
			throw new HCubeStoreException("Erreur lors de la recherche des fields " + LUCENE_DOCUMENT_CATEGORY, e);
		}

		return Collections.unmodifiableList(matchingLocations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(final String appName, final HKey key, final HCube cube, final String processKey) throws HCubeStoreException {
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(cube);
		//---------------------------------------------------------------------
		try (final IndexWriter indexWriter = createIndexWriter(appName)) {
			indexWriter.addDocument(createLuceneHCube(key, cube));
		} catch (final IOException e) {
			throw new HCubeStoreException("Erreur lors de l'insertion d'un cube", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushBulk(final String appName, final Map<HKey, HCube> data, final String lasProcessKey) throws HCubeStoreException {
		Assertion.checkNotNull(data);
		//---------------------------------------------------------------------
		final List<Document> documents = new ArrayList<>();
		for (final Map.Entry<HKey, HCube> entry : data.entrySet()) {
			documents.add(createLuceneHCube(entry.getKey(), entry.getValue()));
		}
		try (final IndexWriter indexWriter = createIndexWriter(appName)) {
			indexWriter.addDocuments(documents);
			final Term lastDocumentIdTerm = new Term(LUCENE_SETTINGS, LUCENE_LAST_DOCUMENT_ID);
			indexWriter.updateDocument(lastDocumentIdTerm, createLuceneLastKey(lasProcessKey));
		} catch (final IOException e) {
			throw new HCubeStoreException("Erreur lors de l'insertion en bulk des cubes", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<HSerie> execute(final String appName, final String type, final HQuery query, final HSelector selector) {
		//the type is not used because it is already in the category of the query
		try(final IndexReader indexReader = createIndexSearcher(appName)){
			final boolean minInclusive=true;
			final boolean maxInclusive=true;
			final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			BooleanQuery booleanQuery = new BooleanQuery();
			Query categoryQuery = new TermQuery(new Term(LUCENE_DOCUMENT_CATEGORY,query.getCategorySelection().getPattern()));
			Query locationQuery = new TermQuery(new Term(LUCENE_DOCUMENT_LOCATION,query.getLocationSelection().getPattern()));
			Query dateQuery = NumericRangeQuery.newLongRange(LUCENE_DOCUMENT_DATE, query.getTimeSelection().getMinTime().inMillis()/60000, query.getTimeSelection().getMaxTime().inMillis()/60000, minInclusive, maxInclusive);
			booleanQuery.add(categoryQuery, Occur.MUST);
			booleanQuery.add(locationQuery, Occur.MUST);
			booleanQuery.add(dateQuery, Occur.MUST);
			final TopScoreDocCollector collector = TopScoreDocCollector.create(2000);
			indexSearcher.search(booleanQuery, collector);
			final ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HCubeStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long size(final String appName, final String type) throws HCubeStoreException {
		try (final IndexReader indexReader = DirectoryReader.open(getAppDirectory(appName))) {
			final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			final TotalHitCountCollector collector = new TotalHitCountCollector();
			indexSearcher.search(new QueryParser(LUCENE_SETTINGS, analyzer).parse("*:*"), collector);
		} catch (IOException | ParseException e) {
			throw new HCubeStoreException("Erreur lors de l'insertion en bulk des cubes", e);
		}

		return 0;
	}

	@Override
	public String getLastCubeKey(final String appName) throws HCubeStoreException {
		try (final IndexReader indexReader = DirectoryReader.open(getAppDirectory(appName))) {
			final int expectedNumberOfResults = 1;
			final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			final TopScoreDocCollector collector = TopScoreDocCollector.create(expectedNumberOfResults + 1);
			indexSearcher.search(new QueryParser(LUCENE_SETTINGS, analyzer).parse(LUCENE_LAST_DOCUMENT_ID), collector);
			Assertion.checkState(collector.getTotalHits() == expectedNumberOfResults, "Erreur lors de recheche de la clé du dernier cube inserer. " + collector.getTotalHits() + " resultats ont été trouvés quand " + expectedNumberOfResults + " sont attendu");
			final ScoreDoc[] hits = collector.topDocs().scoreDocs;
			final Document lastCubeKey = indexSearcher.doc(hits[0].doc);
			return lastCubeKey.get(LUCENE_SETTINGS_VALUE);
		} catch (final IndexNotFoundException e) {
			return null;
		} catch (IOException | ParseException e) {
			throw new HCubeStoreException("Erreur lors de l'insertion en bulk des cubes", e);
		}
	}

	private IndexWriter createIndexWriter(final String appName) throws IOException, HCubeStoreException {
		final IndexWriterConfig config = new IndexWriterConfig(analyzer);
		return new IndexWriter(getAppDirectory(appName), config);
	}

	private IndexReader createIndexSearcher(final String appName)throws IOException,HCubeStoreException{
		return DirectoryReader.open(getAppDirectory(appName));
	}
	
	private Directory getAppDirectory(final String appName) throws HCubeStoreException {
		Assertion.checkArgNotEmpty(appName);
		if (!appNames.contains(appName)) {
			appNames.add(appName);
			final Path appPath = dbPath.resolve(appName);
			try {
				// it isn't necessary to create the folder (its done in FSDirectory's constructor)
				dbPaths.put(appName, FSDirectory.open(dbPath.resolve(appName)));
			} catch (final IOException e) {
				throw new HCubeStoreException("Erreur lors de la tentative de creation du repertoire lucene " + appPath.toString(), e);
			}
		}
		return dbPaths.get(appName);
	}

	private Document createLuceneLastKey(final String lastDocumentKey) {
		final Document doc = new Document();
		doc.add(new StringField(LUCENE_SETTINGS, LUCENE_LAST_DOCUMENT_ID, Field.Store.NO));
		doc.add(new StringField(LUCENE_SETTINGS_VALUE, lastDocumentKey, Field.Store.YES));
		return doc;
	}

	/**
	 * Fonction permetant de convertire un HCube et sa clé HKey dans un document Lucene
	 * */
	private Document createLuceneHCube(final HKey key, final HCube cube) {
		final Document doc = new Document();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(key.getTime().inMillis());
		doc.add(new LongField(LUCENE_DOCUMENT_DATE, key.getTime().inMillis()/60000, Field.Store.NO));
		doc.add(new IntField(HTimeDimension.Year.getLabel(), calendar.get(Calendar.YEAR), Field.Store.NO));
		doc.add(new IntField(HTimeDimension.Month.getLabel(), calendar.get(Calendar.MONTH), Field.Store.NO));
		doc.add(new IntField(HTimeDimension.Day.getLabel(), calendar.get(Calendar.DAY_OF_MONTH), Field.Store.NO));
		doc.add(new IntField(HTimeDimension.Hour.getLabel(), calendar.get(Calendar.HOUR_OF_DAY), Field.Store.NO));
		doc.add(new IntField(HTimeDimension.SixMinutes.getLabel(), calendar.get(Calendar.MINUTE) / 6, Field.Store.NO));
		doc.add(new IntField(HTimeDimension.Minute.getLabel(), calendar.get(Calendar.MINUTE) % 6, Field.Store.NO));
		doc.add(new StringField(LUCENE_DOCUMENT_CATEGORY, StringUtils.join(key.getCategory().getCategoryTerms(), DELIMITER), Field.Store.NO));
		doc.add(new StringField(LUCENE_DOCUMENT_LOCATION, StringUtils.join(key.getLocation().getlocationTerms(), DELIMITER), Field.Store.NO));
		doc.add(new StringField(LUCENE_DOCUMENT_CUBE, gson.toJson(cube), Field.Store.YES));
		return doc;
	}

}
