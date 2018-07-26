package opca.view;

import java.util.*;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import service.Client;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;
import opca.model.*;
import opca.parser.ParsedOpinionCitationSet;

public class OpinionViewBuilder {
	private Logger logger = Logger.getLogger(OpinionViewBuilder.class.getName());
	private final Client statutesRs;
	private ParsedOpinionCitationSet parserResults;  
	public OpinionViewBuilder(Client statutesRs) {
		this.statutesRs = statutesRs;
	}
	
    public OpinionView buildOpinionView(
    	SlipOpinion slipOpinion,
		ParsedOpinionCitationSet parserResults  
	) {
        List<StatuteView> statuteViews = createStatuteViews(slipOpinion);
        this.parserResults = parserResults;
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
    	List<StatuteView> statuteViews = new ArrayList<>();
        // copy results into the new list ..
        // Fill out the codeSections that these section are referencing ..
        // If possible ... 
        Iterator<OpinionStatuteCitation> itc = opinionBase.getStatuteCitations().iterator();
        
        while ( itc.hasNext() ) {
        	OpinionStatuteCitation citation = itc.next();
        	if ( citation.getStatuteCitation().getStatuteKey().getTitle() != null ) {
        		// find the statutesLeaf from the Statutes service and return with all parents filled out.
	            StatutesLeaf statutesLeaf = findStatutesLeaf(responseArray, citation.getStatuteCitation().getStatuteKey());
	            if ( statutesLeaf == null ) {
	            	logger.info("Statute referenced but not found: " + citation.getStatuteCitation().getStatuteKey());
	            	continue;
	            }
	            // get the root
	            StatutesBaseClass statutesRoot = statutesLeaf; 	            
	        	while ( statutesRoot.getParent() != null ) {
	        		statutesRoot = statutesRoot.getParent();
	        	}
	        	// see if StatuteView has already been created
	        	StatuteView statuteView = findExistingStatuteView(statuteViews, statutesRoot);
	            if ( statuteView == null ) {
	                // else, construct one ...
	            	statuteView = new StatuteView( (StatutesRoot)statutesRoot, 0);
	            	statuteViews.add(statuteView);
	            }
                addSectionViewToSectionRoot(statuteView, statutesLeaf, citation.getCountReferences());                
        	}
        }
        return statuteViews;
    }

	private void addSectionViewToSectionRoot(
		StatuteView statuteView, 
		StatutesLeaf statutesLeaf,
		int refCount 
	) {
		Stack<StatutesNode> statutesNodes = new Stack<>();
		StatutesBaseClass parent = statutesLeaf; 
		// push nodes onto stack
		while( (parent = parent.getParent()) != null && parent instanceof StatutesNode) {
			statutesNodes.push((StatutesNode)parent);
		}
		// prime childrenViews
		statuteView.incRefCount(refCount);
		List<ViewReference> childrenViews = statuteView.getChildReferences();
		// start stack loop.
		while ( !statutesNodes.isEmpty()) {
			StatutesNode statutesNode = statutesNodes.pop();
			// do childrenViews check
			childrenViews = checkChildrenViews(childrenViews, statutesNode, refCount, SubcodeView::new);
		}
		checkChildrenViews(childrenViews, statutesLeaf, refCount, SectionView::new);
	}
	
	private List<ViewReference> checkChildrenViews(
		List<ViewReference> childrenViews, 
		StatutesBaseClass statutesBaseClass,
		int refCount, 
		BiFunction<StatutesBaseClass, Integer, ViewReference> viewReferenceConstructor
	) {
		boolean found = false;
		ViewReference childView = null;
		Iterator<ViewReference> vrIt = childrenViews.iterator();
		while ( vrIt.hasNext() ) {
			childView = vrIt.next();
			if ( childView.getTitle().equals(statutesBaseClass.getTitle())) {
				found = true;
				break;
			}
		}
		if ( found ) {
			childView.incRefCount(refCount);
			return childView.getChildReferences(); 
		} else {
			ViewReference viewReference = viewReferenceConstructor.apply(statutesBaseClass, refCount);
			childrenViews.add(viewReference);
			return viewReference.getChildReferences();
		}		
	}

	/**
	 * If there isn't a sectionView at the end of the chain then need to remove entire tree(branch)
	 * Also, shouldn't there be incremented citation counts for removed items?
	 * @param levelOfInterest
	 * @param removeCodes
	 */
/*    
	public void trimToLevelOfInterest( int levelOfInterest, boolean removeCodes) {
		Iterator<StatuteView> ci = statutes.iterator();
		while ( ci.hasNext() ) {
			StatuteView statuteView = ci.next();
			statuteView.trimToLevelOfInterest( levelOfInterest );
			if (removeCodes) {
			    if ( statuteView.getRefCount() < levelOfInterest )
			        ci.remove();
			}
		}
	}
*/
	/**
	 * Use decorator pattern to Override equals and hashCode
	 * @author karln
	 *
	 */
	class SectionViewOverrides {
		private final SectionView sectionView;
		SectionViewOverrides(SectionView sectionView) {
			this.sectionView = sectionView;
		}
		public SectionView getSectionView() {
			return sectionView;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + sectionView.getTitle().hashCode();
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SectionViewOverrides))
				return false;
			SectionViewOverrides other = (SectionViewOverrides) obj;
			if (sectionView == null) {
				if (other.sectionView != null)
					return false;
			} else if (!sectionView.getTitle().equals(other.sectionView.getTitle()))
				return false;
			return true;
		}
	}
    /**
     * Build out the StatutesBaseClass with parent pointers
     * @param responseArray
     * @param key
     * @return
     */
    private StatutesLeaf findStatutesLeaf(statutesrs.ResponseArray responseArray, StatuteKey key) {
		List<StatutesBaseClass> subPaths = null;
    	final String title = key.getTitle();
    	final String sectionNumber = key.getSectionNumber();
    	for ( statutesrs.ResponsePair responsePair: responseArray.getItem()) {
    		statutesrs.StatuteKey statuteKey = responsePair.getStatuteKey();
    		if ( title.equals(statuteKey.getTitle()) && sectionNumber.equals(statuteKey.getSectionNumber()) ) {
    			subPaths = responsePair.getStatutesPath();
    			break;
    		}
    	}

		StatutesBaseClass returnBaseClass = null;
    	if ( subPaths != null ) {
			StatutesRoot statutesRoot = (StatutesRoot)subPaths.get(0);
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
    	return (StatutesLeaf)returnBaseClass;
    }

    
    // here we want to go up the parent tree and until we get to the top Section
    // because that is where the "Code" starts ...
    // What we know is that the sectionReference cannot be at the 
    // Top of the Code.
    private StatuteView findExistingStatuteView( List<StatuteView> statutesViews, StatutesBaseClass statutesRoot) {
    	// First, find the section's top code
//    	StatutesBaseClass statutesBaseClass = sectionView.getStatutesBaseClass();
//    	StatutesLeaf statutesLeaf = (StatutesLeaf) statutesBaseClass;
    	// ok, our codeSection is the top section
    	Iterator<StatuteView> cit = statutesViews.iterator();
    	while ( cit.hasNext() ) {
    		StatuteView statuteView = cit.next();
    		if ( statuteView.getShortTitle().equals(statutesRoot.getShortTitle()) ) 
    			return statuteView;
    	}
    	return null;
    }    

	public ParsedOpinionCitationSet getParserResults() {
		return parserResults;
	}    

/*
    public List<SectionView> getSectionViews() {
    	List<SectionView> sectionViews = new ArrayList<>();
    	Stack<HoldStuff> stack = new Stack<>();
    	stack.push( new HoldStuff(statuteView.getChildReferences()) );
    	while ( !stack.isEmpty() ) {
    		HoldStuff tStuff = stack.peek();
    		if ( tStuff.index >= tStuff.currentReferences.size() ) {
    			stack.pop();
    		} else if ( tStuff.currentReferences.get(tStuff.index) instanceof SectionView ) {
				sectionViews.add((SectionView) tStuff.currentReferences.get(tStuff.index));
				tStuff.index++;
    		} else {
    			stack.push(new HoldStuff(tStuff.currentReferences.get(0).getChildReferences()));
    		}
    	}
    	return sectionViews;
    }
*/
}
