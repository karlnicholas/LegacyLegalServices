package service;

import statutesrs.StatuteHierarchy;
import statutesrs.KeyHierarchyPairs;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

public interface StatutesService {

	StatutesRootArray getStatutesRoots();

	StatutesTitlesArray getStatutesTitles();

	StatuteHierarchy getStatuteHierarchy(String fullFacet);

	KeyHierarchyPairs getStatutesAndHierarchies(StatuteKeyArray statuteKeyArray);

}
