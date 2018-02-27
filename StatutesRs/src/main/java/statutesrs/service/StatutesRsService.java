package statutesrs.service;

import java.util.Arrays;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import parser.ParserInterface;
import service.Client;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesTitles;
import statutesca.factory.CAStatutesFactory;
import statutesrs.ReferencesWithReferences;
import statutesrs.ResponseArray;
import statutesrs.ResponsePair;
import statutesrs.StatuteKey;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

@Path("/")
public class StatutesRsService implements Client {
	private ParserInterface parserInterface;

	public StatutesRsService() {
		parserInterface = CAStatutesFactory.getInstance().getParserInterface(true);
	}

	@Path("xxx")
	@GET
	@Produces("text/plain")
	public String getHello() {
		return "hello";
	}

	@Override
	@Path("statutes")
	@GET
	@Produces("application/json")
	public StatutesRootArray getStatutes() {
		StatutesRootArray statutesRootArray = new StatutesRootArray();
		statutesRootArray.getItem().addAll(parserInterface.getStatutes());
		return statutesRootArray;
	}

	@Override
	@Path("statutestitles")
	@GET
	@Produces("application/json")
	public StatutesTitlesArray getStatutesTitles() {
		StatutesTitles[] statutesTitles = parserInterface.getStatutesTitles();
		StatutesTitlesArray statutesTitlesArray = new StatutesTitlesArray();
		statutesTitlesArray.getItem().addAll(Arrays.asList(statutesTitles));
		return statutesTitlesArray;
	}
	
	@Override
	@Path("referencebytitle")
	@GET
	@Produces("application/json")
	public ReferencesWithReferences returnReferencesByTitle(@QueryParam("fullFacet") String fullFacet) {
		return parserInterface.returnReferencesByTitle(fullFacet);
	}

	@Override
	@Path("findstatutes")
	@GET
	@Produces("application/json")
	public ResponseArray findStatutes(StatuteKeyArray keys) {
		ResponseArray responseArray = new ResponseArray();
		// copy results into the new list ..
		// Fill out the codeSections that these section are referencing ..
		// If possible ...
		Iterator<StatuteKey> itc = keys.getItem().iterator();
		while (itc.hasNext()) {
			StatuteKey key = itc.next();
			// StatuteCitation citation = parserResults.findStatute(key);
			// This is a section
			String code = key.getCode();
			SectionNumber sectionNumber = new SectionNumber();
			sectionNumber.setPosition(-1);
			sectionNumber.setSectionNumber(key.getSectionNumber());
			// int refCount = citation.getRefCount(opinionBase.getOpinionKey());
			// boolean designated = citation.getDesignated();
			if (code != null) {
				// here we look for the Doc Section within the Code Section Hierachary
				// and place it within the sectionReference we previously parsed out of the
				// opinion
				StatutesBaseClass statutesBaseClass = parserInterface.findReference(code, sectionNumber);
				if (statutesBaseClass != null) {
					ResponsePair responsePair = new ResponsePair();
					responsePair.setStatuteKey(key);
					responsePair.setStatutesBaseClass(statutesBaseClass);
					responseArray.getItem().add(responsePair);
				}
				// Section codeSection = codeList.findCodeSection(sectionReference);
				// We don't want to keep ones that we can't map .. so ..
			}
		}
		return responseArray;
	}
}
