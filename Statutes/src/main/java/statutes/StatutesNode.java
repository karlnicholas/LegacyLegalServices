package statutes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("statutesNode")
@JsonPropertyOrder({
	"depth", "part", "partNumber", "statuteRange", 
	"title", "fullFacet" 
})
@SuppressWarnings("serial")
public class StatutesNode implements StatutesBaseClass, Serializable {
	//	private static final Logger logger = Logger.getLogger(Subcode.class.getName());
	private StatutesBaseClass parent;
    private String fullFacet;
	private String part;
    private String partNumber;
    private String title;
    private StatuteRange statuteRange;
    // keep track of how deep we are ..
    // Always > 0 for Subcode
    private int depth;
    // and pointers to under Chapters, Parts, Articles, etc
    private ArrayList<StatutesBaseClass> references;
        
    public StatutesNode() {
    	references = new ArrayList<StatutesBaseClass>();
    	statuteRange = new StatuteRange();
    }
    public StatutesNode(StatutesBaseClass parent, String fullFacet, String part, String partNumber, String title, int depth) {
    	this(parent, fullFacet, part, partNumber, title, depth, new StatuteRange());
    }
    public StatutesNode(StatutesBaseClass parent, String fullFacet, String part, String partNumber, String title, int depth, StatuteRange statuteRange) {
    	references = new ArrayList<StatutesBaseClass>();
    	this.statuteRange = statuteRange;
    	this.fullFacet = fullFacet;
    	
    	this.parent = parent;
    	if ( part != null ) {
    		this.part = Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
    	} else {
    		part = null;
    	}
    	this.partNumber = partNumber;
    	this.title = title;
    	this.depth = depth;
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

    public void addReference( StatutesBaseClass reference ) {
    	references.add(reference);
    	mergeStatuteRange(reference.getStatuteRange());
    }
	@JsonIgnore
    public ArrayList<StatutesBaseClass> getReferences() {
    	return references;
    }

	public void rebuildParentReferences(StatutesBaseClass parent) {
		this.parent = parent;
		for ( StatutesBaseClass reference: references ) {
			reference.rebuildParentReferences(this);
		}
	}

	@JsonIgnore
    public StatutesBaseClass getParent() {
    	return parent;
    }
    public void setParent( StatutesBaseClass parent ) {
    	this.parent = parent;
//    	parent.addReference(this);
    }

	@Override
	@JsonInclude
    public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
	@Override
	@JsonInclude
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	@Override
	public String getTitle(boolean showPart) {
        String ret = new String();
        if ( showPart ) {
        	if ( part != null && partNumber != null ) ret = (part+" "+partNumber+". ");
        }
        if ( title != null ) ret = ret + title;
		return ret.toString();
	}

    @Override
    public String getFullTitle(String separator) {
        String ret = new String();
        if ( part != null && partNumber != null ) ret = (part+" "+partNumber+". ");
        if ( title != null ) ret = ret + title;
        return parent.getFullTitle(separator)+separator+ret;
    }
	@JsonInclude
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String toString() {
        String indent = new String();
        String ret = indent + part + " " + partNumber + ":" + title;
        return ret;
    }

	@JsonInclude
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	@JsonIgnore
	@Override
	public StatutesLeaf getStatutesLeaf() {
		return null;
	}
	// can only assume that they are read in order
	// and therefore 
	public void mergeStatuteRange(StatuteRange statuteRange) {
    	this.statuteRange.mergeRange(statuteRange);
    	parent.mergeStatuteRange(this.statuteRange);		
	}
	@JsonInclude
	public StatuteRange getStatuteRange() {
		return statuteRange;
	}
	public void setStatuteRange(StatuteRange statuteRange) {
		this.statuteRange = statuteRange;
	}
	@Override
	public void getParents(ArrayList<StatutesBaseClass> returnPath) {
		returnPath.add(parent);
		parent.getParents(returnPath);
	}

	@JsonIgnore
	@Override
	public StatutesRoot getStatutesRoot() {
		return parent.getStatutesRoot();
	}
	@JsonIgnore
	@Override
	public StatutesNode getStatutesNode() {
		return this;
	}
	@JsonInclude
	@Override
	public String getFullFacet() {
		return fullFacet;
	}
	@Override
	public void setFullFacet(String fullFacet) {
		this.fullFacet = fullFacet;
	}

	@JsonIgnore
	@Override
	public String getShortTitle() {
        StringBuilder ret = new StringBuilder();
        if ( part != null ) {
        	ret.append(part);
        	ret.append(" ");
        }
        if ( partNumber != null ) {
        	ret.append(partNumber);
        }
        return ret.toString();
	}
	@Override
	public void setShortTitle(String shortTitle) {
		// do nothing
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((part == null) ? 0 : part.hashCode());
		result = prime * result + ((partNumber == null) ? 0 : partNumber.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		StatutesNode other = (StatutesNode) obj;
		if (part == null) {
			if (other.part != null)
				return false;
		} else if (!part.equals(other.part))
			return false;
		if (partNumber == null) {
			if (other.partNumber != null)
				return false;
		} else if (!partNumber.equals(other.partNumber))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
