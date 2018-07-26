package opca.view;

import java.util.*;

import statutes.SectionNumber;
import statutes.StatutesRoot;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 6/7/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatuteView extends ViewReference implements Comparable<StatuteView> {
    // This is holding the "chapter" section that is of interest ..
    private int cumulativeRefCount;
    private ArrayList<ViewReference> childReferences;
    
    // Well, this should really be the toplevel object in the codes hierarchy ..
    // but, lets set it to this for now, come back to it later ..
    private String fullFacet; 
    private String title; 
    private String shortTitle;
    
	private int score;
	private int importance;    
	
    // the old one was .. Section section, OpinionSection opSectionReference
    // Section section, OpinionSection opSectionReference
    public StatuteView(StatutesRoot statutesRoot, int refCount) {
//        this.statutesBaseClass = code;
//        this.leafBaseClass = leafBaseClass;
    	cumulativeRefCount = refCount;
    	fullFacet = statutesRoot.getFullFacet();
    	title = statutesRoot.getTitle();
    	shortTitle = statutesRoot.getShortTitle();
    	
        childReferences = new ArrayList<ViewReference>();
//        queue = new QueueUtility(); 
        // even so .. we'll have think about this .. 
        // no references initially .. 
    }
    
    public void trimToLevelOfInterest( int levelOfInterest ) {
    	Iterator<ViewReference> ori = childReferences.iterator();
    	while ( ori.hasNext() ) {
    		ViewReference opReference = ori.next();
    		int saveRefCount = opReference.getRefCount();
    		opReference.trimToLevelOfInterest( levelOfInterest );	
    		if ( opReference.getRefCount() < levelOfInterest ) {
    			incRefCount(0 - saveRefCount);
    			ori.remove();
    		}
    	}
    }

    public int compareTo( StatuteView statuteView) {
    	return shortTitle.compareTo(statuteView.shortTitle);
    }
/*
    public int compareTo( StatuteView statuteView) {
//    	StatutesBaseClass baseClass = getStatutesBaseClass();
    	StatutesBaseClass cBaseClass = statuteView.childReferences.get(0).getStatutesBaseClass();
    	int bCompare = baseClass.getStatutesRoot().compareTo(cBaseClass.getStatutesRoot());
    	if ( bCompare == 0 ) {
			SectionView sectionView = getSectionView();
			SectionView sectionViewOther = statuteView.getSectionView();
	    	SectionNumber sNumber = sectionView.getStatutesBaseClass().getStatutesLeaf().getStatuteRange().getsNumber();
	    	SectionNumber sNumberOther = sectionViewOther.getStatutesBaseClass().getStatutesLeaf().getStatuteRange().getsNumber();
	    	return sNumber.compareTo(sNumberOther);
    	} else {
    		return bCompare;
    	}
    }
*/
    public ArrayList<ViewReference> getChildReferences() {
    	return this.childReferences;
    }
    public void setChildReferences(ArrayList<ViewReference> childReferences) {
    	this.childReferences = childReferences;
    }
    public int getRefCount() {
        return cumulativeRefCount;
    }
    public void setRefCount(int refCount) {
        cumulativeRefCount = refCount;
    }
    public int incRefCount(int amount) {
    	cumulativeRefCount = cumulativeRefCount + amount;
        return cumulativeRefCount;
    }
            
	public void addReference(ViewReference opReference) {
		childReferences.add(opReference);
	}
	public SectionNumber getSectionNumber() {
		// return nothing
		return null;
	}
	public boolean iterateSections(IterateSectionsHandler handler) {
		Iterator<ViewReference> rit = childReferences.iterator();
		while ( rit.hasNext() ) {
			if ( !rit.next().iterateSections(handler) ) return false; 
		}
		return true;
	}
	public String toString() {
        return "\n" + childReferences;
    }
/*
	public SectionView getSectionView() {
		ViewReference tRef = this;
		while ( !(tRef instanceof SectionView) ) {
			tRef = tRef.getChildReferences().get(0);
		}
		return (SectionView) tRef;
	}
*/	
	public void setSectionNumber(SectionNumber sectionNumber) {
		// do nothing
	}
	// view layer support
	@Override
	public List<SectionView> getSections() {
		List<SectionView> sectionList = new ArrayList<SectionView>();
		for ( ViewReference opReference: childReferences ) {
			if ( opReference instanceof SectionView ) sectionList.add((SectionView)opReference);
		};
		return sectionList;
	}

	// Do the test here so that it doesn't need to be done in the presentation layer.
	@Override
	public List<ViewReference> getSubcodes() {
		List<ViewReference> referenceList = new ArrayList<ViewReference>();
		for ( ViewReference opReference: childReferences ) {
			if ( opReference instanceof SubcodeView ) referenceList.add(opReference);
		};
		return referenceList;
	}
	public List<SectionView> getSectionViews() {
		final List<SectionView> sectionList = new ArrayList<SectionView>();
		iterateSections(new IterateSectionsHandler() {			
			@Override
			public boolean handleOpinionSection(SectionView section) {
				sectionList.add(section);
				return true;
			}
		});
		return sectionList;
	}
/*
	public String getDisplayTitlePath() {
    	List<String> shortTitles = getSectionView().getShortTitles();
    	return shortTitles.toString().replace("[", "").replace("]", "") + ", " + getSectionView().getStatutesBaseClass().getTitle(true);
	}
	public String getDisplaySections() {
		StatuteRange statuteRange = getSectionView().getStatuteRange();
		if ( statuteRange.geteNumber().getSectionNumber() == null ) {
	    	return ("§ " + statuteRange.getsNumber().toString());
		}
    	return ("§§ " + statuteRange.toString());
	}
*/	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getImportance() {
		return importance;
	}
	public void setImportance(int importance) {
		this.importance = importance;
	}

	public int getCumulativeRefCount() {
		return cumulativeRefCount;
	}
	public String getFullFacet() {
		return fullFacet;
	}
	@Override
	public String getTitle() {
		return title;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fullFacet.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatuteView other = (StatuteView) obj;
		return fullFacet.equals(other.getFullFacet());
	}
	@Override
	public ViewReference getParent() {
		return null;
	}
}
