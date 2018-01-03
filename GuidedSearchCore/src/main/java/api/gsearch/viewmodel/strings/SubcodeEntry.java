package api.gsearch.viewmodel.strings;

import java.util.*;

import statutes.StatutesBaseClass;

public class SubcodeEntry extends EntryBase implements EntryReference {

	private List<EntryReference> entries;
	
	public SubcodeEntry() {super(); init(); }
	public SubcodeEntry( StatutesBaseClass baseClass, String facetHead) {		
		super(baseClass.getPart() + " " + baseClass.getPartNumber(), baseClass.getTitle(), baseClass, facetHead ); 
		init(); 
	}

	private void init() { entries = new ArrayList<EntryReference>(); }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return null;}
}

