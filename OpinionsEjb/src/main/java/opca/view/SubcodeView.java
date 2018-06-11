package opca.view;

import java.util.*;

import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesNode;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 6/7/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubcodeView implements ViewReference {
    // This is holding the "chapter" section that is of interest ..
	private StatutesNode statutesNode;
    private int cumulativeRefCount;
    // and pointers to under Chapters, Parts, Articles, etc
    private ArrayList<ViewReference> childReferences;

    public SubcodeView() {}
    // Construct a branch .. with a 
    public SubcodeView(StatutesNode statutesNode, int cumulativeRefCount ) {
    	childReferences = new ArrayList<ViewReference>();
    	this.statutesNode = statutesNode;
    	this.cumulativeRefCount = cumulativeRefCount;
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

    public StatutesBaseClass getStatutesBaseClass() {
        return statutesNode;
    }

    public int getRefCount() {
        return cumulativeRefCount;
    }

    public int incRefCount(int amount) {
    	cumulativeRefCount = cumulativeRefCount + amount;
        return cumulativeRefCount;
    }

	
    public void incorporateOpinionReference( ViewReference opReference, QueueUtility queue ) {
        incRefCount(opReference.getRefCount());
        // basically, by ignoring the statutesNode it gets left out
        // we should so incorporate the increment count into the section .. 
        addToChildren(queue);
    }

	
    public void addToChildren( QueueUtility queue ) {
        if ( queue.size() > 0 ) {
            queue.mergeSubcodes( childReferences);
        }
    }

    public void addReference(ViewReference opReference) {
		childReferences.add(opReference);
	}

	public ArrayList<ViewReference> getChildReferences() {
		return childReferences;
	}
	
	public void setSectionNumber(SectionNumber sectionNumber) {
		// do nothing here ..
	}
	
	public boolean iterateSections(IterateSectionsHandler handler) {
		Iterator<ViewReference> rit = childReferences.iterator();
		while ( rit.hasNext() ) {
			if ( !rit.next().iterateSections(handler) ) return false; 
		}
		return true;
	}

	
	public SectionNumber getSectionNumber() {
		// return nothing
		return null;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statutesNode == null) ? 0 : statutesNode.hashCode());
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
		SubcodeView other = (SubcodeView) obj;
		if (statutesNode == null) {
			if (other.statutesNode != null)
				return false;
		} else if (!statutesNode.equals(other.statutesNode))
			return false;
		return true;
	}
}
