package gsearch.viewmodel;

import java.util.*;

import statutes.StatutesBaseClass;

public class StatuteEntry extends EntryBase {
	
	private List<EntryReference> entries;
	
//	public StatuteEntry() {super(); init(); }
	public StatuteEntry( StatutesBaseClass reference) {
		super( reference, reference.getShortTitle() );
		init();
	}
	
	private void init() { entries = new ArrayList<EntryReference>(); }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return null;}
	@Override
	public boolean isSectionText() { return false; }
}