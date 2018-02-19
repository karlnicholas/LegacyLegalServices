package service;

import statutesrs.ReferencesWithReferences;
import statutesrs.ResponseArray;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

public interface StatutesRs {

	StatutesRootArray getStatutes();

	StatutesTitlesArray getStatutesTitles();

	ReferencesWithReferences returnReferencesByTitle(String fullFacet);

	ResponseArray findStatutes(StatuteKeyArray statuteKeyArray);

}
