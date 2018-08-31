package gsearch.viewmodel;

import java.util.*;

import statutes.StatutesBaseClass;

public abstract class EntryBase implements EntryReference {
	private StatutesBaseClass statutesBaseClass;
	private int count;
	private boolean pathPart;
	private String displayTitle;
	// only for presentation layer
	protected List<EntryReference> scratchList;
	
//	public EntryBase(String facetHead) { this.setFacetHead(facetHead); init(); }
	public EntryBase(String facetHead) { init(); }
	public EntryBase( StatutesBaseClass statutesBaseClass, String displayTitle) {
		this.statutesBaseClass = statutesBaseClass;
		this.displayTitle = displayTitle;
/*		
		this.fullFacet = FacetUtils.toString(
				FacetUtils.getFullFacet(facetHead, statutesBaseClass)
			);
*/			
		// 
		init();
	}
	
	private void init() {
		count = 0;
		this.pathPart = true;
		this.scratchList = new ArrayList<EntryReference>();
	}

	@Override
	public StatutesBaseClass getStatutesBaseClass() {
		return statutesBaseClass;
	}

	@Override
	public void setStatutesBaseClass(StatutesBaseClass cloneBaseClass) {
		this.statutesBaseClass = cloneBaseClass;
	}
	@Override
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public boolean isPathPart() {
		return pathPart;
	}
	public void setPathPart(boolean pathPart) {
		this.pathPart = pathPart;
	}
	@Override
	public String getFullFacet() {
		if ( statutesBaseClass != null )
			return statutesBaseClass.getFullFacet();
		else 
			return "";
	}
/*	
	public void setFullFacet(String[] fullFacet) {
		this.fullFacet = FacetUtils.toString(fullFacet);
	}
	public void setFullFacet(String fullFacet) {
		this.fullFacet = fullFacet;
	}
*/	
	public String getDisplayTitle() {
		return displayTitle;
	}
	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}
/*	
	public String getFacetHead() {
		return facetHead;
	}
	public void setFacetHead(String facetHead) {
		this.facetHead = facetHead;
	}
*/	
}
