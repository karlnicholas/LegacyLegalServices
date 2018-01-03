package statutes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Leaf node of Statute Hierarchy
 * @author Karl Nicholas
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
	"depth", "part", "partNumber", "statuteRange", 
	"title", "fullFacet", "sectionNumbers"
})
@SuppressWarnings("serial")
public class StatutesLeaf extends StatutesBaseClass {
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
    	if ( part != null ) {
    		this.part = Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
    	} else {
    		part = null;
    	}
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

	@XmlTransient
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
    
    @XmlTransient
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

/*
	//JAXB processor stuff
	@XmlAttribute(required=true)
	public String getCodeBegin() {
	    if ( statuteRange != null ) {
	    	if ( statuteRange.getsNumber() != null ) {
	    		return statuteRange.getsNumber().getSectionNumber();
	    	}
    	}
		return null;
	}
	public void setCodeBegin(String codeBegin) {}
	@XmlAttribute
	public String getCodeEnd() {
	    if ( statuteRange != null ) {
	    	if ( statuteRange.geteNumber() != null ) {
	    		return statuteRange.geteNumber().getSectionNumber();
	    	}
	    }
		return null;
	}
	public void setCodeEnd(String codeEnd) {}
	@XmlAttribute(required=true)
	public int getPosBegin() {
	    if ( statuteRange != null ) {
	    	if ( statuteRange.getsNumber() != null ) {
				return statuteRange.getsNumber().getPosition();
	    	}
	    }
		return -1;
	}
	public void setPosBegin(int posBegin) {}
	@XmlAttribute
	public int getPosEnd() {
	    if ( statuteRange != null ) {
	    	if ( statuteRange.geteNumber() != null ) {
	    		return statuteRange.geteNumber().getPosition();
	    	}
	    }
		return -1;
	}
	public void setPosEnd(int codeEnd) {}
*/	
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

	@XmlElement
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

	@Override
    @XmlTransient
	public StatutesRoot getStatutesRoot() {
		return parent.getStatutesRoot();
	}
	@Override
    @XmlTransient
	public StatutesLeaf getStatutesLeaf() {
		return this;
	}
	@XmlElement
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
	@Override
	@XmlAttribute
	public String getPart() {
        return part;
    }
	@Override
	public void setPart(String part) {
		this.part = part;
	}
	@Override
	@XmlAttribute
    public String getPartNumber() {
        return partNumber;
    }
	@Override
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	@XmlElement
	@Override
	public StatuteRange getStatuteRange() {
		return statuteRange;
	}
	@Override
	public void setStatuteRange(StatuteRange statuteRange) {
		this.statuteRange = statuteRange;
	}
	@XmlAttribute(required=true)
	@Override
	public int getDepth() {
		return depth;
	}
	@Override
	public void setDepth(int depth) {
		this.depth = depth;
	}
	@Override
	public StatutesNode getStatutesNode() {
		return null;
	}
	@XmlAttribute
	@Override
	public String getFullFacet() {
		return fullFacet;
	}
	@Override
	public void setFullFacet(String fullFacet) {
		this.fullFacet = fullFacet;
	}

	@XmlTransient
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

}
