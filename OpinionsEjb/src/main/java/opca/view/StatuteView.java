package opca.view;

import java.util.*;

import statutes.SectionNumber;
import statutes.StatuteRange;
import statutes.StatutesBaseClass;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 6/7/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatuteView implements ViewReference, Comparable<StatuteView> {
    // This is holding the "chapter" section that is of interest ..
    private int cumulativeRefCount;
    private ArrayList<ViewReference> childReferences;
    
    // Well, this should really be the toplevel object in the codes hierarchy ..
    // but, lets set it to this for now, come back to it later ..
    private StatutesBaseClass statutesBaseClass;
    private StatutesBaseClass leafBaseClass;
    
    // this thing again
    private QueueUtility queue;
    
	private int score;
	private int importance;    

    public StatuteView() {}
    // the old one was .. Section section, OpinionSection opSectionReference
    // Section section, OpinionSection opSectionReference
    public StatuteView(StatutesBaseClass code, StatutesBaseClass leafBaseClass) {
        this.statutesBaseClass = code;
        this.leafBaseClass = leafBaseClass;
        childReferences = new ArrayList<ViewReference>();
        queue = new QueueUtility(); 
        // even so .. we'll have think about this .. 
        // no references initially .. 
        cumulativeRefCount = 0;
    }
    
    public void trimToLevelOfInterest( int levelOfInterest ) {
    	Iterator<ViewReference> ori = childReferences.iterator();
    	while ( ori.hasNext() ) {
    		ViewReference opReference= ori.next();
    		opReference.trimToLevelOfInterest( levelOfInterest );	
    		if ( opReference.getRefCount() < levelOfInterest ) {
    			ori.remove();
    		}
    	}
    }

    // push the new SectionReference and all its parents onto a stack, 
    // then call the mergeSubcodes so the stack can combine parents that
    // are the same
    public void addNewSectionReference( ViewReference opReference ) {
		queue.push(opReference);
		//
	    while ( opReference.getStatutesBaseClass().getParent() != null ) {
	    	// First, check to see if are at the top ...
	    	StatutesBaseClass statutesBaseClass = opReference.getStatutesBaseClass().getParent();
	    	if ( statutesBaseClass.getParent() != null  ) {
//		    	subcode = new OpinionSubcode( codeSection, subcode );
		    	opReference = new SubcodeView( statutesBaseClass, opReference.getRefCount() );
		        queue.push(opReference);
	    	} else {
	    		break;
	    	}
	    }
	    // Merge the queue into the grand hierarchy 
	    incRefCount( queue.mergeSubcodes(childReferences).getRefCount() );
    }    

    public int compareTo( StatuteView statuteView) {
//      return 0;
//        return cumulativeRefCount - statuteView.getRefCount();
    	StatutesBaseClass baseClass = getStatutesBaseClass();
//    	StatuteRange range = baseClass.getStatuteRange();
    	StatutesBaseClass cBaseClass = statuteView.childReferences.get(0).getStatutesBaseClass();
//    	StatuteRange cRange = cBaseClass.getStatuteRange();
    	int bCompare = baseClass.getStatutesRoot().compareTo(cBaseClass.getStatutesRoot());
    	if ( bCompare == 0 ) {
/*    	
    		int sComp = range.getsNumber().compareTo(cRange.getsNumber());
    		int eComp = range.geteNumber().compareTo(cRange.geteNumber());
    		if ( ((sComp < 0) && (eComp < 0)) || ((sComp > 0) && (eComp > 0)) ) {
    			return sComp;
    		} else {
*/    		
    			SectionView sectionView = getSectionView();
    			SectionView sectionViewOther = statuteView.getSectionView();
		    	SectionNumber sNumber = sectionView.getStatutesBaseClass().getStatutesLeaf().getStatuteRange().getsNumber();
		    	SectionNumber sNumberOther = sectionViewOther.getStatutesBaseClass().getStatutesLeaf().getStatuteRange().getsNumber();
		    	return sNumber.compareTo(sNumberOther);
//    		}
    	} else {
    		return bCompare;
    	}
/*		    	
*/    	
    }

    public StatutesBaseClass getStatutesBaseClass() {
    	return statutesBaseClass;
    }
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
            
	public void incorporateOpinionReference(ViewReference opReference, QueueUtility queue) {
        incRefCount(opReference.getRefCount());
	}
	public void addReference(ViewReference opReference) {
		childReferences.add(opReference);
	}
	public SectionNumber getSectionNumber() {
		// return nothing
		return null;
	}
	public void addToChildren(QueueUtility queueUtility) {
        if ( queue.size() > 0 ) {
            queue.mergeSubcodes( childReferences);
        }
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

	public SectionView getSectionView() {
		ViewReference tRef = this;
		while ( !(tRef instanceof SectionView) ) {
			tRef = tRef.getChildReferences().get(0);
		}
		return (SectionView) tRef;
	}
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
	public String getDisplaySectionNumber() {
		if ( childReferences.size() == 1 ) 
			return childReferences.get(0).getSectionView().getDisplaySectionNumber(); 
		StatuteRange statuteRange = null;
		SectionView sectionView = null;
	    for ( ViewReference viewReference: childReferences ) {
	    	sectionView = viewReference.getSectionView();
	    	if ( statuteRange == null ) {
		    	statuteRange = new StatuteRange(sectionView.getSectionNumber(), sectionView.getSectionNumber());
	    	}
	    	statuteRange.mergeRange(new StatuteRange(sectionView.getSectionNumber(), sectionView.getSectionNumber()));
	    }
    	return "§§ " + statuteRange.toString();
	}
*/	
	public String getDisplayTitlePath() {
    	List<String> shortTitles = getSectionView().getShortTitles();
    	return shortTitles.toString().replace("[", "").replace("]", "") + ", " + getSectionView().getStatutesBaseClass().getTitle(true);
	}
	public String getDisplaySections() {
		StatuteRange statuteRange = getSectionView().getStatutesBaseClass().getStatuteRange();
		if ( statuteRange.geteNumber().getSectionNumber() == null ) {
	    	return ("§ " + statuteRange.getsNumber().toString());
		}
    	return ("§§ " + statuteRange.toString());
	}
/*	
    private void getAllSectionViews(List<SectionView> sortedSectionViews) {
		handleSections(sortedSectionViews);
    	for ( ViewReference subcode: getSubcodes() ) {
    		getAllSectionViews(sortedSectionViews);
    	}
    }
    private void handleSections(List<SectionView> sortedSectionViews) {
		for ( SectionView sectionView: getSections() ) {
			sortedSectionViews.add(sectionView); 
		}
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
	public StatutesBaseClass getLeafBaseClass() {
		return leafBaseClass;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leafBaseClass.getStatutesLeaf() == null) ? 0 : leafBaseClass.getStatutesLeaf().hashCode());
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
		if (leafBaseClass == null) {
			if (other.leafBaseClass != null)
				return false;
		} else if (!leafBaseClass.getStatutesLeaf().equals(other.leafBaseClass.getStatutesLeaf()))
			return false;
		return true;
	}
	
	
}
