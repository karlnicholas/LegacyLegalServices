package opca.view;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import statutes.StatutesBaseClass;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 6/7/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class StatuteView extends ViewReference implements Comparable<StatuteView> {

    private String fullFacet; 
    private String title; 
    private String shortTitle;
    private int refCount;
    private ArrayList<ViewReference> childReferences;

	/**
	 * 
	 * @param statutesRoot
	 * @param refCount
	 */
	@Override
    public void initialize(StatutesBaseClass statutesRoot, int refCount, ViewReference parent) {
    	this.refCount = refCount;
    	fullFacet = statutesRoot.getFullFacet();
    	title = statutesRoot.getTitle();
    	shortTitle = statutesRoot.getShortTitle();
        childReferences = new ArrayList<ViewReference>();
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
    @XmlTransient
    public ArrayList<ViewReference> getChildReferences() {
    	return this.childReferences;
    }
    public void setChildReferences(ArrayList<ViewReference> childReferences) {
    	this.childReferences = childReferences;
    }
    @XmlTransient
    public int getRefCount() {
        return refCount;
    }
    public int incRefCount(int amount) {
    	refCount = refCount + amount;
        return refCount;
    }
    public void setRefCount(int count) {
        refCount = count;
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
*/	
    @XmlTransient
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
    @XmlTransient
	public String getFullFacet() {
		return fullFacet;
	}
    @XmlTransient
	@Override
	public String getTitle() {
		return title;
	}
    @XmlTransient
	@Override
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
    @XmlTransient
	public ViewReference getParent() {
		return null;
	}

}
