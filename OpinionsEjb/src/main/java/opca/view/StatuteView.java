package opca.view;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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

    @XmlTransient
    @Override
    public ArrayList<ViewReference> getChildReferences() {
    	return this.childReferences;
    }
    public void setChildReferences(ArrayList<ViewReference> childReferences) {
    	this.childReferences = childReferences;
    }
    @XmlTransient
    @Override
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
    @XmlTransient
	public List<SectionView> getSectionViews() {
		final List<SectionView> sectionList = new ArrayList<SectionView>();
		iterateSections( s -> {sectionList.add(s); return true;});
		return sectionList;
	}
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
