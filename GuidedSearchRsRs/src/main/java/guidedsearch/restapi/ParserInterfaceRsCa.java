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
import statutesrs.StatutesHierarchy;

public class ParserInterfaceRsCa implements ParserInterface {
	private StatutesRsService service;

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
			service = new StatutesRsService(rsLocation);
		} catch (MalformedURLException e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public List<StatutesRoot> getStatutes() {
		StatutesService statutesRs = service.getRsService();
		List<StatutesRoot> statutesList = statutesRs.getStatutes().getItem();
//		for ( int i=0, l=objectList.size(); i<l; ++i ) {
//			statutesList.add(  (StatutesRoot) objectList.get(i) );
//		}		
		return statutesList;
	}

	@Override
	public StatutesBaseClass findReference(String title, SectionNumber sectionNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatutesTitles[] getStatutesTitles() {
		StatutesTitles[] statutesTitles; 
		StatutesService statutesRs = service.getRsService();
		List<StatutesTitles> statutesTitlesList = statutesRs.getStatutesTitles().getItem();
		statutesTitles = new StatutesTitles[statutesTitlesList.size()];
		for (int i=0; i < statutesTitlesList.size(); ++i ) {
			statutesTitles[i] = (StatutesTitles) statutesTitlesList.get(i); 
		}
		return statutesTitles;
	}

	@Override
	public String getShortTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFacetHead(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, StatutesTitles> getMapStatutesToTitles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean loadStatutes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StatutesRoot findReferenceByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatutesHierarchy getStatutesHierarchy(String fullFacet) {
		StatutesService statutesRs = service.getRsService();
		return statutesRs.getStatutesForFacet(fullFacet);
	}

}
