package opca.view;

import java.util.List;

import statutes.SectionNumber;
import statutes.StatutesBaseClass;

public interface ViewReference {

	public void trimToLevelOfInterest( int levelOfInterest );

	public void addReference(ViewReference opReference);
    public void addToChildren( QueueUtility queueUtility );
	public void incorporateOpinionReference(ViewReference opReference, QueueUtility queueUtility);	
	public int incRefCount(int amount);
	
	public SectionNumber getSectionNumber();
	public StatutesBaseClass getStatutesBaseClass();
	public List<ViewReference> getChildReferences();
	public SectionView getSectionView();
	public List<SectionView> getSections();
	public List<ViewReference> getSubcodes();
	public int getRefCount();

    // return true to keep iterating, false to stop iteration
	public boolean iterateSections( IterateSectionsHandler handler);
	
}
