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
@XmlAccessorType(XmlAccessType.FIELD)
public class SubcodeView extends ViewReference {
    private String fullFacet;
	private String part;
    private String partNumber;
    private String title;
    private int refCount;
    @XmlTransient
    private ViewReference parent;
    // and pointers to under Chapters, Parts, Articles, etc
    private ArrayList<ViewReference> childReferences;

    @Override
    public void initialize( StatutesBaseClass statutesNode, int refCount, ViewReference parent) {
    	childReferences = new ArrayList<ViewReference>();
    	this.fullFacet = statutesNode.getFullFacet();
    	this.part = statutesNode.getPart();
    	this.partNumber = statutesNode.getPartNumber();
    	this.title = statutesNode.getTitle();
    	this.refCount = refCount;
    	this.parent = parent;
    }
    
	public void trimToLevelOfInterest( int levelOfInterest ) {
    	Iterator<ViewReference> ori = childReferences.iterator();
    	while ( ori.hasNext() ) {
    		ViewReference opReference = ori.next();
    		int saveRefCount = opReference.getRefCount();
			opReference.trimToLevelOfInterest( levelOfInterest );
    		if ( opReference.getRefCount() < levelOfInterest ) {
    			// remove reference counts from removed node
    			incRefCount(0 - saveRefCount );
    			ori.remove();
    		}
    	}
    }

    public int compareTo( SubcodeView statutesNode ) {
        return refCount - statutesNode.getRefCount();
    }

    public int getRefCount() {
        return refCount;
    }

    public int incRefCount(int amount) {
    	refCount = refCount + amount;
        return refCount;
    }

	public ArrayList<ViewReference> getChildReferences() {
		return childReferences;
	}
	
	public boolean iterateSections(IterateSectionsHandler handler) {
		Iterator<ViewReference> rit = childReferences.iterator();
		while ( rit.hasNext() ) {
			if ( !rit.next().iterateSections(handler) ) return false; 
		}
		return true;
	}

/*
	public SectionView getSectionView() {
		ViewReference tRef = this;
		while ( !(tRef instanceof SectionView) ) {
			tRef = tRef.getChildReferences().get(0);
		}
		return (SectionView) tRef;
	}

	// Do the test here so that it doesn't need to be done in the presentation layer.
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
			if ( opReference instanceof SubcodeView ) referenceList.add((SubcodeView)opReference);
		};
		return referenceList;
	}
*/
	public String getFullFacet() {
		return fullFacet;
	}

	public String getPart() {
		return part;
	}

	public String getPartNumber() {
		return partNumber;
	}

	@Override
	public String getTitle() {
		return title;
	}
    @Override
	public ViewReference getParent() {
		return parent;
	}

	public void setParent(ViewReference parent) {
		this.parent = parent;
	}

	@Override
	public String getShortTitle() {
		return title;
	}
}
