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
	
    public StatutesBaseClass findReference(String lawCode, SectionNumber sectionNumber);
    public StatutesTitles[] getStatutesTitles();
    public String getShortTitle(String lawCode);
	public String getFullTitle(String lawCode);
    public Map<String, StatutesTitles> getMapStatutesToTitles();
    
    public boolean loadStatutes();	// no exceptions allowed

	public StatutesRoot findReferenceByLawCode(String lawCode);
    
	public StatuteHierarchy getStatutesHierarchy(String lawCode);


}