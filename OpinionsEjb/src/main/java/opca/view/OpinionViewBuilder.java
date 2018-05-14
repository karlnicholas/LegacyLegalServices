package opca.view;

import java.util.*;
import java.util.logging.Logger;

import service.Client;
import statutes.StatutesBaseClass;
import statutes.StatutesRoot;
import opca.model.*;
import opca.parser.ParsedOpinionCitationSet;

public class OpinionViewBuilder {
	private Logger logger = Logger.getLogger(OpinionViewBuilder.class.getName());
	private Client statutesRs; 

    public OpinionViewBuilder() {
    }

    public OpinionView buildSlipOpinionView(
    	Client statutesRs, 
		SlipOpinion slipOpinion, 
		ParsedOpinionCitationSet parserResults 
	) {
    	this.statutesRs = statutesRs; 
    	return buildOpinionView(slipOpinion, slipOpinion.getFileName(), parserResults);
    }

    private OpinionView buildOpinionView(
    	SlipOpinion slipOpinion,
		String name, 
		ParsedOpinionCitationSet parserResults
	) {
    	
        // statutes ws .. getStatuteKeys list to search for 
    	statutesrs.StatuteKeyArray statuteKeyArray = new statutesrs.StatuteKeyArray();
        for( StatuteCitation statuteCitation: slipOpinion.getOnlyStatuteCitations() ) {
            statutesrs.StatuteKey statuteKey = new statutesrs.StatuteKey();            
            statuteKey.setTitle(statuteCitation.getStatuteKey().getTitle());
            statuteKey.setSectionNumber(statuteCitation.getStatuteKey().getSectionNumber());
            statuteKeyArray.getItem().add(statuteKey);
        }
        // call statutesws to get details of statutes 
        statutesrs.ResponseArray responseArray = statutesRs.findStatutes(statuteKeyArray);
        List<StatuteView> statuteViews = createStatuteViews(slipOpinion, responseArray);
        // create a CaseView list.
    	ArrayList<CaseView> cases = new ArrayList<CaseView>();
    	for ( OpinionBase opinionBase: slipOpinion.getOpinionCitations() ) {
    		OpinionBase opcase = parserResults.findOpinion(opinionBase.getOpinionKey());
			CaseView caseView = new CaseView(opcase.getTitle(), opinionBase.getOpinionKey().toString(), opcase.getOpinionDate(), opcase.getCountReferringOpinions());
    		cases.add(caseView);
    	}
/*    	
    	long maxCited = 0;
		for ( CaseView c: cases ) {
			if ( c.getCountReferringOpinions() > maxCited )
				maxCited = c.getCountReferringOpinions();
		}	
		maxCited = (maxCited / 4)+1;
		for ( CaseView c: cases ) {
			c.setImportance((int)(c.getCountReferringOpinions()/maxCited)+1);
		}	
*/
        return new OpinionView(slipOpinion, name, statuteViews, cases);
    }


    private List<StatuteView> createStatuteViews(OpinionBase opinionBase, statutesrs.ResponseArray responseArray) {
        //
    	ArrayList<StatuteView> statuteViews = new ArrayList<StatuteView>();
        // copy results into the new list ..
        // Fill out the codeSections that these section are referencing ..
        // If possible ... 
        Iterator<OpinionStatuteCitation> itc = opinionBase.getStatuteCitations().iterator();
        
        while ( itc.hasNext() ) {
        	OpinionStatuteCitation citation = itc.next();
        	if ( citation.getStatuteCitation().getStatuteKey().getTitle() != null ) {
	            // This is a section
	            SectionView opSection = new SectionView(opinionBase, citation);
	            // here we look for the Doc Section within the Code Section Hierachary
	            // and place it within the sectionReference we previously parsed out of the opinion
	//                opSection.setCodeReference( codesInterface.findReference(opSection.getTitle(), opSection.getSectionNumber() ) );
	            opSection.setStatutesBaseClass(findStatutesBaseClass(responseArray, citation.getStatuteCitation().getStatuteKey()));
	//                Section codeSection = codeList.findCodeSection(sectionReference);
	            // We don't want to keep ones that we can't map .. so .. 
	            if ( opSection.getStatutesBaseClass() != null ) {
	                // First .. let's get the OpinionCode for this sectionReference
	                StatuteView opCode = findOrMakeOpinionCode(statuteViews, opSection); 
	                opCode.addNewSectionReference( opSection );
	            }
        	}
        }
        return statuteViews;
    }
    
    /**
     * Build out the StatutesBaseClass with parent pointers
     * @param responseArray
     * @param key
     * @return
     */
    private StatutesBaseClass findStatutesBaseClass(statutesrs.ResponseArray responseArray, StatuteKey key) {
		List<StatutesBaseClass> subPaths = null;
    	final String code = key.getTitle();
    	final String sectionNumber = key.getSectionNumber();
    	for ( statutesrs.ResponsePair responsePair: responseArray.getItem()) {
    		statutesrs.StatuteKey statuteKey = responsePair.getStatuteKey();
    		if ( code.equals(statuteKey.getTitle()) && sectionNumber.equals(statuteKey.getSectionNumber()) ) {
    			subPaths =  responsePair.getStatutesPath();
    			break;
    		}
    	}

		StatutesBaseClass returnBaseClass = null;
    	if ( subPaths != null ) {
			StatutesRoot statutesRoot = (StatutesRoot)subPaths.remove(0);
			StatutesBaseClass parent = statutesRoot;
			returnBaseClass = statutesRoot;
			for (StatutesBaseClass baseClass: subPaths ) {
				// check terminating
				baseClass.setParent(parent);
				parent = baseClass; 
				returnBaseClass = baseClass;
				subPaths = baseClass.getReferences();
			}
    	}
    	return returnBaseClass;
    }

    
    // here we want to go up the parent tree and until we get to the top Section
    // because that is where the "Code" starts ...
    // What we know is that the sectionReference cannot be at the 
    // Top of the Code.
    private StatuteView findOrMakeOpinionCode( ArrayList<StatuteView> codes, SectionView sectionReference) {
    	// First, find the section's top code
    	StatutesBaseClass statutesBaseClass = sectionReference.getStatutesBaseClass();
    	while ( statutesBaseClass.getParent() != null ) {
    		statutesBaseClass = statutesBaseClass.getParent();
    	}
    	// ok, our codeSection is the top section
    	Iterator<StatuteView> cit = codes.iterator();
    	while ( cit.hasNext() ) {
    		StatuteView opCode = cit.next();
//    		if ( opCode.getCodeReference().equals( code.getTitle())) return opCode;
    		if ( opCode.getStatutesBaseClass() == statutesBaseClass ) return opCode;
    	}
    	// else, construct one ...
    	StatuteView opCode = new StatuteView( statutesBaseClass, sectionReference.getStatutesBaseClass());
        codes.add(opCode);
    	return opCode;
    }
    

//    private CodesInterface codesInterface;
/*    
    public static void main(String[] args) throws Exception {

//        File file = new File("Cases/B231123.DOC");

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File directory, String name) {
                if ( DEBUGFILE != null ) if ( !name.contains(DEBUGFILE)) return false;
                if (name.contains("~")) return false;
                if (name.contains(".DOC")) {
                    return true;
                }
                return false;
            }
        };

        OpinionDocumentParser po = new OpinionDocumentParser();

        File casesDirectory = new File("c:/users/karln/workspace/opinionsreport/cases");
        File[] files = casesDirectory.listFiles(filter);
        for (int i=0; i<files.length; ++i ) {
            
            if ( DEBUG ) System.out.print( files[i].getName() );

            po.parseOpinionFile( files[i]);
//            ArrayList<OpinionSection> sectionReferences = po.parseOpinionFile( files[i], sp );
//            if ( DEBUG ) System.out.println( sectionReferences );
            
        }
        
    }    
    public void parseCitations(Case opinionSummary, InputStream inputStream, CodesInterface codesInterface) throws Exception {

    	codeTitles = codesInterface.getCodeTitles();

//        codes = new ArrayList<OpinionCode>();
        ArrayList<OpinionSection> sectionReferences = parseCase(inputStream);
        
//        logger.info(sectionReferences.toString());

        // Map to the Codes
        // and then compress the Section References into a Hierarchy
        // within the California Codes structure
        //

        opinionSummary.setDisposition( getSectionParser().getDisposition() );
        opinionSummary.setSummary( getSectionParser().getSummaryParagraph() );

        // copy results into the new list ..
        // Fill out the codeSections that these section are referencing ..
        // If possible ... 
        Iterator<OpinionSection>sri = sectionReferences.iterator();
        while ( sri.hasNext() ) {
            // This is a section
            OpinionSection opSection = sri.next();
            // here we look for the Doc Section within the Code Section Hierachary
            // and place it within the sectionReference we previously parsed out of the opinion
            String codeTitle = opSection.getTitle();
            if ( codeTitle != null ) {
                opSection.setCodeReference( codesInterface.findReference(codeTitle, opSection.getSectionNumber() ) );
            }
//            Section codeSection = codeList.findCodeSection(sectionReference);
            // We don't want to keep ones that we can't map .. so .. 
            if ( opSection.getCodeReference() != null ) {
            	// First .. let's get the OpinionCode for this sectionReference
            	OpinionCode topCode = findOrMakeOpinionCode(opSection); 

                topCode.addNewSectionReference( opSection );
            } else {
//                    System.out.println("Cannot find codeSection for " + sectionReference);
            }
//            System.out.println(this);
        }

    }

*/

	public void scoreSlipOpinionOpinions(
		OpinionView opinionView
	) {
		// need a collection StatutueCitations.
        for ( OpinionBase opinionCited: opinionView.getOpinionCitations() ) {
            // statutes ws .. getStatuteKeys list to search for 
        	statutesrs.StatuteKeyArray statuteKeyArray = new statutesrs.StatuteKeyArray();
            for( StatuteCitation statuteCitation: opinionCited.getOnlyStatuteCitations() ) {
                statutesrs.StatuteKey statuteKey = new statutesrs.StatuteKey();            
                statuteKey.setTitle(statuteCitation.getStatuteKey().getTitle());
                statuteKey.setSectionNumber(statuteCitation.getStatuteKey().getSectionNumber());
                statuteKeyArray.getItem().add(statuteKey);
            }
            // call statutesws to get details of statutes 
            statutesrs.ResponseArray responseArray = statutesRs.findStatutes(statuteKeyArray);
            List<StatuteView> statuteViews = createStatuteViews(opinionCited, responseArray);
            // do a ranking
            rankStatuteViews(statuteViews);
            // remove anything not in the slip opinion statutes
            List<StatuteView> slipStatuteViews = opinionView.getStatutes();
            Iterator<StatuteView> itsv = statuteViews.iterator();
            int score = 0;
            while ( itsv.hasNext() ) {
            	StatuteView statuteView = itsv.next();
            	int idx = slipStatuteViews.indexOf(statuteView);
            	if ( idx < 0 ) {
            		itsv.remove();
            		continue;
            	}
                // score the rest
            	score = score + ( statuteView.getImportance() + slipStatuteViews.get(idx).getImportance());
            }
            opinionView.findCaseView(opinionCited).setScore(score);
        }
    	long maxScore = 0;
		for ( CaseView c: opinionView.getCases() ) {
			if ( c.getScore() > maxScore )
				maxScore = c.getScore();
		}	
		double d = ((maxScore+1) / 4.0);
		for ( CaseView c: opinionView.getCases() ) {
			c.setImportance((int)(((double)c.getScore())/d)+1);
		}	
		Collections.sort(opinionView.getCases(), new Comparator<CaseView>() {
			@Override
			public int compare(CaseView o1, CaseView o2) {
				return o2.getImportance() - o1.getImportance();
			}
		});
	}
	public void scoreSlipOpinionStatutes(
		OpinionView opinionView
	) {
		rankStatuteViews(opinionView.getStatutes());
		Collections.sort(opinionView.getStatutes(), new Comparator<StatuteView>() {
			@Override
			public int compare(StatuteView o1, StatuteView o2) {
				return o2.getImportance() - o1.getImportance();
			}
		});
	}
	private void rankStatuteViews(List<StatuteView> statuteViews) {
		long maxCount = 0;
		for ( StatuteView c: statuteViews) {
			if ( c.getRefCount() > maxCount )
				maxCount = c.getRefCount();
		}	
		double d = ((maxCount+1) / 4.0);
		for ( StatuteView c: statuteViews ) {
			c.setImportance((int)(((double)c.getRefCount())/d)+1);
		}	
	}
/*

	public List<OpinionScoreList> scoreSlipOpinionOpinions(
		OpinionView opinionView, 
		ParsedOpinionCitationSet parserResults
	) {
		List<OpinionScoreList> opinionScores = new ArrayList<OpinionScoreList>();
		// need a collection StatutueCitations.
        for ( OpinionBase opinionCited: opinionView.getOpinionCitations() ) {
        	for ( OpinionStatuteCitation opinionStatuteCitation: opinionCited.getStatuteCitations() ) {
    			// search scoreMatrix for opinionStatuteCitation
    			OpinionScoreList opinionScoreList = null;
    			Iterator<OpinionScoreList> osIt = opinionScores.iterator(); 
    			while( osIt.hasNext() ) {
    				opinionScoreList = osIt.next(); 
    				if (opinionScoreList.getOpinionKey().equals(opinionCited.getOpinionKey())) {
    					break;
    				}
    				opinionScoreList = null;
    			}
    			if ( opinionScoreList == null ) {
    				opinionScoreList = new OpinionScoreList();
    				opinionScoreList.setOpinionKey(opinionCited.getOpinionKey());
    				opinionScores.add(opinionScoreList);
    			}
				//TODO will all entries be unqiue?
				OpinionScore opinionScore = new OpinionScore();
				opinionScore.setSlipOpinionStatute(opinionStatuteCitation.getStatuteCitation().getStatuteKey());
				opinionScore.setOpinionReferCount(opinionStatuteCitation.getStatuteCitation().getReferringOpinions().size());
				opinionScore.setSlipOpinionReferCount(opinionStatuteCitation.getCountReferences());
				opinionScoreList.getOpinionScoreList().add(opinionScore);
        	}
        }

        List<CaseView> cases = opinionView.getCases();

    	for ( OpinionScoreList opinionScoreList:  opinionScores ) {
    		int score = 0;
    		for ( OpinionScore opinionScore: opinionScoreList.getOpinionScoreList() ) {
    			score = score + (opinionScore.getOpinionReferCount() * opinionScore.getSlipOpinionReferCount() );
    		}
    		int caseViewPos = Collections.binarySearch(cases, new CaseView(null, opinionScoreList.getOpinionKey().toString(), null, 0));
    		cases.get(caseViewPos).setScore(score);
    	}

    	long maxScore = 0;
		for ( CaseView c: cases ) {
			if ( c.getScore() > maxScore )
				maxScore = c.getScore();
		}	
		maxScore = (maxScore / 4)+1;
		for ( CaseView c: cases ) {
			c.setImportance((int)(c.getScore()/maxScore)+1);
		}	

		
		Collections.sort(opinionView.getCases(), new Comparator<CaseView>() {
			@Override
			public int compare(CaseView o1, CaseView o2) {
				return o2.getScore() - o1.getScore();
			}
		});

        return opinionScores;
	}

	public List<StatuteScoreList> scoreSlipOpinionStatutes(
		OpinionView opinionView, 
		ParsedOpinionCitationSet parserResults
	) {
		List<StatuteScoreList> statuteScores = new ArrayList<StatuteScoreList>();
		// make a sorted list of statuteKey's that opinionView refers to
		
        for ( OpinionBase opinionCited: opinionView.getOpinionCitations()) {
        	for ( OpinionStatuteCitation opinionStatuteCitation: opinionCited.getStatuteCitations() ) {
    			// search scoreMatrix for opinionStatuteCitation
    			StatuteScoreList statuteScoreList = null;
    			StatuteKey statuteKey = opinionStatuteCitation.getStatuteCitation().getStatuteKey();
    			Iterator<StatuteScoreList> scsIt = statuteScores.iterator(); 
    			while( scsIt.hasNext() ) {
    				statuteScoreList = scsIt.next(); 
    				if (statuteScoreList.getSlipOpinionStatute().equals(statuteKey)) {
    					break;
    				}
    				statuteScoreList = null;
    			}
    			if ( statuteScoreList == null ) {
    				statuteScoreList = new StatuteScoreList();
    				statuteScoreList.setSlipOpinionStatute(statuteKey);
    				statuteScoreList.setSlipOpinionReferCount(opinionStatuteCitation.getCountReferences());
    				statuteScores.add(statuteScoreList);
    			}
				//TODO will all entries be unqiue?
				StatuteScore statuteScore = new StatuteScore();
				statuteScore.setOpinionKey(opinionCited.getOpinionKey());
				statuteScore.setOpinionReferCount(opinionStatuteCitation.getCountReferences());
				statuteScoreList.getStatuteScoreList().add(statuteScore);
        	}
        }

        List<StatuteView> statutes = opinionView.getStatutes();
//		Collections.sort(statutes);

		for ( StatuteScoreList statuteScoreList:  statuteScores ) {
    		int score = 0;
    		for ( StatuteScore statuteScore: statuteScoreList.getStatuteScoreList() ) {
    			score = score + (statuteScore.getOpinionReferCount() * statuteScoreList.getSlipOpinionReferCount() );
    		}
    		for ( StatuteView statuteView: statutes ) {
    			if ( statuteView.getSectionView().getTitle().equals(statuteScoreList.getSlipOpinionStatute().getTitle()) 
    					&& statuteView.getSectionView().getSectionNumber().equals(statuteScoreList.getSlipOpinionStatute().getSectionNumber()) )
				{
    				statuteView.setScore(score);
    			}
    		}
    	}

		long maxScore = 0;
		for ( StatuteView c: statutes ) {
			if ( c.getScore() > maxScore )
				maxScore = c.getScore();
		}	
		maxScore = (maxScore / 4)+1;
		for ( StatuteView c: statutes ) {
			c.setImportance((int)(c.getScore()/maxScore)+1);
		}	
        return statuteScores;
	}
*/	
}
