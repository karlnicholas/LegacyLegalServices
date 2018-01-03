package api.gsearch.viewmodel;

import java.util.*;

import statutes.StatutesBaseClass;

public interface EntryReference {
	String getFullFacet();
	StatutesBaseClass getStatutesBaseClass();
	void setStatutesBaseClass(StatutesBaseClass cloneBaseClass );
	List<EntryReference> getEntries();
	String getText();
	int getCount();
	void setCount(int count);
	boolean isPathPart();
	void setPathPart(boolean pathPart);
	boolean isSectionText();
}
