package api.gsearch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;

import api.gsearch.util.FacetUtils;
import api.gsearch.viewmodel.*;
import api.gsearch.viewmodel.ViewModel.STATES;
import parser.ParserInterface;
import statutes.SectionNumber;
import statutes.SectionNumberPosition;
import statutes.StatutesBaseClass;
import statutes.StatutesRoot;
import statutesrs.StatutesHierarchy;

/*
 * Changes to match spec and make thread safe .. 
 */
public class GSearch {
	private static final Logger logger = Logger.getLogger(GSearch.class.getName());
	
	// Application scoped variables ..
	
	// The parsed statute hierarchy
//	private StatutesTitles[] statutesTitles; 
	private IndexReader indexReader;
	private TaxonomyReader taxoReader;
	private IndexSearcher searcher;
	private FacetsConfig facetsConfig;
	private Analyzer analyzer;
	private StandardQueryParser parser;
	private int maxTopDocs;
	private ParserInterface parserInterface;


	// This is meant to be put into an application scope
	// after instantiation .. 
	public GSearch(ParserInterface parserInterface, Path index, Path indexTaxo) throws IOException {
//		statutesTitles = parserInterface.getStatutesTitles();
		this.parserInterface = parserInterface;
		indexReader = DirectoryReader.open( FSDirectory.open(index));
        taxoReader = new DirectoryTaxonomyReader(FSDirectory.open(indexTaxo));
        searcher = new IndexSearcher(indexReader);
        facetsConfig = new FacetsConfig();
	    facetsConfig.setHierarchical(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, true);
//	    facetsConfig.setRequireDimCount(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, true);
	    analyzer = new EnglishAnalyzer();
	    parser = new StandardQueryParser(analyzer);
	    maxTopDocs = 1000;
	}
	
	public void destroy() throws IOException {
		indexReader.close();
		taxoReader.close();
	}

/*		
	public boolean viewModelIsTerminatingSubcode(viewModel viewModel) {
		return viewModel.pathList.get(viewModel.pathList.size()-1).section;
		List<EntryReference> entryList = viewModel.codeList;
		while ( entryList != null  ) {
			if ( entryList.size() == 0 ) return false;
			EntryReference eRef = entryList.get(0);
			if ( eRef instanceof CodeEntry ) {
				if ( entryList.size() > 1 ) return false;				
			} else if ( eRef instanceof SubcodeEntry ) {
				if ( entryList.size() > 1 ) return false;				
			} else if ( eRef instanceof TextEntry ) {
				return true;				
			}
		}
		return false;
    }
*/		
	
	public ViewModel handleRequest( String path, String term, boolean highlights) throws IOException {
		logger.fine("get:" + path + ":" + term + ":" + highlights );
		ViewModel viewModel = new ViewModel(path, term, highlights, null);
		
		// set state
		viewModel.setState(STATES.START);
		if ( !viewModel.getPath().isEmpty() ) viewModel.setState(STATES.BROWSE);	// path overrides select

		// This section is building a list of the codes available .. 
		// it should be called whenever there is no path, at least
		// it can also, presumably, be built if the codeselect 
		// 
		if ( viewModel.getState() == STATES.START ) {
			List<StatutesRoot> statutesRootList = parserInterface.getStatutes();
			for ( int i=0, l=statutesRootList.size(); i<l; ++i ) {
				StatutesRoot statutesRoot = (StatutesRoot) statutesRootList.get(i);
//				String facetHead = FacetUtils.getFacetHeadFromRoot(statutesTitles, statutesRoot);
				StatuteEntry cEntry = new StatuteEntry( statutesRoot);
				cEntry.setPathPart(false);
		    	viewModel.getEntries().add( cEntry );
			}
		} else {
			processPathAndSubcodeList( viewModel );			
		}
		
		if ( viewModel.getState() == STATES.TERMINATE || viewModel.isFragments() || !viewModel.getTerm().isEmpty() ) { 
			processTerm(viewModel);
		}
		// return the processing results

		return viewModel;
	}

	private void processPathAndSubcodeList( ViewModel viewModel ) {
		// at this point, only exhange.path is filled out ..
		StatutesHierarchy rwr = parserInterface.getStatutesHierarchy(viewModel.getPath());

		List<StatutesBaseClass> subPaths = rwr.getStatutesPath();

		StatutesRoot statutesRoot = (StatutesRoot)subPaths.remove(0);
//		String facetHead = statutesRoot.getFacetHead();
//		viewModel.setFacetHead(facetHead);
		EntryReference entryReference = new StatuteEntry(statutesRoot);
		viewModel.getEntries().add( entryReference );
		List<EntryReference> entries = entryReference.getEntries();
		StatutesBaseClass parent = statutesRoot;
		
		for (StatutesBaseClass baseClass: subPaths ) {
			// check terminating
			baseClass.setParent(parent);
			parent = baseClass; 
			entryReference = new SubcodeEntry(baseClass);
			entries.add(entryReference);
			entries = entryReference.getEntries();
			subPaths = baseClass.getReferences();
			// check terminating
			if ( baseClass.getStatutesLeaf() != null ) {
				viewModel.setState(STATES.TERMINATE);
			}
		}
    	for ( StatutesBaseClass reference: rwr.getFinalReferences() ) {
    		reference.setParent(parent);
    		parent.addReference(reference);
    		SubcodeEntry subcode = new SubcodeEntry( reference);
    		subcode.setPathPart(false);
    		entries.add( subcode );
	    }
	}
//	StatutesRoot statutesRoot = FacetUtils.findStatuteFromFacet(statutesWS, statutesTitles, viewModel.getPath());
/*		
	String fullTitle = FacetUtils.findFullTitleFromFacet(statutesTitles, viewModel.getPath());
	
	ReferencesWithReferences statutesRootWR = statutesWS.returnReferenceByTitle(fullTitle);
	StatutesRoot statutesRoot = statutesRootWR.getStatutesRoot();
	for ( Object reference: statutesRootWR.getReferences() ) {
		statutesRoot.addReference((StatutesBaseClass) reference);
	}

	String facetHead = FacetUtils.getFacetHeadFromRoot(statutesTitles, statutesRoot);
	viewModel.setFacetHead(facetHead);
	EntryReference entryReference = new StatuteEntry(statutesRoot, facetHead);
	viewModel.getEntries().add( entryReference );
	List<EntryReference> entries = entryReference.getEntries();

	List<StatutesBaseClass> subPaths = statutesRoot.getReferences();

	// ok .. now we are building parent paths ..
	StringTokenizer tokenizer = new StringTokenizer(viewModel.getPath(), String.valueOf(FacetUtils.DELIMITER) );
	// burn the first token
	String token = tokenizer.nextToken();
	while ( tokenizer.hasMoreTokens() ) {
		token = tokenizer.nextToken();
		for (StatutesBaseClass baseClass: subPaths ) {
			String subPart = FacetUtils.getBaseClassFacet(facetHead, baseClass);
			if ( subPart.equals(token) ) {
				entryReference = new SubcodeEntry(baseClass, facetHead);
				entries.add(entryReference);
				entries = entryReference.getEntries();
				subPaths = baseClass.getReferences();
				// check terminating
				if ( baseClass.getStatutesLeaf() != null ) {
					viewModel.setState(STATES.TERMINATE);
					termSection = baseClass.getStatutesLeaf();
				}
				break;	// out of for loop
			}
		}
	}
    if ( subPaths != null ) {
    	for ( StatutesBaseClass reference: subPaths ) {
    		SubcodeEntry subcode = new SubcodeEntry( reference, facetHead);
    		subcode.setPathPart(false);
    		entries.add( subcode );
	    }
    }
*/	    
	private void processTerm( ViewModel viewModel) throws IOException {
	    // is this really the best way???
	    // or .. let's see if this works 

		Query query = makeQuery(viewModel);
		
//		FacetSearchParams facetSearchParams = null;
		DrillDownQuery ddQuery;
		FacetsCollector facetsCollector = null;
		TopScoreDocCollector topScoreDocCollector = null;
		Collector collector = null;
		String[] catPath = null;
		int totalCount = 0;
		
    	// need to have the current list of entries that will be displayed
		List<EntryReference> entries = viewModel.getEntries();
		while ( entries.size() != 0 && entries.get(0).isPathPart() ) {
			entries = entries.get(0).getEntries();
		}
		
		// 2
		if ( viewModel.getState() == STATES.START ) {
			// search for all root level facets
			// CODE  -- facet and category : combined
			// Create facet search parameter object and put the path(s) into it
			ddQuery = new DrillDownQuery(facetsConfig, query);
			
			for ( EntryReference eRef: entries ) {
				ddQuery.add(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, eRef.getFullFacet());
			}
		} 
		// 3 or 4
		else if ( viewModel.getState() == STATES.BROWSE) {
			catPath = FacetUtils.fromString(viewModel.getPath());
		    DrillDownQuery drillDownQuery = new DrillDownQuery(facetsConfig, query);
		    drillDownQuery.add(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, catPath);
		    query = drillDownQuery; 

		}
		// 5
		else if ( viewModel.getState() == STATES.TERMINATE || viewModel.isFragments() ) {
			// actual searching of text fields .. 
			// again, drill down query .. 
			catPath = FacetUtils.fromString(viewModel.getPath());
		    DrillDownQuery drillDownQuery = new DrillDownQuery(facetsConfig, query);
		    drillDownQuery.add(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, catPath);
		    query = drillDownQuery; 
		}

		// 2
		if ( viewModel.getState() == STATES.START) {
			// collect facts .. 
			facetsCollector = new FacetsCollector(); 
			// maybe wrap a topScoreDocCollector ..
			if ( viewModel.isFragments() ) {
				topScoreDocCollector = TopScoreDocCollector.create(maxTopDocs);
				collector = MultiCollector.wrap( topScoreDocCollector, facetsCollector );
			} else {
				collector = facetsCollector;
			}
		}
		//3 and 4
		else if ( viewModel.getState() == STATES.BROWSE ) {
			// collect facts .. 
			facetsCollector = new FacetsCollector(); 
			// maybe wrap a topScoreDocCollector ..
			if ( viewModel.isFragments() ) {
				topScoreDocCollector = TopScoreDocCollector.create(maxTopDocs);
				collector = MultiCollector.wrap( topScoreDocCollector, facetsCollector );
			} else {
				collector = facetsCollector;				
			}
		}
		// 5
		else if ( viewModel.getState() == STATES.TERMINATE || viewModel.isFragments() ) {
			collector = topScoreDocCollector = TopScoreDocCollector.create(maxTopDocs);
		}
		
    	searcher.search(query, collector);
    	
    	// 2
    	if ( viewModel.getState() == STATES.START ) {
	    	// Here have used multiple CategoryPaths, so
	    	// ignore the subresults and get the various first level facet results
    	    Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, facetsCollector);
    	    FacetResult result = facets.getTopChildren(maxTopDocs, FacetsConfig.DEFAULT_INDEX_FIELD_NAME );
    	    
    	    if ( result != null ) {
		    	for ( LabelAndValue labelAndValue: result.labelValues ) {
	    	    	for ( EntryReference eRef: entries ) {
	    	    		if ( eRef.getFullFacet().equals(labelAndValue.label) ) {
	    	    			int count = labelAndValue.value.intValue();
	    	    			totalCount += count;
	    	    			eRef.setCount( count );
	    	    			break;
	    	    		}
	    	    	}
		    	}
    	    }
    	}
    	// 3 or 4 
    	else if ( viewModel.getState() == STATES.BROWSE) {
        	// using the path, so only one CategoryPath ... (with some number of sub-results)
    	    Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, facetsCollector);
    	    FacetResult result = facets.getTopChildren(maxTopDocs, FacetsConfig.DEFAULT_INDEX_FIELD_NAME, catPath );
    		
	    	// specific path, so get the result
    	    if ( result != null ) {
	    	    String pathStart = FacetUtils.toString(result.path) + FacetUtils.DELIMITER;
		    	for ( LabelAndValue labelAndValue: result.labelValues ) {
	    	    	for ( EntryReference eRef: entries ) {
	    	    		if ( eRef.getFullFacet().equals( pathStart + labelAndValue.label ) ) {
	    	    			int count = labelAndValue.value.intValue();
	    	    			totalCount += count;
	    	    			eRef.setCount( count );
	    	    			break;
	    	    		}
	    	    	}
	    	    }
    	    }
    	}
    	// 5 or highlights
    	if ( viewModel.getState() == STATES.TERMINATE || viewModel.isFragments() ) {
    		
	    	TopDocs docResults = topScoreDocCollector.topDocs();
	        ScoreDoc[] hits = docResults.scoreDocs;
	
	        int numTotalHits = docResults.totalHits;

	        int start = 0;
	        int end = Math.min(numTotalHits, maxTopDocs);
	        if ( end < numTotalHits ) totalCount = -maxTopDocs;
	        else totalCount = end;

	        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
	        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
	        highlighter.setTextFragmenter(new GSearchFragmenter() );
	    	
	    	// create a temporary holding list of sectionText entries
	    	ArrayList<SectionText> sectionTextList = new ArrayList<SectionText>();
	    	
	    	for (int i = start; i < end; i++) {
	
	            int docId = hits[i].doc;
	            Document doc = searcher.doc(docId);
	            
	            IndexableField textField = doc.getField("sectiontext");
	            SectionNumberPosition sectionNumber = new SectionNumberPosition( 
	            		Integer.parseInt(doc.getField("position").stringValue()), 
	            		doc.getField("sectionnumber").stringValue() 
	            	);
	            String docFacet = doc.getField("path").stringValue();

	            String text = textField.stringValue();      

	            if ( viewModel.isFragments() ) { 
	            	// getTokenStream(String field, Fields tvFields, String text, Analyzer analyzer, int maxStartOffset)
	            	// Get a token stream from either un-inverting a term vector if possible, or by analyzing the text.
	            	//IndexReader.getTermVectors(int docId)
//	    	    	TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), docId, "sectiontext", analyzer);
	    	    	TokenStream tokenStream = TokenSources.getTokenStream("sectiontext", indexReader.getTermVectors(docId), text, analyzer,  highlighter.getMaxDocCharsToAnalyze() - 1);
	            	TextFragment[] frag = null;
	    			try {
	    				frag = highlighter.getBestTextFragments(tokenStream, text, false, 1);
	    			} catch (InvalidTokenOffsetsException e) {
	    				throw new RuntimeException( e );
	    			}
	    			for (int j = 0; j < frag.length; j++) {
	    				if ((frag[j] != null) && (frag[j].getScore() > 0)) {

	    					sectionTextList.add( new SectionText(
								sectionNumber.getSectionNumber(), 
								sectionNumber.getPosition(), 
    							sectionNumber.toString() + " ... " + frag[j].toString() + " ... "
    							,docFacet
    							) 
    						);
	    					break;
//	    	                  System.out.println((frag[j].toString()));
	    				}
	    			}
	            } else {
	            	sectionTextList.add( 
            			new SectionText(
							sectionNumber.getSectionNumber(), 
							sectionNumber.getPosition(), 
	            			text
	            			, docFacet
	            		) 
	            	);
	            }
	    	}
	        // so .. getting last one .. getting section .. getting sectionNumbers .. iterating results ..
	        // this part fills in the missing sectionTexts if there was a search term used
	        // don't need to do this if the term is empty ..
	        // though, you might want to do it anyway so as to fall through to the sort call
	    	if ( sectionTextList != null || !viewModel.getPath().isEmpty() ) {
//		        Section section = viewModel.terminatingSubcode();
	    		// not called in a loop, so, suffer through it once ...
	    		StatutesBaseClass termSection = viewModel.findPathEnd();
	    		if ( termSection != null && termSection.getStatutesLeaf() != null ) {
//	    			String fullFacet = FacetUtils.toString(FacetUtils.getFullFacet(viewModel.getFacetHead(), termSection));
	    			//TODO: This has changed
	    			String fullFacet = termSection.getFullFacet();
		        	for (SectionNumber sectionNumber : termSection.getStatutesLeaf().getSectionNumbers() ) {
		        		int sectionNumberPosition = sectionNumber.getPosition();
			        	boolean found = false;
		        		for (SectionText sectionText: sectionTextList ) {
		        			// searching for a specific entry, so don't do lexical comparisons
        					if ( sectionText.getPosition() == sectionNumberPosition ) {
				        		found = true;
				        		break;
				        	}
			        	}
			        	if ( !found ) {
			        		sectionTextList.add(
			        			new SectionText(
									sectionNumber.getSectionNumber(),
									sectionNumberPosition, 
									sectionNumberPosition + ". NO TERMS FOUND."
									, fullFacet
								)
	        				);
			        	}
			        }
		        	// need to get the list from the section 
		        	// and sort if by that ... 
		        }
	        }

	        Collections.sort(sectionTextList);
			List<EntryReference> entries1 = viewModel.getEntries();

			// Okay, these get "placed" because when there is a search term 
			// and fragments are highlighted then the results
			// have to be woven into the statute tree so that
			// the expand tree to see highlights functionality works.
	        for ( SectionText textEntry: sectionTextList ) {
				boolean placed = putTextEntry(entries1, textEntry);
				if ( !placed ) {
					throw new RuntimeException("TextEntry not placed.");
				}
	        }			
    	}
    	// update total counts
    	viewModel.setTotalCount(totalCount);
    }

	/**
	 * This is done so that fragments works. It weaves fragment results into the StatutesBaseClass hierarchy 
	 */
	private boolean putTextEntry(List<EntryReference> entries, SectionText textEntry) {
		boolean retVal = false;
		int textPosition = textEntry.getPosition();
		for ( EntryReference entryReference: entries ) {
			String facetText = textEntry.getFullFacet();
			String facetReference = entryReference.getFullFacet();
			if ( FacetUtils.facetMatch( facetText, facetReference ) ) {
				int posBegin = entryReference.getStatutesBaseClass().getStatuteRange().getsNumber().getPosition();
				int posEnd = entryReference.getStatutesBaseClass().getStatuteRange().geteNumber().getPosition();
				if ( textPosition >= posBegin && textPosition <= posEnd ) {
					List<EntryReference> nextEntries = entryReference.getEntries();
					if ( nextEntries.size() == 0 || nextEntries.get(0) instanceof SectionText ) {
						entryReference.getEntries().add(textEntry);
						return true;
					} else {
						retVal = putTextEntry(entryReference.getEntries(), textEntry);
					}
				}
			}
		}
		return retVal;
	}

	private Query makeQuery(ViewModel viewModel ) {
		Query q;
	    if ( viewModel.getTerm().isEmpty() ) {
	    	q = new MatchAllDocsQuery();
	    } else {
//	    	Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9);
//	    	QueryParser parser = new QueryParser(Version.LUCENE_4_9, "sectiontext", analyzer);
//	    	StandardQueryParser parser = new StandardQueryParser(new EnglishAnalyzer(Version.LUCENE_4_9));
	    	try {
	    		q = parser.parse(viewModel.getTerm(), "sectiontext");
	    	} catch (Throwable t) {
				// TODO Auto-generated catch block
				logger.severe("Parser error for term |"+viewModel.getTerm()+"| = " + t.getMessage());
				q = new MatchAllDocsQuery();
			}
	    }
		return q;
	}
	
}
