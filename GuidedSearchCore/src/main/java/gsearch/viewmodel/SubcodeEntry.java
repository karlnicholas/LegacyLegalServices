package gsearch.viewmodel;

import java.util.*;

import statutes.StatutesBaseClass;

public class SubcodeEntry extends EntryBase {

	private List<EntryReference> entries;
	
//	public SubcodeEntry() {super(); init(); }
	public SubcodeEntry( StatutesBaseClass statutesBaseClass ) {
		super( 
			statutesBaseClass, 
			statutesBaseClass.getPart()==null?"": (statutesBaseClass.getPart() + " " + statutesBaseClass.getPartNumber())
		); 
		init(); 
	}

	private void init() { entries = new ArrayList<EntryReference>(); }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return getDisplayTitle(); }
	@Override
	public boolean isSectionText() { return false; }
}