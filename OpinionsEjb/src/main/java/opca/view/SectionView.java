package opca.view;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import statutes.StatuteRange;
import statutes.StatutesBaseClass;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/27/12
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SectionView extends ViewReference { 
	//	private static final Logger logger = Logger.getLogger(OpinionSection.class.getName());
	// This stuff holds the reference .. 
	// Which "code" it is and which section within that code is referenced
	// Also a place for the number of reference counts
	// as well as "designated," a working variable that shows how "strong" the reference is
    private String title;
    private String fullFacet;
    private StatuteRange statuteRange;
    private int refCount;
	private int score;
	private int importance;    
    @XmlTransient
    private ViewReference parent;
    
    // This holds the CodeRefence to the 
//    private StatutesBaseClass section;

    public void initialize(StatutesBaseClass statutesLeaf, int refCount, ViewReference parent) {
    	// this is constructed without a parent and that's added later
    	// when we build the hierarchy
//    	logger.fine("code:" + code + ":section:" + section);
        title = statutesLeaf.getTitle();
        fullFacet = statutesLeaf.getFullFacet();
        this.refCount = refCount;
        statuteRange = statutesLeaf.getStatuteRange();
        this.setParent(parent);
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
		SectionView other = (SectionView) obj;
		if (fullFacet == null) {
			if (other.fullFacet != null)
				return false;
		} else if (!fullFacet.equals(other.fullFacet))
			return false;
		return true;
	}

    public StatuteRange getStatuteRange() {
        return statuteRange;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle( String code) {
    	this.title = code;
    }
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

/*    
    public void setStatutesBaseClass( StatutesBaseClass section ) {
    	this.section = section;
    }

    @Override
    public StatutesBaseClass getStatutesBaseClass( ) {
    	return section;
    }
*/	
	public void addReference(ViewReference reference) {
		// do nothing
	}

	public ArrayList<ViewReference> getChildReferences() {
		// nothing to return
		return null;
	}
	
	public void trimToLevelOfInterest(int levelOfInterest) {
		// nothing to do 
	}
	
	public boolean iterateSections(IterateSectionsHandler handler) {
		return handler.handleOpinionSection(this);
	}
	
	public SectionView getSectionView() {
		return this;
	}

    public String toString() {
        return title + ":" + statuteRange + ":" + refCount;
    }

    @Override
	public ViewReference getParent() {
		return parent;
	}

	public void setParent(ViewReference parent) {
		this.parent = parent;
	}
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

	public String getDisplayTitlePath() {
    	List<String> shortTitles = getShortTitles();
    	return shortTitles.toString().replace("[", "").replace("]", "") + ", " + title;
	}
	public String getDisplaySections() {
		if ( statuteRange.geteNumber().getSectionNumber() == null ) {
	    	return ("§ " + statuteRange.getsNumber().toString());
		}
    	return ("§§ " + statuteRange.toString());
	}

    
	public List<String> getShortTitles() {
    	ArrayList<ViewReference> baseViews = new ArrayList<ViewReference>();
    	ViewReference parent = this.parent;
    	while ( parent != null ) {
    		baseViews.add(parent);
    		parent = parent.getParent();
    	}
		List<String> shortTitles = new ArrayList<String>();
		Collections.reverse(baseViews);
		for ( ViewReference baseView: baseViews) {
			shortTitles.add(baseView.getShortTitle());
		}
    	return shortTitles;
	}

	@Override
	public String getShortTitle() {
		return title;
	}
}

