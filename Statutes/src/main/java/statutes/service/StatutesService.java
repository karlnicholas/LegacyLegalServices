package statutes.service;

import statutes.service.dto.KeyHierarchyPairs;
import statutes.service.dto.StatuteHierarchy;
import statutes.service.dto.StatuteKeyArray;
import statutes.service.dto.StatutesRootArray;
import statutes.service.dto.StatutesTitlesArray;

public interface StatutesService {

	String STATUTES = "statutes";
	String STATUTESTITLES = "statutestitles";
	String STATUTEHIERARCHY = "statutehierarchy";
	String STATUTESANDHIERARCHIES = "statutesandhierarchies";

	StatutesRootArray getStatutesRoots();

	StatutesTitlesArray getStatutesTitles();

	StatuteHierarchy getStatuteHierarchy(String fullFacet);

	KeyHierarchyPairs getStatutesAndHierarchies(StatuteKeyArray statuteKeyArray);

}
