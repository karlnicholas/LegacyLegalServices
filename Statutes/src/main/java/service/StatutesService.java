package service;

import statutesrs.StatutesHierarchy;
import statutesrs.ResponseArray;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

public interface StatutesService {

	StatutesRootArray getStatutes();

	StatutesTitlesArray getStatutesTitles();

	StatutesHierarchy getStatutesForFacet(String fullFacet);

	ResponseArray findStatutes(StatuteKeyArray statuteKeyArray);

}
