package statutes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="@class" )
@JsonSubTypes({
      @JsonSubTypes.Type(value=StatutesRoot.class, name="statutesRoot"),
      @JsonSubTypes.Type(value=StatutesNode.class, name="statutesNode"),
      @JsonSubTypes.Type(value=StatutesLeaf.class, name="statutesLeaf")
  }) 
public interface StatutesBaseClass {
	
	public StatutesBaseClass findReference(SectionNumber sectionNumber);

	//	public String returnFullpath();
	public void mergeStatuteRange(StatuteRange statuteRange);
		
	public void addReference(StatutesBaseClass reference);

	public void getParents(ArrayList<StatutesBaseClass> returnPath);
	public List<StatutesBaseClass> getReferences();
	public void rebuildParentReferences(StatutesBaseClass parent);
	public String getTitle(boolean showPart);
    public String getFullTitle(String separator);
    
    // for typecasting
	public StatutesNode getStatutesNode();
	public StatutesLeaf getStatutesLeaf();	
	public StatutesRoot getStatutesRoot();

	// transferables
	public StatutesBaseClass getParent();
	public void setParent(StatutesBaseClass parent);
	public int getDepth();
	public void setDepth(int depth);
	public String getTitle();
	public void setTitle(String title);
	public String getShortTitle();
	public void setShortTitle(String shortTitle);
//	public String getFacetHead();
//	public void setFacetHead(String facetHead);
	public String getFullFacet();
	public void setFullFacet(String fullFacet);
	public String getPart();
	public void setPart(String part);
	public String getPartNumber();
	public void setPartNumber(String partNumber);
	public StatuteRange getStatuteRange();
	public void setStatuteRange(StatuteRange statuteRange);
	
	// return true to keep iterating, false to stop iteration
	public boolean iterateLeafs( IteratorHandler handler) throws Exception;
	
}
