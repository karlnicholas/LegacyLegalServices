
package statutesca.code;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import api.gsearch.load.LuceneCodeModel;
import api.gsearch.load.LuceneSectionModel;
import api.gsearch.util.FacetUtils;
import parser.ParserInterface;
import statutes.*;

public class CASaveLucene {
	private static final Logger logger = Logger.getLogger(CASaveLucene.class.getName());
	private static final String DEBUGFILE = null;	//"fam";
//	private static long globalsectioncount = 0;
	private IndexWriter indexWriter;
	private TaxonomyWriter taxoWriter;
    private FacetsConfig facetsConfig;
//    private StatutesTitles[] statutesTitles;
	
	private int nDocsAdded;
    private int nFacetsAdded;
    
    private int position;
    
    /**
     * This path happens in two places. The first creates hybrid XML files, the second
     *  load up the lucene indexes.
     */

    /**
     * This part loads Lucene Indexes
     * @throws IOException 
     */
	public void loadCode(Path codesDir, Path index, Path indextaxo) throws IOException {
		Date start = new Date();
		CALoadStatutes parserInterface = new CALoadStatutes();
//		statutesTitles = parserInterface.getStatutesTitles(); 
		logger.info("Indexing to directory 'index'...");

		Directory indexDir = FSDirectory.open(index);
		Directory taxoDir = FSDirectory.open(indextaxo);

//		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);		
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
//	    facetsConfig.setRequireDimCount(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, true);
/*	    
		FacetIndexingParams fip = new FacetIndexingParams(new CategoryListParams() {
			@Override
			public OrdinalPolicy getOrdinalPolicy(String dim) {
				// NO_PARENTS also works:
				return OrdinalPolicy.ALL_PARENTS;
			}
		});

		facetFields = new FacetFields(taxoWriter, fip);
*/
	    nDocsAdded = 0;
	    nFacetsAdded = 0;
/*
	    List<Path> files = new ArrayList<>(); 
		// dreams and aspirations
	    Files.newDirectoryStream(codesDir).forEach(pathname->{
			if ( Files.isDirectory(pathname) ) return;
			if (pathname.getFileName().toString().contains("constitution"))
				return;
			if ( DEBUGFILE != null ) { 
				if (!pathname.getFileName().toString().contains(DEBUGFILE)) return;
			}
			files.add(pathname);
	    });
*/
	    Files.list(codesDir)
        	.filter(Files::isRegularFile)
        	.filter(p->!p.getFileName().toString().contains("constitution"))
        	.filter(p->{ if ( DEBUGFILE != null ) return p.getFileName().toString().contains(DEBUGFILE); else return true; })
        	.forEach(p->{{
				logger.info("Processing " + p);
				try {
					processCodesFile(parserInterface, codesDir, p);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}});
		taxoWriter.commit();
		indexWriter.commit();

		taxoWriter.close();
		indexWriter.close();

		Date end = new Date();
		logger.info(end.getTime() - start.getTime() + " total milliseconds");
		logger.info("From " + "codes" + " " + nDocsAdded + ": Facets = " + nFacetsAdded);
	}

	private void processCodesFile(ParserInterface parserInterface, Path codesDir, Path path) throws IOException {
		StatutesParser parser = new StatutesParser();
		StatutesRoot r = parser.parse(parserInterface, StandardCharsets.ISO_8859_1, path);
		String abvr = path.getFileName().toString().substring(0, path.getFileName().toString().indexOf('_'));
		position = 1;
/*
		// debug code
		StatutesBaseClass reference = c.findReference(new SectionNumber("2150"));
		processReference( reference, codesdir.getPath() + "/" + abvr );
		// debug code
*/		
		iterateReferences( r.getReferences(), codesDir.toAbsolutePath() + "/" + abvr);		
	}
	
	private void iterateReferences( List<StatutesBaseClass> references, String basepath) throws IOException {
		// Iterator over sections ..
		for ( StatutesBaseClass reference: references ) { 
        	// keep going until we get into a section
        	if ( reference.getReferences() != null ) iterateReferences(reference.getReferences(), basepath);
        	processReference( reference, basepath);
        }
	}
	
	/*
	 * I need to save the title, the categorypath, the full path for reference, the text, the part and partnumber
	 * and of course the section and sectionParagraph if it exists
	 */
	private void processReference( StatutesBaseClass statutesBaseClass, String basepath) throws IOException  {
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
			String strPath = new String(basepath + "\\" + subdir + "\\" + strRange);
			Path codeDetail = Paths.get(strPath);
			ArrayList<String>sections = SectionParser.parseSectionFile(StandardCharsets.ISO_8859_1, codeDetail, statutesBaseClass); //  parseParagraph( codeDetail, model );
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

/*		
		CodeRange range = reference.getCodeRange();
		String codeRange; 
		if ( range == null ) codeRange = "";
		else if ( range.geteNumber() != null && range.geteNumber().getSectionNumber() != null ) codeRange = range.getsNumber().getSectionNumber() + " - " + range.geteNumber().getSectionNumber();
		else if ( range.getsNumber() != null && range.getsNumber().getSectionNumber() != null ) codeRange = range.getsNumber().getSectionNumber();
		else codeRange = "";
*/		

		
		String fullFacet = baseClass.getFullFacet();
		String[] facetPath = FacetUtils.fromString(fullFacet);
		
//		String[] facetPath = FacetUtils.getFullFacet(facetHead, baseClass);
//		FacetUtils.getFullFacet(facetHead, baseClass);
//		String path = FacetUtils.toString(facetPath);
		
		doc.add(new StringField("path", fullFacet, Field.Store.YES));
		

//		doc.add(new StringField("part", part , Field.Store.YES));

//		doc.add(new StringField("partnumber", partNumber, Field.Store.YES));

//		doc.add(new TextField("title", reference.getTitle(), Field.Store.YES));

		doc.add(new StringField("sectionnumber", sectionNumber, Field.Store.YES));

//		doc.add(new StringField("coderange", codeRange, Field.Store.YES));

		doc.add(new StringField("position", strPosition, Field.Store.YES));

		//		doc.add(new VecTextField("sectiontext", sectionText, Field.Store.YES));
		doc.add(new TextField("sectiontext", sectionText, Field.Store.YES));

		// obtain the sample facets for current document
		
//		String[] facetPath = FacetUtils.getFullFacet(facetHead, baseClass);
		
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
//			SectionNumberPosition PNumber = SectionParser.getSectionNumberPosition(position, sect);
			SectionNumber sNumber = SectionParser.getSectionNumber(position, sect);
			SectionNumberPosition PNumber = new SectionNumberPosition(position, sNumber.getSectionNumber());
			position++;
			LuceneSectionModel sectionModel = new LuceneSectionModel( PNumber, sect );
			model.getSections().add(sectionModel);
		}
	}

	public static void main(String... args) throws IOException  {

		// For gscalifornia
		Path codesDir = Paths.get("c:/users/karln/code");

		// For gsvirginia
//		Path codesDir = new Path("c:/users/karl/vacode.json.zip");

		CASaveLucene loader = new CASaveLucene();
		
//		LoadInterface cloader = new CALoad();
		
//		Path xmlcodes = new Path("c:/users/karl/op/GuidedSearch/gs/src/main/resources/xmlcodes");
		Path index = Paths.get("c:/users/karln/opca/GuidedSearchRs/src/main/resources/index/");
		Path indextaxo = Paths.get("c:/users/karln/opca/GuidedSearchRs/src/main/resources/indextaxo/");
		
//		loader.createXMLCodes(codesDir, xmlcodes );
		loader.loadCode(codesDir, index, indextaxo);
	}
    
}
