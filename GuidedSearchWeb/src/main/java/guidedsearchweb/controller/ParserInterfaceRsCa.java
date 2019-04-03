package guidedsearchweb.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.api.IStatutesApi;
import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;
import statutes.service.dto.StatuteHierarchy;

public class ParserInterfaceRsCa implements IStatutesApi {
	private static final URL serviceURL;	
	static {
		try {
			String s = System.getenv("statutesrsservice");
			if ( s != null )
				serviceURL = new URL(s);
			else 
				serviceURL = new URL("http://localhost:8080/statutesrs/rs/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private StatutesService statutesService;
	public ParserInterfaceRsCa() {
		statutesService = new StatutesServiceClientImpl(serviceURL);
	}

	@Override
	public List<StatutesRoot> getStatutes() {
		List<StatutesRoot> statutesList = statutesService.getStatutesRoots().getItem();
//		for ( int i=0, l=objectList.size(); i<l; ++i ) {
//			statutesList.add(  (StatutesRoot) objectList.get(i) );
//		}		
		return statutesList;
	}

	@Override
	public StatutesBaseClass findReference(String title, SectionNumber sectionNumber) {
		return null;
	}

	@Override
	public StatutesTitles[] getStatutesTitles() {
		StatutesTitles[] statutesTitles; 
		List<StatutesTitles> statutesTitlesList = statutesService.getStatutesTitles().getItem();
		statutesTitles = new StatutesTitles[statutesTitlesList.size()];
		for (int i=0; i < statutesTitlesList.size(); ++i ) {
			statutesTitles[i] = (StatutesTitles) statutesTitlesList.get(i); 
		}
		return statutesTitles;
	}

	@Override
	public String getShortTitle(String title) {
		return null;
	}

	@Override
	public Map<String, StatutesTitles> getMapStatutesToTitles() {
		return null;
	}

	@Override
	public boolean loadStatutes() {
		return false;
	}

	@Override
	public StatutesRoot findReferenceByLawCode(String lawCode) {
		return null;
	}

	@Override
	public StatuteHierarchy getStatutesHierarchy(String fullFacet) {
		return statutesService.getStatuteHierarchy(fullFacet);
	}

	@Override
	public String getTitle(String lawCode) {
		return null;
	}

}
