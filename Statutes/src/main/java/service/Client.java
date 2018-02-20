package service;

import java.util.List;

import statutes.StatutesRoot;
import statutesrs.ReferencesWithReferences;
import statutesrs.ResponseArray;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

public interface Client {

	StatutesRootArray getStatutes();

	StatutesTitlesArray getStatutesTitles();

	ReferencesWithReferences returnReferencesByTitle(String fullFacet);

	ResponseArray findStatutes(StatuteKeyArray statuteKeyArray);

}
