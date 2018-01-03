package statutes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
@XmlSeeAlso({StatutesRoot.class, StatutesNode.class, StatutesLeaf.class})
@SuppressWarnings("serial")
public abstract class StatutesBaseClass implements Serializable {
	
	public abstract StatutesBaseClass findReference(SectionNumber sectionNumber);

	//	public String returnFullpath();
	public abstract void mergeStatuteRange(StatuteRange statuteRange);
		
	public abstract void addReference(StatutesBaseClass reference);

	public abstract void getParents(ArrayList<StatutesBaseClass> returnPath);
	public abstract List<StatutesBaseClass> getReferences();
	public abstract void rebuildParentReferences(StatutesBaseClass parent);
	public abstract String getTitle(boolean showPart);
    public abstract String getFullTitle(String separator);
    
    // for typecasting
	public abstract StatutesNode getStatutesNode();
	public abstract StatutesLeaf getStatutesLeaf();	
	public abstract StatutesRoot getStatutesRoot();

	// transferables
	public abstract StatutesBaseClass getParent();
	public abstract void setParent(StatutesBaseClass parent);
	public abstract int getDepth();
	public abstract void setDepth(int depth);
	public abstract String getTitle();
	public abstract void setTitle(String title);
	public abstract String getShortTitle();
	public abstract void setShortTitle(String shortTitle);
//	public abstract String getFacetHead();
//	public abstract void setFacetHead(String facetHead);
	public abstract String getFullFacet();
	public abstract void setFullFacet(String fullFacet);
	public abstract String getPart();
	public abstract void setPart(String part);
	public abstract String getPartNumber();
	public abstract void setPartNumber(String partNumber);
	public abstract StatuteRange getStatuteRange();
	public abstract void setStatuteRange(StatuteRange statuteRange);
	
	// return true to keep iterating, false to stop iteration
	public abstract boolean iterateLeafs( IteratorHandler handler) throws Exception;
	
}
