package opca.view;

import java.util.*;
import java.util.logging.Logger;

import statutes.*;
import service.Client;
import opca.model.*;
import opca.parser.ParsedOpinionCitationSet;

public class OpinionViewBuilder {
	Logger logger = Logger.getLogger(OpinionViewBuilder.class.getName());

    public OpinionViewBuilder() {
    }

    public OpinionView buildSlipOpinionView(
    	Client statutesRs, 
		SlipOpinion slipOpinion, 
		ParsedOpinionCitationSet parserResults 
	) {
    	return buildOpinionView(statutesRs, slipOpinion, slipOpinion.getFileName(), parserResults);
    }

    private OpinionView buildOpinionView(
    	Client statutesRs, 
    	SlipOpinion slipOpinion,
		String name, 
		ParsedOpinionCitationSet parserResults
	) {
    	
        // statutes ws .. getStatuteKeys list to search for 
    	statutesrs.StatuteKeyArray statuteKeyArray = new statutesrs.StatuteKeyArray();
        for( StatuteKeyEntity statuteCitation: slipOpinion.getStatuteCitations() ) {
            statutesrs.StatuteKey statuteKey = new statutesrs.StatuteKey();            
            statuteKey.setCode(statuteCitation.getCode());
            statuteKey.setSectionNumber(statuteCitation.getSectionNumber());
            statuteKeyArray.getItem().add(statuteKey);
        }
        // call statutesws to get details of statutes 
        statutesrs.ResponseArray responseArray = statutesRs.findStatutes(statuteKeyArray);
        //
    	ArrayList<StatuteView> codes = new ArrayList<StatuteView>();
        // copy results into the new list ..
        // Fill out the codeSections that these section are referencing ..
        // If possible ... 
        Iterator<StatuteKeyEntity> itc = slipOpinion.getStatuteCitations().iterator();
        
        while ( itc.hasNext() ) {
        	StatuteKeyEntity key = itc.next();
        	StatuteCitation citation = parserResults.findStatute(key);
            // This is a section
            if ( citation.getStatuteKey().getCode() != null ) {
                SectionView opSection = new SectionView(slipOpinion, citation);
                // here we look for the Doc Section within the Code Section Hierachary
                // and place it within the sectionReference we previously parsed out of the opinion
//                opSection.setCodeReference( codesInterface.findReference(opSection.getCode(), opSection.getSectionNumber() ) );
                opSection.setStatutesBaseClass(findStatutesBaseClass(responseArray, key));
//                Section codeSection = codeList.findCodeSection(sectionReference);
                // We don't want to keep ones that we can't map .. so .. 
                if ( opSection.getStatutesBaseClass() != null ) {
                    // First .. let's get the OpinionCode for this sectionReference
                    StatuteView opCode = findOrMakeOpinionCode(codes, opSection); 
                    opCode.addNewSectionReference( opSection );
                } 
            }
        }

        // create a CaseView list.
    	ArrayList<CaseView> cases = new ArrayList<CaseView>();
    	for ( OpinionKey caseKey: slipOpinion.getOpinionCitations() ) {
    		OpinionSummary opcase = parserResults.findOpinion(caseKey);
			CaseView caseView = new CaseView(opcase.getTitle(), caseKey.toString(), opcase.getOpinionDate(), opcase.getCountReferringOpinions());
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
        return new OpinionView(slipOpinion, name, codes, cases);
    }


    private StatutesBaseClass findStatutesBaseClass(statutesrs.ResponseArray responseArray, StatuteKeyEntity key) {
    	StatutesBaseClass statutesBaseClass = null;
    	final String code = key.getCode();
    	final String sectionNumber = key.getSectionNumber();
    	for ( statutesrs.ResponsePair responsePair: responseArray.getItem()) {
    		statutesrs.StatuteKey statuteKey = responsePair.getStatuteKey();
    		if ( code.equals(statuteKey.getCode()) && sectionNumber.equals(statuteKey.getSectionNumber()) ) {
    			statutesBaseClass = (StatutesBaseClass) responsePair.getStatutesBaseClass();
    			break;
    		}
    	}
    	return statutesBaseClass;
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
    	StatuteView opCode = new StatuteView( statutesBaseClass);
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
            String codeTitle = opSection.getCode();
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

	public List<OpinionScoreList> scoreSlipOpinionOpinions(
		OpinionView opinionView, 
		ParsedOpinionCitationSet parserResults, 
		List<OpinionSummary> opinionSummaries
	) {
		List<OpinionScoreList> opinionScores = new ArrayList<OpinionScoreList>();
		// need a collection StatutueCitations.
        for ( OpinionSummary opinionCited: opinionSummaries ) {
        	for ( StatuteKeyEntity statuteKey: opinionCited.getStatuteCitations() ) {
				StatuteCitation statuteCitation = parserResults.findStatute(statuteKey);
        		if ( statuteCitation != null) {
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
					opinionScore.setSlipOpinionStatute(statuteKey);
					opinionScore.setOpinionReferCount(statuteCitation.getRefCount(opinionCited.getOpinionKey()));
					opinionScore.setSlipOpinionReferCount(statuteCitation.getRefCount(opinionView.getOpinionKey()));
					opinionScoreList.getOpinionScoreList().add(opinionScore);
        		}
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
		ParsedOpinionCitationSet parserResults, 
		List<OpinionSummary> opinionSummaries
	) {
		List<StatuteScoreList> statuteScores = new ArrayList<StatuteScoreList>();
		// make a sorted list of statuteKey's that opinionView refers to
		
        for ( OpinionSummary opinionCited: opinionSummaries) {
        	for ( StatuteKeyEntity statuteKey: opinionCited.getStatuteCitations() ) {
				StatuteCitation statuteCitation = parserResults.findStatute(statuteKey);
        		if ( statuteCitation != null ) {
        			// search scoreMatrix for opinionStatuteCitation
        			StatuteScoreList statuteScoreList = null;
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
        				statuteScoreList.setSlipOpinionReferCount(statuteCitation.getRefCount(opinionView.getOpinionKey()));
        				statuteScores.add(statuteScoreList);
        			}
					//TODO will all entries be unqiue?
					StatuteScore statuteScore = new StatuteScore();
					statuteScore.setOpinionKey(opinionCited.getOpinionKey());
					statuteScore.setOpinionReferCount(statuteCitation.getRefCount(opinionCited.getOpinionKey()));
					statuteScoreList.getStatuteScoreList().add(statuteScore);
        		}
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
    			if ( statuteView.getSectionView().getCode().equals(statuteScoreList.getSlipOpinionStatute().getCode()) 
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
}
