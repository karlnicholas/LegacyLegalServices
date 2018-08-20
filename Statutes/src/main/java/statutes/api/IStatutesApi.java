package statutes.api;

import java.util.List;
import java.util.Map;

import statutes.StatutesTitles;
import statutes.service.dto.StatuteHierarchy;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesRoot;

public interface IStatutesApi {
	public List<StatutesRoot> getStatutes();
	
    public StatutesBaseClass findReference(String title, SectionNumber sectionNumber);

    public StatutesTitles[] getStatutesTitles();
    public String getShortTitle(String title);
    public String getFacetHead(String title);

    public Map<String, StatutesTitles> getMapStatutesToTitles();
    
    public boolean loadStatutes();	// no exceptions allowed

	public StatutesRoot findReferenceByTitle(String title);
    
	public StatuteHierarchy getStatutesHierarchy(String fullFacet);

}