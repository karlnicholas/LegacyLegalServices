package guidedsearch.restapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import client.StatutesRsService;
import parser.ParserInterface;
import service.StatutesService;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutesrs.StatuteHierarchy;

public class ParserInterfaceRsCa implements ParserInterface {
	private StatutesService statutesService;

	public ParserInterfaceRsCa() {
		init("http://localhost:8080/statutesrs/rs/");
	}
	
	public ParserInterfaceRsCa(final String defaultAddress) {
		init(defaultAddress);
	}

	private void init(final String defaultAddress) {
		try {
			String s = System.getenv("statutesrsservice");
			URL rsLocation;
			if ( s != null )
				rsLocation = new URL(s);
			else 
				rsLocation = new URL(defaultAddress);
			statutesService = new StatutesRsService(rsLocation).getRsService();
		} catch (MalformedURLException e) {
			throw new RuntimeException( e );
		}
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
	public String getFacetHead(String title) {
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
	public StatutesRoot findReferenceByTitle(String title) {
		return null;
	}

	@Override
	public StatuteHierarchy getStatutesHierarchy(String fullFacet) {
		return statutesService.getStatuteHierarchy(fullFacet);
	}

}
