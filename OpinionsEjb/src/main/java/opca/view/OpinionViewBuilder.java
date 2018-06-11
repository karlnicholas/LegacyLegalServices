package opca.view;

import java.util.*;
import java.util.logging.Logger;

import service.Client;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesRoot;
import opca.model.*;
import opca.parser.ParsedOpinionCitationSet;

public class OpinionViewBuilder {
	private Logger logger = Logger.getLogger(OpinionViewBuilder.class.getName());
	private final Client statutesRs;
	public OpinionViewBuilder(Client statutesRs) {
		this.statutesRs = statutesRs;
	}
	
    public OpinionView buildOpinionView(
    	SlipOpinion slipOpinion,
		ParsedOpinionCitationSet parserResults  
	) {
        List<StatuteView> statuteViews = createStatuteViews(slipOpinion);
        // create a CaseView list.
    	ArrayList<CaseView> cases = new ArrayList<CaseView>();
    	for ( OpinionBase opinionBase: slipOpinion.getOpinionCitations() ) {
    		OpinionBase opcase = parserResults.findOpinion(opinionBase.getOpinionKey());
			CaseView caseView = new CaseView(opcase.getTitle(), opinionBase.getOpinionKey().toString(), opcase.getOpinionDate(), opcase.getCountReferringOpinions());
    		cases.add(caseView);
    	}
        return new OpinionView(slipOpinion, slipOpinion.getFileName(), statuteViews, cases);
    }


    /**
     * Should be combined and trimmed, yes?
     * 
     * @param opinionBase
     * @param responseArray
     * @return
     */
    public List<StatuteView> createStatuteViews(OpinionBase opinionBase) {
        // statutes ws .. getStatuteKeys list to search for 
    	statutesrs.StatuteKeyArray statuteKeyArray = new statutesrs.StatuteKeyArray();
        for( StatuteCitation statuteCitation: opinionBase.getOnlyStatuteCitations() ) {
            statutesrs.StatuteKey statuteKey = new statutesrs.StatuteKey();            
            statuteKey.setTitle(statuteCitation.getStatuteKey().getTitle());
            statuteKey.setSectionNumber(statuteCitation.getStatuteKey().getSectionNumber());
            statuteKeyArray.getItem().add(statuteKey);
        }
        // call statutesws to get details of statutes 
        statutesrs.ResponseArray responseArray = statutesRs.findStatutes(statuteKeyArray);
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
	            // here we look for the Doc Section within the Code Section hierarchy
	            // and place it within the sectionReference we previously parsed out of the opinion
	            opSection.setStatutesBaseClass(findStatutesBaseClass(responseArray, citation.getStatuteCitation().getStatuteKey()));
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
    	StatutesLeaf statutesLeaf = (StatutesLeaf) statutesBaseClass;
    	while ( statutesBaseClass.getParent() != null ) {
    		statutesBaseClass = statutesBaseClass.getParent();
    	}
    	// ok, our codeSection is the top section
    	Iterator<StatuteView> cit = codes.iterator();
    	while ( cit.hasNext() ) {
    		StatuteView opCode = cit.next();
    		if ( opCode.getStatutesBaseClass().equals(statutesBaseClass) ) 
    			return opCode;
    	}
    	// else, construct one ...
    	StatuteView opCode = new StatuteView( statutesBaseClass, statutesLeaf);
        codes.add(opCode);
    	return opCode;
    }    

}
