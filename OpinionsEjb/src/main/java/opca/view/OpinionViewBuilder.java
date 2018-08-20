package opca.view;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;
import statutes.service.StatutesService;
import opca.model.*;
import opca.parser.ParsedOpinionCitationSet;

public class OpinionViewBuilder {
	private Logger logger = Logger.getLogger(OpinionViewBuilder.class.getName());
	private static final int LEVEL_OF_INTEREST = 3; 
	private final StatutesService statutesRs;
	private ParsedOpinionCitationSet parserResults;
	private OpinionView opinionView;
	private List<CaseView> cases; 
	private List<SectionView> sectionViews;
	
	public OpinionViewBuilder(StatutesService statutesRs) {
		this.statutesRs = statutesRs;
	}
	
    public OpinionView buildOpinionView(
    	SlipOpinion slipOpinion,
		ParsedOpinionCitationSet parserResults  
	) {
        List<StatuteView> statuteViews = createStatuteViews(slipOpinion);
        this.parserResults = parserResults;
        // create a CaseView list.
    	cases = new ArrayList<CaseView>();
    	for ( OpinionBase opinionBase: slipOpinion.getOpinionCitations() ) {
    		OpinionBase opcase = parserResults.findOpinion(opinionBase.getOpinionKey());
			CaseView caseView = new CaseView(opcase.getTitle(), opinionBase.getOpinionKey().toString(), opcase.getOpinionDate(), opcase.getCountReferringOpinions());
    		cases.add(caseView);
    	}
        opinionView = new OpinionView(slipOpinion, slipOpinion.getFileName(), statuteViews, cases);
        sectionViews = opinionView.getSectionViews();
        scoreCitations();
        return opinionView;
    }


    /**
     * Should be combined and trimmed, yes?
     * 
     * @param opinionBase
     * @param responseArray
     * @return
     */
    public List<StatuteView> createStatuteViews(OpinionBase opinionBase) {
    	List<StatuteKey> errorKeys = new ArrayList<>();
        // statutes ws .. getStatuteKeys list to search for 
    	statutes.service.dto.StatuteKeyArray statuteKeyArray = new statutes.service.dto.StatuteKeyArray();
        for( StatuteCitation statuteCitation: opinionBase.getOnlyStatuteCitations() ) {
            statutes.service.dto.StatuteKey statuteKey = new statutes.service.dto.StatuteKey();            
            statuteKey.setTitle(statuteCitation.getStatuteKey().getTitle());
            statuteKey.setSectionNumber(statuteCitation.getStatuteKey().getSectionNumber());
            statuteKeyArray.getItem().add(statuteKey);
        }
        // call statutesws to get details of statutes 
        statutes.service.dto.KeyHierarchyPairs keyHierarchyPairs = statutesRs.getStatutesAndHierarchies(statuteKeyArray);
        //
    	List<StatuteView> statuteViews = new ArrayList<>();
        // copy results into the new list ..
        Iterator<OpinionStatuteCitation> itc = opinionBase.getStatuteCitations().iterator();
        
        while ( itc.hasNext() ) {
        	OpinionStatuteCitation citation = itc.next();
        	if ( citation.getStatuteCitation().getStatuteKey().getTitle() != null ) {
        		// find the statutesLeaf from the Statutes service and return with all parents filled out.
	            StatutesLeaf statutesLeaf = findStatutesLeaf(keyHierarchyPairs, citation.getStatuteCitation().getStatuteKey());
	            if ( statutesLeaf == null ) {
	            	if ( logger.getLevel() == Level.FINE ) {
	            		errorKeys.add(citation.getStatuteCitation().getStatuteKey());
	            	}
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
	            	statuteView = new StatuteView();
	            	statuteView.initialize((StatutesRoot)statutesRoot, 0, null );
	            	statuteViews.add(statuteView);
	            }
                addSectionViewToSectionRoot(statuteView, statutesLeaf, citation.getCountReferences());                
        	}
        }
        trimToLevelOfInterest(statuteViews, LEVEL_OF_INTEREST, true);
        if ( errorKeys.size() > 0 && logger.getLevel() == Level.FINE) {
        	logger.fine("Error Keys: " + Arrays.toString(errorKeys.toArray()));
        }
        return statuteViews;
    }

    /**
     * Take statute hierarchy in statutesLeaf and merge into 
     * ViewReference hierarchy in statuteView. Add reference counts
     * into existing ViewReference hierarchy. 
     * 
     * @param statuteView existing StatuteView root
     * @param statutesLeaf new StatutesLeaf citation
     * @param refCount reference count of slip opinion for this statutesLeaf
     */
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
		// setup view parent
		ViewReference viewParent = statuteView;
		// start stack loop.		
		while ( !statutesNodes.isEmpty()) {
			StatutesNode statutesNode = statutesNodes.pop();
			// do childrenViews check
			viewParent = checkChildrenViews(childrenViews, statutesNode, refCount, viewParent, SubcodeView::new);
			childrenViews = viewParent.getChildReferences(); 
		}
		checkChildrenViews(childrenViews, statutesLeaf, refCount, viewParent, SectionView::new);
	}
	
	/**
	 * Check to see if the statutesBaseClass is in the existing childrenViews
	 * by checking title though might change it to fullFacet.
	 * Create new viewReference if needed with statutesBaseClass and refCount.
	 * Return  the childrenViews of new or existing childView.
	 * 
	 * @param childrenViews list of ViewReferences to check against.
	 * @param statutesBaseClass of statutesNode or StatutesLeaf
	 * @param refCount of statutesBaseClass 
	 * @param viewReferenceConstructor function to create new ViewReference if needed
	 * @return childrenViews of next level in hierarchy.
	 */
	private ViewReference checkChildrenViews(
		List<ViewReference> childrenViews, 
		StatutesBaseClass statutesBaseClass,
		int refCount, 
		ViewReference parent, 
		Supplier<ViewReference> viewReferenceConstructor 
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
			return childView; 
		} else {
			ViewReference viewReference = viewReferenceConstructor.get();
			viewReference.initialize(statutesBaseClass, refCount, parent);
			childrenViews.add(viewReference);
			return viewReference;
		}		
	}

	/**
	 * Trim the ViewReference hierarchy where and refCount less than levelOfInterest. 
	 * If removeStatuteViews then remove any statuteViews that don't meet requirement. 
	 * 
	 * If there isn't a sectionView at the end of the chain then need to remove entire tree(branch).
	 *
	 * @param levelOfInterest level to trim to.
	 * @param removeStatuteViews remove statuteViews
	 */
	private void trimToLevelOfInterest( List<StatuteView> statuteViews, int levelOfInterest, boolean removeStatuteViews) {
		Iterator<StatuteView> ci = statuteViews.iterator();
		while ( ci.hasNext() ) {
			StatuteView statuteView = ci.next();
			statuteView.trimToLevelOfInterest( levelOfInterest );
			if (removeStatuteViews) {
			    if ( statuteView.getRefCount() < levelOfInterest )
			        ci.remove();
			}
		}
	}
    /**
     * Build out the StatutesBaseClass with parent pointers
     * @param keyHierarchyPairs
     * @param key
     * @return
     */
    private StatutesLeaf findStatutesLeaf(statutes.service.dto.KeyHierarchyPairs keyHierarchyPairs, StatuteKey key) {
		List<StatutesBaseClass> subPaths = null;
    	final String title = key.getTitle();
    	final String sectionNumber = key.getSectionNumber();
    	for ( statutes.service.dto.KeyHierarchyPair keyHierarchyPair: keyHierarchyPairs.getItem()) {
    		statutes.service.dto.StatuteKey statuteKey = keyHierarchyPair.getStatuteKey();
    		if ( title.equals(statuteKey.getTitle()) && sectionNumber.equals(statuteKey.getSectionNumber()) ) {
    			subPaths = keyHierarchyPair.getStatutesPath();
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

    
    private StatuteView findExistingStatuteView( List<StatuteView> statutesViews, StatutesBaseClass statutesRoot) {
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
	
	// end: supporting methods for JSF pages 
	private void scoreCitations() {
		scoreSlipOpinionStatutes();
		// create a union of all statutes from the slipOpinion and the cited cases
		List<SectionView> sectionUnion = new ArrayList<>(sectionViews);		
		List<OpinionView> tempOpinionViewList = new ArrayList<>();
		// need a collection StatutueCitations.
		for ( OpinionBase opinionCited: getParserResults().getOpinionTable() ) {
			List<StatuteView> statuteViews = createStatuteViews(opinionCited);
        	List<SectionView> sectionViews = new ArrayList<>();
            for( StatuteView statuteView: statuteViews ) {
            	sectionViews.addAll(statuteView.getSectionViews());
            }
            rankSectionViews(sectionViews);
            // create a temporary OpinionView to use its functions
            // store the opinionView in the Cases list.
            List<CaseView> tempCaseViews = new ArrayList<>();
            CaseView caseView = findCaseView(opinionCited);
            tempCaseViews.add( caseView );
            OpinionView tempOpinionView = new OpinionView();
            tempOpinionView.setStatutes(statuteViews);
            tempOpinionView.setCases(tempCaseViews); 
            if ( tempOpinionView.getStatutes().size() == 0 ) {
            	caseView.setScore(-1);
            	// well, just remove cases with no interesting citations
            	cases.remove(caseView);
            	continue;
            }
/*            
        	tempOpinionView.getStatutes().forEach(statuteView->{
        		System.out.println(statuteView.getDisplaySections()+"-"+statuteView.getStatutesBaseClass().getShortTitle()+","+statuteView.getRefCount()+","+opinionCited.getOpinionKey()+","+getName()+",,");
    		});
*/    				
            // save this for next loop
            tempOpinionViewList.add(tempOpinionView);
            for ( SectionView sectionView: sectionViews ) {
            	if ( !sectionUnion.contains(sectionView) ) {
            		sectionUnion.add(sectionView);
            	}
            }
        }
        
        // create a ranked slipAdjacencyMatrix
        int[] slipAdjacencyMatrix = createAdjacencyMatrix(sectionUnion, sectionViews);
        for ( OpinionView tempOpinionView: tempOpinionViewList ) {

            int[] opinionAdjacencyMatrix = createAdjacencyMatrix(sectionUnion, tempOpinionView.getSectionViews());
            // find the distance between the matrices
            int sum = 0;
            for ( int i=slipAdjacencyMatrix.length-1; i >= 0; --i ) {
            	sum = sum + Math.abs( slipAdjacencyMatrix[i] - opinionAdjacencyMatrix[i] ); 
            }
            tempOpinionView.getCases().get(0).setScore(sum);
        }
        // rank "scores"
        // note that scores is actually a distance computation
        // and lower is better
        // so the final result is subtracted from 4
        // because importance is higher is better.
    	long maxScore = 0;
		for ( CaseView c: cases ) {
			if ( c.getScore() > maxScore )
				maxScore = c.getScore();
		}	
		double d = ((maxScore+1) / 4.0);
		for ( CaseView c: cases ) {
			if ( c.getScore() == -1 ) {
				c.setImportance(0);
			} else {
				int imp = (int)(((double)c.getScore())/d)+1;
				c.setImportance(5-imp);
			}
		}	
		Collections.sort(cases, new Comparator<CaseView>() {
			@Override
			public int compare(CaseView o1, CaseView o2) {
				return o2.getImportance() - o1.getImportance();
			}
		});
	}

	/**
	 * This is only a single row adjacency matrix because all of the rows
	 * past the first row would contain 0's and they would not be used in 
	 * the distance computation
	 * 
	 * @param statuteUnion
	 * @param statuteViews
	 * @return
	 */
	private int[] createAdjacencyMatrix(
		List<SectionView> sectionUnion,
		List<SectionView> sectionViews
	) {
		int[] adjacencyMatrix = new int[sectionUnion.size()];
		int i = 0;
		for (SectionView unionSectionView: sectionUnion ) {
			int idx = sectionViews.indexOf(unionSectionView);
			adjacencyMatrix[i++] = idx == -1 ? 0 : sectionViews.get(idx).getImportance(); 
		}
		return adjacencyMatrix;
	}

	private void scoreSlipOpinionStatutes() {
		rankSectionViews(sectionViews);
/*		
		Collections.sort(sectionViews, new Comparator<SectionView>() {
			@Override
			public int compare(SectionView o1, SectionView o2) {
				return o2.getImportance() - o1.getImportance();
			}
		});
*/		
	}
	/**
	 * Rank from 0-4
	 * @param statuteViews
	 */
	private void rankSectionViews(List<SectionView> sectionViews) {
		long maxCount = 0;
		for ( SectionView c: sectionViews) {
			if ( c.getRefCount() > maxCount )
				maxCount = c.getRefCount();
		}
		// Don't scale any refCounts. If less than 4 then leave as is.
		if ( maxCount < 4) maxCount = 4;
		double d = ((maxCount+1) / 5.0);
		for ( SectionView c: sectionViews ) {
			c.setImportance((int)(((double)c.getRefCount())/d));
		}
	}
	public CaseView findCaseView(OpinionBase opinionCited) {
		for ( CaseView caseView: cases) {
			if ( caseView.getCitation().equals(opinionCited.getOpinionKey().toString())) {
				return caseView;
			}
		}
		return null;
	}
}
