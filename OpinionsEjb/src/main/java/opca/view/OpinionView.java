package opca.view;

import java.util.*;

import opca.model.SlipOpinion;
import statutes.StatutesRoot;

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

	public void trimToLevelOfInterest( int levelOfInterest, boolean removeCodes) {
		Iterator<StatuteView> ci = statutes.iterator();
		while ( ci.hasNext() ) {
			StatuteView code = ci.next();
			code.trimToLevelOfInterest( levelOfInterest );
			if (removeCodes) {
			    if ( code.getRefCount() < levelOfInterest ) 
			        ci.remove();
			}
		}
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
}
