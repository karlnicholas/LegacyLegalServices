package opca.view;

import java.util.*;

import statutes.StatutesBaseClass;
import statutes.StatutesNode;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 6/7/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubcodeView extends ViewReference {
    // This is holding the "chapter" section that is of interest ..
//	private StatutesNode statutesNode;
    private String fullFacet;
	private String part;
    private String partNumber;
    private String title;
	
    private int cumulativeRefCount;
    // and pointers to under Chapters, Parts, Articles, etc
    private ArrayList<ViewReference> childReferences;
	private ViewReference parent;

    public SubcodeView( StatutesBaseClass statutesNode, int refCount) {
    	childReferences = new ArrayList<ViewReference>();
//    	this.statutesNode = statutesNode;
    	this.fullFacet = statutesNode.getFullFacet();
    	this.part = statutesNode.getPart();
    	this.partNumber = statutesNode.getPartNumber();
    	this.title = statutesNode.getTitle();
    	cumulativeRefCount = refCount;
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
        return cumulativeRefCount - statutesNode.getRefCount();
    }

    public int getRefCount() {
        return cumulativeRefCount;
    }

    public int incRefCount(int amount) {
    	cumulativeRefCount = cumulativeRefCount + amount;
        return cumulativeRefCount;
    }

    public void addReference(ViewReference opReference) {
		childReferences.add(opReference);
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

	@Override
	public ViewReference getParent() {
		return parent;
	}
	public void setParent(ViewReference parent) {
		this.parent = parent;
	}

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

	public int getCumulativeRefCount() {
		return cumulativeRefCount;
	}	
}
