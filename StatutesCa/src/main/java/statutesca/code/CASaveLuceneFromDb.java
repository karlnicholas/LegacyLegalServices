
package statutesca.code;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import gsearch.load.LuceneCodeModel;
import gsearch.load.LuceneSectionModel;
import gsearch.util.FacetUtils;
import statutes.*;
import statutes.api.IStatutesApi;
import statutesca.statutesapi.CAStatutesApiImpl;

public class CASaveLuceneFromDb extends CASaveStatutesFromDb {
	private static final Logger logger = Logger.getLogger(CASaveLucene.class.getName());
	private static final String DEBUGFILE = null;	//"fam";
	private IndexWriter indexWriter;
	private TaxonomyWriter taxoWriter;
    private FacetsConfig facetsConfig;

    private final Path index;
    private final Path indextaxo;
	
	private int nDocsAdded;
    private int nFacetsAdded;    
    private int position;

    public static void main(String... args) throws SQLException, IOException  {

		new CASaveLuceneFromDb().loadCode();
	}
    
    
    public CASaveLuceneFromDb() throws SQLException {
		super();
		// For gscalifornia
		index = Paths.get("c:/users/karln/opcastorage/index/");
		indextaxo = Paths.get("c:/users/karln/opcastorage/indextaxo/");
		
	}

	/**
     * This part loads Lucene Indexes
     * @throws IOException 
	 * @throws SQLException 
     */
	public void loadCode() throws IOException, SQLException {
		Date start = new Date();
		CAStatutesApiImpl parserInterface = new CAStatutesApiImpl();
		logger.info("Indexing to directory 'index'...");

		Directory indexDir = FSDirectory.open(index);
		Directory taxoDir = FSDirectory.open(indextaxo);

		Analyzer analyzer = new EnglishAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		// Create a new index in the directory, removing any
		// previously indexed documents:
		iwc.setOpenMode(OpenMode.CREATE);
	    // create and open an index writer
	    indexWriter = new IndexWriter(indexDir, iwc);
	    // create and open a taxonomy writer
	    taxoWriter = new DirectoryTaxonomyWriter(taxoDir, OpenMode.CREATE);
	    
	    facetsConfig = new FacetsConfig();
	    facetsConfig.setHierarchical(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, true);
	    nDocsAdded = 0;
	    nFacetsAdded = 0;
		List<LawCode> lawCodes = retrieveLawCodes();
		for ( LawCode lawCode: lawCodes) {
			if ( lawCode.getCode().equals("CONS")) {
				continue;
			}
			try {
				StatutesRoot statutesRoot = parseLawCode(lawCode.getCode());
				processStatutesRoot(statutesRoot);
			} catch ( Exception ex) {
				System.out.println(lawCode.getCode());
				throw ex;
			}
		}
		taxoWriter.commit();
		indexWriter.commit();

		taxoWriter.close();
		indexWriter.close();

		Date end = new Date();
		logger.info(end.getTime() - start.getTime() + " total milliseconds");
		logger.info("From " + "codes" + " " + nDocsAdded + ": Facets = " + nFacetsAdded);
	}

	private void processStatutesRoot(StatutesRoot r) throws IOException {
		position = 1;
		iterateReferences( r.getReferences());		
	}
	
	private void iterateReferences( List<StatutesBaseClass> references) throws IOException {
		// Iterator over sections ..
		for ( StatutesBaseClass reference: references ) { 
        	// keep going until we get into a section
			// presumably a leaf
        	if ( reference.getReferences() != null ) iterateReferences(reference.getReferences());
        	processReference( reference);
        }
	}
	/*
	 * I need to save the title, the categorypath, the full path for reference, the text, the part and partnumber
	 * and of course the section and sectionParagraph if it exists
	 */
	private void processReference( StatutesBaseClass statutesBaseClass) throws IOException  {
		LuceneCodeModel model = new LuceneCodeModel( statutesBaseClass );
		
//		String facetHead = FacetUtils.getFacetHeadFromRoot(statutesTitles, statutesBaseClass.getStatutesRoot());
		
//		SectionRange range = reference.getSectionRange();
//		if (range != null) {
		StatutesLeaf section = statutesBaseClass.getStatutesLeaf();
		if (section != null) {
			StatuteRange range = statutesBaseClass.getStatuteRange();
			String strRange = range.getsNumber().getSectionNumber();
			String firstInt = new String();
			for (int i = 0, il = strRange.length(); i < il; ++i) {
				char ch = strRange.charAt(i);
				if (Character.isDigit(ch)) {
					firstInt = firstInt.concat("" + ch);
				} else {
					break;
				}
			}
			int num = Integer.parseInt(firstInt);
			num = ((num - 1) / 1000) * 1000;
			String subdir = String.format("%05d-%05d", num + 1, num + 1000);
			if ( range.geteNumber() != null && range.geteNumber().getSectionNumber() != null ) strRange = strRange + "-" + range.geteNumber();

//			ArrayList<String>sections = SectionParser.parseSectionFile(StandardCharsets.ISO_8859_1, codeDetail, statutesBaseClass); //  parseParagraph( codeDetail, model );

			
			int idx = Collections.binarySearch(lawSections, new LawSection(lawToc));
			if ( idx <  0) { 
				throw new RuntimeException("LawSection not found for LawToc: " + lawToc);
			}
			LawSection lawSection = lawSections.get(idx);
			
			
			getSectionsFromDatabase()
			// not sure this is needed here .. but why not
			range.getsNumber().setPosition(position);
			parseSectionModels(sections, model);
			// not sure this is needed here .. but why not
			range.geteNumber().setPosition(position-1);
		}
		
		if ( model.getSections().size() == 0 ) {
			writeDocument(statutesBaseClass, new LuceneSectionModel(new SectionNumberPosition(-1, ""), "") );
		} else {
			for ( LuceneSectionModel sectionModel: model.getSections() ) {
				writeDocument(statutesBaseClass, sectionModel);
			}
		}
	}
	
	private void writeDocument(StatutesBaseClass baseClass, LuceneSectionModel sectionModel) throws IOException {

		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		
		String sectionNumber = sectionModel.getSectionNumberPosition().toString();
		String sectionText = sectionModel.getSectionParagraph();
		String strPosition = Integer.toString( sectionModel.getSectionNumberPosition().getPosition() );

		String part = baseClass.getPart();
		if ( part == null ) part = "";

		String partNumber = baseClass.getPartNumber();
		if ( partNumber == null ) partNumber = "";
		
		String fullFacet = baseClass.getFullFacet();
		String[] facetPath = FacetUtils.fromString(fullFacet);
		
		doc.add(new StringField("path", fullFacet, Field.Store.YES));
		doc.add(new StringField("sectionnumber", sectionNumber, Field.Store.YES));
		doc.add(new StringField("position", strPosition, Field.Store.YES));
		doc.add(new TextField("sectiontext", sectionText, Field.Store.YES));
		// invoke the category document builder for adding categories to the document and,
		// as required, to the taxonomy index 
		FacetField facetField = new FacetField( 
				FacetsConfig.DEFAULT_INDEX_FIELD_NAME, 
				facetPath 
			);

		doc.add( facetField );
		
		// finally add the document to the index
		indexWriter.addDocument(facetsConfig.build(taxoWriter, doc));
		
		nDocsAdded++;
		nFacetsAdded += facetPath.length; 
		
	}

	private void parseSectionModels(ArrayList<String> sections, LuceneCodeModel model) {
		for ( String sect: sections ) {
			SectionNumberPosition PNumber = new SectionNumberPosition(position, sNumber.getSectionNumber());
			position++;
			LuceneSectionModel sectionModel = new LuceneSectionModel( PNumber, sect );
			model.getSections().add(sectionModel);
		}
	}

}
