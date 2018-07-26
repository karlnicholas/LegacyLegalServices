package opca.view;

import java.util.*;

import opca.model.OpinionBase;
import opca.model.SlipOpinion;

public class OpinionView {
	private static final int MAX_INFO_LENGTH = 75;
	// reverse sorted by the constructor.
	private List<StatuteView> statutes;
	// reverse sorted by the constructor.
	private List<CaseView> cases;
	private String name;
	private String title;
	private Date opinionDate;
	
	public OpinionView() {
		super();
	}
	public OpinionView(
		SlipOpinion slipOpinion, 
		String name, 
		List<StatuteView> statutes, 
		List<CaseView> cases
	) {
		this.name = name;
		this.statutes = statutes;
		this.title = slipOpinion.getTitle();
		this.setOpinionDate(slipOpinion.getOpinionDate());
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
			sb.append(statuteView.getShortTitle());
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
	public void scoreCitations( OpinionViewBuilder opinionViewBuilder) {
		scoreSlipOpinionStatutes();
		// create a union of all statutes from the slipOpinion and the cited cases
		List<StatuteView> statuteUnion = new ArrayList<>(getStatutes());
		
		List<OpinionView> tempOpinionViewList = new ArrayList<>();
		// need a collection StatutueCitations.
//		opinionViewBuilder.getParserResults().getOpinionTable()
//        for ( OpinionBase opinionCited: getOpinionCitations() ) {
		for ( OpinionBase opinionCited: opinionViewBuilder.getParserResults().getOpinionTable() ) {
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
//            tempOpinionView.combineCommonSections();
//            tempOpinionView.trimToLevelOfInterest(2, true);
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
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public Date getOpinionDate() {
		return opinionDate;
	}
	public void setOpinionDate(Date opinionDate) {
		this.opinionDate = opinionDate;
	}
}

