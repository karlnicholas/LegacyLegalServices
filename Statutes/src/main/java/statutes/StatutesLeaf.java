package statutes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Leaf node of Statute Hierarchy
 * @author Karl Nicholas
 *
 */
@JsonTypeName("statutesLeaf")
@JsonPropertyOrder({
	"depth", "part", "partNumber", "statuteRange", 
	"title", "fullFacet", "sectionNumbers"
})
@SuppressWarnings("serial")
public class StatutesLeaf implements StatutesBaseClass, Serializable {
	//	private static final Logger logger = Logger.getLogger(Section.class.getName());
    private StatutesBaseClass parent;
    private String fullFacet;
    // this is the subdivision name
    private String part;
    // and the subdivision number
    private String partNumber;
    private String title;
//    private SectionRange sectionRange;
    private StatuteRange statuteRange;
    // keep track of how deep we are ..
    // always > 0 for sections
    private int depth;
    // optionally, keep track of the sectionNumbers within this CodeReference
    // section numbers are just increment integers for each section.
    // They define the correct ordering
    // can be a range if this leaf represents several sections.
    private ArrayList<SectionNumber> sectionNumbers;

    public StatutesLeaf() {
    	sectionNumbers = new ArrayList<SectionNumber>();
    }
//    public Section (String line, boolean statuteRange, Section p, int level) throws CodeException {
    public StatutesLeaf (
		StatutesBaseClass parent, 
		String fullFacet, 
		String part, 
		String partNumber, 
		String title, 
		int depth, 
		StatuteRange range
	) {
    	this.sectionNumbers = new ArrayList<>();
    	this.parent = parent;
    	this.fullFacet = fullFacet;
/*    	
    	if ( part != null ) {
    		this.part = Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
    	} else {
    		part = null;
    	}
*/    	
    	this.part = part;
    	this.partNumber = partNumber;
    	this.title = title;
    	this.statuteRange = range;
    	this.depth = depth;
    	assert( depth>0 );
    }
    
    @Override
	public StatutesBaseClass findReference(SectionNumber sectionNumber) {
		for ( SectionNumber sNumber: sectionNumbers ) {
			if ( sNumber.equals(sectionNumber)) return this;
		}
		return null;
	}

	@Override
	public boolean iterateLeafs(IteratorHandler handler) throws Exception {
		return handler.handleSection(this);
	}

	@JsonIgnore
//	@XmlElement
    public StatutesBaseClass getParent() {
    	return parent;
    }
    
    public void setParent( StatutesBaseClass parent ) {
    	this.parent = parent;
//    	parent.addReference(this);
    }
    
    @Override
    public void addReference( StatutesBaseClass reference ) {
    }
    
	@JsonIgnore
    @Override
	public List<StatutesBaseClass> getReferences() {
		return null;
	}
	public void setReferences(ArrayList<StatutesBaseClass> references) {
	}
	public void rebuildParentReferences(StatutesBaseClass parent) {
		this.parent = parent;
	}
    @Override
    public String getFullTitle(String separator) {
        String ret = new String();
        if ( part != null && partNumber != null ) ret = (part+" "+partNumber+". ");
        if ( title != null ) ret = ret + title;
        return parent.getFullTitle(separator)+separator+ret;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if ( part != null ) {
        	ret.append(part);
        	ret.append(" ");
        }
        if ( partNumber != null ) {
        	ret.append(partNumber);
        	ret.append(".");
        }
        if ( title != null ) {
        	ret.append(" ");
        	ret.append(title);
        }
        if ( statuteRange != null ) {
        	ret.append(":");
        	ret.append(statuteRange);
        }
        return ret.toString();
    }

	public void mergeStatuteRange(StatuteRange statuteRange) {}

	@JsonInclude
	public ArrayList<SectionNumber> getSectionNumbers() {
		return sectionNumbers;
	}
	public void setSectionNumbers(ArrayList<SectionNumber> sectionNumbers) {
		this.sectionNumbers = sectionNumbers;
	}

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
	public StatutesLeaf getStatutesLeaf() {
		return this;
	}
	@JsonIgnore
	@Override
	public StatutesNode getStatutesNode() {
		return null;
	}
	@JsonInclude
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
    public String getTitle(boolean showPart) {
        String ret = new String();
        if ( showPart) {
        	if ( part != null && partNumber != null ) ret = (part+" "+partNumber+". ");
        }
        if ( title != null ) ret = ret + title;
        return ret.toString();
    }
	@JsonInclude
	@Override
	public String getPart() {
        return part;
    }
	@Override
	public void setPart(String part) {
		this.part = part;
	}
	@JsonInclude
	@Override
    public String getPartNumber() {
        return partNumber;
    }
	@Override
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	@JsonInclude
	@Override
	public StatuteRange getStatuteRange() {
		return statuteRange;
	}
	@Override
	public void setStatuteRange(StatuteRange statuteRange) {
		this.statuteRange = statuteRange;
	}
	@JsonInclude
	@Override
	public int getDepth() {
		return depth;
	}
	@Override
	public void setDepth(int depth) {
		this.depth = depth;
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
		StatutesLeaf other = (StatutesLeaf) obj;
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
	@Override
	public String getLawCode() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setLawCode(String lawCode) {
		// TODO Auto-generated method stub
		
	}

}
