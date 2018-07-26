package opca.view;

import java.util.List;

public abstract class ViewReference {

	public abstract void trimToLevelOfInterest( int levelOfInterest );

	public abstract void addReference(ViewReference opReference);
	public abstract int incRefCount(int amount);
	
	public abstract List<ViewReference> getChildReferences();
//	public abstract SectionView getSectionView();
	public abstract List<SectionView> getSections();
	public abstract List<ViewReference> getSubcodes();
	public abstract int getRefCount();
	public abstract ViewReference getParent();

    // return true to keep iterating, false to stop iteration
	public abstract boolean iterateSections( IterateSectionsHandler handler);

	public abstract String getTitle();
	
}
