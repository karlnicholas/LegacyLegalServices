package statutesrs.service;

import java.util.Arrays;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import client.ClientImpl;
import parser.ParserInterface;
import service.StatutesService;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesTitles;
import statutesca.factory.CAStatutesFactory;
import statutesrs.StatuteHierarchy;
import statutesrs.KeyHierarchyPairs;
import statutesrs.KeyHierarchyPair;
import statutesrs.StatuteKey;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

@Path("/")
public class StatutesRsService implements StatutesService {
	private ParserInterface parserInterface;

	public StatutesRsService() {
		parserInterface = CAStatutesFactory.getInstance().getParserInterface(true);
	}

	@Override
	@Path(ClientImpl.STATUTES)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StatutesRootArray getStatutesRoots() {
		StatutesRootArray statutesRootArray = new StatutesRootArray();
		statutesRootArray.getItem().addAll(parserInterface.getStatutes());
		return statutesRootArray;
	}

	@Override
	@Path(ClientImpl.STATUTESTITLES)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StatutesTitlesArray getStatutesTitles() {
		StatutesTitles[] statutesTitles = parserInterface.getStatutesTitles();
		StatutesTitlesArray statutesTitlesArray = new StatutesTitlesArray();
		statutesTitlesArray.getItem().addAll(Arrays.asList(statutesTitles));
		return statutesTitlesArray;
	}
	
	@Override
	@Path(ClientImpl.REFERENCEBYTITLE)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StatuteHierarchy getStatuteHierarchy(@QueryParam("fullFacet") String fullFacet) {
		return parserInterface.getStatutesHierarchy(fullFacet);
	}

	@Override	
	@Path(ClientImpl.FINDSTATUTES)
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public KeyHierarchyPairs getStatutesAndHierarchies(StatuteKeyArray keys) {
		KeyHierarchyPairs keyHierarchyPairs = new KeyHierarchyPairs();
		// copy results into the new list ..
		// Fill out the codeSections that these section are referencing ..
		// If possible ...
		Iterator<StatuteKey> itc = keys.getItem().iterator();
		while (itc.hasNext()) {
			StatuteKey key = itc.next();
			// StatuteCitation citation = parserResults.findStatute(key);
			// This is a section
			String title = key.getTitle();
			SectionNumber sectionNumber = new SectionNumber();
			sectionNumber.setPosition(-1);
			sectionNumber.setSectionNumber(key.getSectionNumber());
			// int refCount = citation.getRefCount(opinionBase.getOpinionKey());
			// boolean designated = citation.getDesignated();
			if (title != null) {
				// here we look for the Doc Section within the Code Section Hierachary
				// and place it within the sectionReference we previously parsed out of the
				// opinion
				StatutesBaseClass statutesBaseClass = parserInterface.findReference(title, sectionNumber);
				if (statutesBaseClass != null) {
					StatuteHierarchy statuteHierarchy = parserInterface.getStatutesHierarchy( statutesBaseClass.getFullFacet() );
					KeyHierarchyPair keyHierarchyPair = new KeyHierarchyPair();
					keyHierarchyPair.setStatuteKey(key);
					keyHierarchyPair.setStatutesPath(statuteHierarchy.getStatutesPath());
					keyHierarchyPairs.getItem().add(keyHierarchyPair);
				}
				// Section codeSection = codeList.findCodeSection(sectionReference);
				// We don't want to keep ones that we can't map .. so ..
			}
		}
		return keyHierarchyPairs;
	}

}
