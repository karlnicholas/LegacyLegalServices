package opca.view;

import java.util.*;

import opca.model.OpinionBase;
import opca.model.SlipOpinion;
import statutes.StatutesBaseClass;

public class OpinionView extends SlipOpinion {
	private static final int MAX_INFO_LENGTH = 75;
	private static final long serialVersionUID = 1L;
	// reverse sorted by the constructor.
	private List<StatuteView> statutes;
	// reverse sorted by the constructor.
	private List<CaseView> cases;
	private String name;
	
	public OpinionView() {
		super();
	}
	public OpinionView(
		SlipOpinion slipOpinion, 
		String name, 
		List<StatuteView> statutes, 
		List<CaseView> cases
	) {
		super(slipOpinion);
		this.name = name;
		this.statutes = statutes;
		Collections.sort(this.statutes);
//		Collections.reverse(this.statutes);
		this.cases = cases;
		Collections.sort(this.cases);
//		Collections.reverse(this.cases);
	}
	// supporting methods for JSF pages
	public String getCondensedStatuteInfo() {
		StringBuilder sb = new StringBuilder();
		boolean shortened = false;
		for (StatuteView statuteView: statutes) {
			sb.append(statuteView.getStatutesBaseClass().getShortTitle());
			sb.append("  [");
			sb.append(statuteView.getRefCount());
			sb.append("], ");
			if ( sb.length() > MAX_INFO_LENGTH ) {
				sb.delete(sb.length()-2, sb.length()-1);
				sb.append("...");
				shortened = true;
				break;
			}
		}
		if( sb.length() >= 3 && !shortened ) {
			sb.delete(sb.length()-2, sb.length()-1);
		}
		return sb.toString();
	}
	
	public String getCondensedCaseInfo() {
		StringBuilder sb = new StringBuilder();
		boolean shortened = false;
		for (CaseView caseView: cases) {
			if ( caseView.getTitle() == null ) {
				sb.append(caseView.getCitation());
			} else {
				sb.append(caseView.getTitle());
			}
			sb.append(", ");
			if ( sb.length() > MAX_INFO_LENGTH ) {
				sb.delete(sb.length()-2, sb.length()-1);
				sb.append("...");
				shortened = true;
				break;
			}
		}
		if( sb.length() >= 3 && !shortened ) {
			sb.delete(sb.length()-2, sb.length()-1);
		}
		return sb.toString();
	}
	// end: supporting methods for JSF pages 

	/**
	 * If there isn't a sectionView at the end of the chain then need to remove entire tree(branch)
	 * Also, shouldn't there be incremented citation counts for removed items?
	 * @param levelOfInterest
	 * @param removeCodes
	 */
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
    public void combineCommonSections() {
    	Map<StatutesBaseClass, List<StatuteView>> combinedStatutes = new HashMap<StatutesBaseClass, List<StatuteView>>(); 
    	for ( StatuteView statuteView:  statutes) {
    		List<StatuteView> statuteViews = combinedStatutes.get(statuteView.getStatutesLeaf());
    		if ( statuteViews == null ) {
    			statuteViews = new ArrayList<StatuteView>(); 
    			combinedStatutes.put(statuteView.getStatutesLeaf(), statuteViews);
    		}
    		boolean found = false;
    		for ( StatuteView existingStatuteView:  statuteViews ) {
    			if ( statuteView.getStatutesLeaf().getStatuteRange().equals(existingStatuteView.getStatutesLeaf().getStatuteRange()) ) {
    				existingStatuteView.addReference(statuteView);
    				existingStatuteView.incRefCount(statuteView.getRefCount());
    				found = true;
    				break;
    			}
    		}
    		if ( !found ) {
    			statuteViews.add(statuteView);
    		}
    	}
    	statutes.clear();
        for ( StatutesBaseClass key: combinedStatutes.keySet()) {
        	List<StatuteView> statuteViews =  combinedStatutes.get(key);
        	for ( StatuteView statuteView: statuteViews ) {
        		statutes.add(statuteView);
        	}
        }
    }
	public void scoreCitations( OpinionViewBuilder opinionViewBuilder) {
		scoreSlipOpinionStatutes();
		// create a union of all statutes from the slipOpinion and the cited cases
		List<StatuteView> statuteUnion = new ArrayList<>(getStatutes());
		
		List<OpinionView> tempOpinionViewList = new ArrayList<>();
		// need a collection StatutueCitations.
        for ( OpinionBase opinionCited: getOpinionCitations() ) {
            List<StatuteView> statuteViews = opinionViewBuilder.createStatuteViews(opinionCited);
            rankStatuteViews(statuteViews);
            // create a temporary OpinionView to use its functions
            // store the opinionView in the Cases list.
            List<CaseView> tempCaseViews = new ArrayList<>();
            CaseView caseView = findCaseView(opinionCited);
            tempCaseViews.add( caseView );
            OpinionView tempOpinionView = new OpinionView();
            tempOpinionView.setStatutes(statuteViews);
            tempOpinionView.setCases(tempCaseViews); 
            tempOpinionView.combineCommonSections();
            tempOpinionView.trimToLevelOfInterest(2, true);
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
            for ( StatuteView statuteView: tempOpinionView.getStatutes() ) {
            	if ( !statuteUnion.contains(statuteView) ) {
            		statuteUnion.add(statuteView);
            	}
            }
            
        }
        
        // create a ranked slipAdjacencyMatrix
        int[] slipAdjacencyMatrix = createAdjacencyMatrix(statuteUnion, statutes);
        for ( OpinionView tempOpinionView: tempOpinionViewList ) {

            int[] opinionAdjacencyMatrix = createAdjacencyMatrix(statuteUnion, tempOpinionView.getStatutes());
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
/*		
		statutes.forEach(statuteView->{
			String ds = statuteView.getDisplaySections();
    		System.out.println(",,,"+getName()+","+statuteView.getImportance()+","+ds+"-"+statuteView.getStatutesBaseClass().getShortTitle());
		});
		cases.forEach(caseView->{
    		System.out.println(","+caseView.getImportance()+","+caseView.getCitation()+","+getName()+",,");
		});
*/
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
		List<StatuteView> statuteUnion,
		List<StatuteView> statuteViews
	) {
		int[] adjacencyMatrix = new int[statuteUnion.size()];
		int i = 0;
		for (StatuteView unionStatuteView: statuteUnion ) {
			int idx = statuteViews.indexOf(unionStatuteView);
			adjacencyMatrix[i++] = idx == -1 ? 0 : statuteViews.get(idx).getImportance(); 
		}
		return adjacencyMatrix;
	}

	private void scoreSlipOpinionStatutes() {
		rankStatuteViews(statutes);
		Collections.sort(statutes, new Comparator<StatuteView>() {
			@Override
			public int compare(StatuteView o1, StatuteView o2) {
				return o2.getImportance() - o1.getImportance();
			}
		});
/*
		statutes.forEach(statuteView->{
			String ds = statuteView.getDisplaySections();
    		System.out.println(",,,"+getName()+","+statuteView.getRefCount()+","+ds+"-"+statuteView.getStatutesBaseClass().getShortTitle());
		});
*/		
	}
	/**
	 * Rank from 0-4
	 * @param statuteViews
	 */
	private void rankStatuteViews(List<StatuteView> statuteViews) {
		long maxCount = 0;
		for ( StatuteView c: statuteViews) {
			if ( c.getRefCount() > maxCount )
				maxCount = c.getRefCount();
		}
		// Don't scale any refCounts. If less than 4 then leave as is.
		if ( maxCount < 4) maxCount = 4;
		double d = ((maxCount+1) / 5.0);
		for ( StatuteView c: statuteViews ) {
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
    public List<StatuteView> getStatutes() {
        return statutes;
    }
    public void setStatutes(List<StatuteView> statutes) {
        this.statutes = statutes;
    }
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CaseView> getCases() {
		return cases;
	}
	public void setCases(List<CaseView> cases) {
		this.cases = cases;
	}
	@Override
    public String toString() {
    	return name + " " + this.getTitle();
    }
}

/*	
public Map<StatutesRoot, List<StatuteView>> combineCommonSections() {
	Map<StatutesRoot, List<StatuteView>> combinedStatutes = new HashMap<StatutesRoot, List<StatuteView>>(); 
	for ( StatuteView statuteView:  statutes) {
		List<StatuteView> statuteViews = combinedStatutes.get(statuteView.getStatutesBaseClass().getStatutesRoot());
		if ( statuteViews == null ) {
			statuteViews = new ArrayList<StatuteView>(); 
			combinedStatutes.put(statuteView.getStatutesBaseClass().getStatutesRoot(), statuteViews);
		}
		boolean found = false;
		for ( StatuteView existingStatuteView:  statuteViews ) {
			if ( statuteView.getStatutesBaseClass().getStatuteRange().equals(existingStatuteView.getStatutesBaseClass().getStatuteRange()) ) {
				existingStatuteView.addReference(statuteView);
				existingStatuteView.incRefCount(statuteView.getRefCount());
				found = true;
				break;
			}
		}
		if ( !found ) {
			statuteViews.add(statuteView);
		}
	}
	statutes.clear();
    for ( StatutesRoot key: combinedStatutes.keySet()) {
    	List<StatuteView> statuteViews =  combinedStatutes.get(key);
    	for ( StatuteView statuteView: statuteViews ) {
    		statutes.add(statuteView);
    	}
    }
    
    return combinedStatutes;
}
*/    
/*
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

        statuteViews.forEach(statuteView->{
    		System.out.println(statuteView.getDisplaySections()+"-"+statuteView.getStatutesBaseClass().getShortTitle()+","+statuteView.getRefCount()+","+opinionCited.getOpinionKey()+","+opinionView.getName()+",,");
		});		

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
*/	
