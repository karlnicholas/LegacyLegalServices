package statutes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Root element of a Statute Hierarchy.
 */
@JsonTypeName("statutesRoot")
@JsonPropertyOrder({
		"depth", "part", "partNumber", "statuteRange", 
		"lawCode", "title", "shortTitle", "fullFacet"
	})
@SuppressWarnings("serial")
public class StatutesRoot implements StatutesBaseClass, Serializable, Comparable<StatutesRoot> {
	//	private static final Logger logger = Logger.getLogger(Code.class.getName());
	// lawCode = primary identifier
	private String lawCode;
    private String title;
    private String shortTitle;
    private String fullFacet;
    // Always 0 for code, increments for each level of children
    private int depth;
    private StatuteRange codeRange;
    private ArrayList<StatutesBaseClass> references;
    // wtf is this?
//    private ArrayList<CodeReference> comparableList;

    public StatutesRoot() {
    	references = new ArrayList<StatutesBaseClass>();
    }
    
    public StatutesRoot(String lawCode, String title, String shortTitle, String fullFacet) {
    	this(lawCode, title, shortTitle, fullFacet, new StatuteRange());
    }

    public StatutesRoot(String lawCode, String title, String shortTitle, String fullFacet, StatuteRange codeRange) {
    	references = new ArrayList<StatutesBaseClass>();
    	this.lawCode = lawCode;
    	this.codeRange = codeRange;
        this.title = title;
        this.shortTitle = shortTitle;
        this.fullFacet = fullFacet;
    	this.depth = 0;
    }

    public StatutesBaseClass findReference(SectionNumber sectionNumber) {
		Iterator<StatutesBaseClass> rit = references.iterator();
		while ( rit.hasNext() ) {
			StatutesBaseClass reference = rit.next().findReference(sectionNumber);
			if ( reference != null ) return reference;
		}
		return null;
	}

	@Override
	public boolean iterateLeafs(IteratorHandler handler) throws Exception {
		Iterator<StatutesBaseClass> rit = references.iterator();
		while ( rit.hasNext() ) {
			if ( !rit.next().iterateLeafs(handler) ) return false;
		}
		return true;
	}
	/**
	 * Internal function for settings the hierarchy's parent
	 * references after being loaded from XML.
	 * @param parent set null at this level.
	 */
	public void rebuildParentReferences(StatutesBaseClass parent) {
		for ( StatutesBaseClass reference: references ) {
			reference.rebuildParentReferences(this);
		}
	}

	@JsonIgnore
    public StatutesBaseClass getParent() {
    	return null;
    }

    public void setParent( StatutesBaseClass parent ) {
    }

	public void addReference(StatutesBaseClass reference) {
		references.add(reference);
    	mergeStatuteRange(reference.getStatuteRange());
	}
	@JsonIgnore
	@Override
	public ArrayList<StatutesBaseClass> getReferences() {
		return references;
	}
	@Override
    public String toString() {
    	String cString = title + ": " + references.size() + " references";
        return cString;
    }
	@JsonInclude
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
    public String getTitle(boolean showPart) {
        return title;
    }
    public String getFullTitle(String separator) {
        return title;
    }

	@JsonInclude
	@Override
    public String getShortTitle() {
        return shortTitle;
    }
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	@JsonInclude
	@Override
    public String getFullFacet() {
        return fullFacet;
    }
	public void setFullFacet(String fullFacet) {
		this.fullFacet = fullFacet;
	}
	@JsonInclude
	public int getDepth() {
		return depth;	// always 0
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}

	@JsonIgnore
	@Override
	public StatutesLeaf getStatutesLeaf() {
		return null;
	}
	@JsonIgnore
	@Override
	public StatutesNode getStatutesNode() {
		return null;
	}
	@JsonInclude
	public StatuteRange getStatuteRange() {
		return codeRange;
	}
	public void setStatuteRange(StatuteRange codeRange) {
		this.codeRange = codeRange;
	}
	public void mergeStatuteRange(StatuteRange codeRange) {
		this.codeRange.mergeRange(codeRange);
	}
	public void getParents(ArrayList<StatutesBaseClass> returnPath) {}

	@JsonIgnore
	@Override
	public StatutesRoot getStatutesRoot() {
		return this;
	}
	@Override
	public int compareTo(StatutesRoot o) {
		return this.title.compareTo(o.getTitle(false));
	}
	@Override
	public String getPart() {
		return null;
	}

	@Override
	public void setPart(String part) {
		// do nothing
		
	}
	@Override
	public String getPartNumber() {
		return null;
	}
	@Override
	public void setPartNumber(String partNumber) {
		// do nothing
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((shortTitle == null) ? 0 : shortTitle.hashCode());
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
		StatutesRoot other = (StatutesRoot) obj;
		if (shortTitle == null) {
			if (other.shortTitle != null)
				return false;
		} else if (!shortTitle.equals(other.shortTitle))
			return false;
		return true;
	}

	@JsonInclude
	@Override
	public String getLawCode() {
		return lawCode;
	}

	@Override
	public void setLawCode(String lawCode) {
		this.lawCode = lawCode;
	}

}