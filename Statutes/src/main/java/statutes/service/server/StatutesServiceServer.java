package statutes.service.server;

import java.util.Arrays;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesTitles;
import statutes.api.IStatutesApi;
import statutes.service.StatutesService;
import statutes.service.dto.KeyHierarchyPair;
import statutes.service.dto.KeyHierarchyPairs;
import statutes.service.dto.StatuteHierarchy;
import statutes.service.dto.StatuteKey;
import statutes.service.dto.StatuteKeyArray;
import statutes.service.dto.StatutesRootArray;
import statutes.service.dto.StatutesTitlesArray;

@Path("/")
public class StatutesServiceServer implements StatutesService {
	private IStatutesApi iStatutesApi = ApiImplSingleton.getInstance().getStatutesApi();

	@Override
	@Path(StatutesService.STATUTES)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StatutesRootArray getStatutesRoots() {
		StatutesRootArray statutesRootArray = new StatutesRootArray();
		statutesRootArray.getItem().addAll(iStatutesApi.getStatutes());
		return statutesRootArray;
	}

	@Override
	@Path(StatutesService.STATUTESTITLES)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StatutesTitlesArray getStatutesTitles() {
		StatutesTitles[] statutesTitles = iStatutesApi.getStatutesTitles();
		StatutesTitlesArray statutesTitlesArray = new StatutesTitlesArray();
		statutesTitlesArray.getItem().addAll(Arrays.asList(statutesTitles));
		return statutesTitlesArray;
	}

	@Override
	@Path(StatutesService.STATUTEHIERARCHY)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StatuteHierarchy getStatuteHierarchy(@QueryParam("fullFacet") String fullFacet) {
		return iStatutesApi.getStatutesHierarchy(fullFacet);
	}

	@Override
	@Path(StatutesService.STATUTESANDHIERARCHIES)
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
				StatutesBaseClass statutesBaseClass = iStatutesApi.findReference(title, sectionNumber);
				if (statutesBaseClass != null) {
					StatuteHierarchy statuteHierarchy = iStatutesApi
							.getStatutesHierarchy(statutesBaseClass.getFullFacet());
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
