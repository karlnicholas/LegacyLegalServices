package api.gsearch.viewmodel.strings;

import java.util.*;

import statutes.StatutesBaseClass;

public class CodeEntry extends EntryBase implements EntryReference {
	
	private List<EntryReference> entries;
	
	public CodeEntry() {super(); init(); }
	public CodeEntry( StatutesBaseClass baseClass, String facetHead ) {
		super( baseClass.getShortTitle(), baseClass.getTitle(), baseClass, facetHead );
		init();
	}
	
	private void init() { entries = new ArrayList<EntryReference>(); }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return null;}	
}

