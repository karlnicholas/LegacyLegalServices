package opca.view;

import java.util.*;

import opca.model.OpinionBase;
import opca.model.StatuteCitation;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/27/12
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class SectionView implements ViewReference { 
	//	private static final Logger logger = Logger.getLogger(OpinionSection.class.getName());
	// This stuff holds the reference .. 
	// Which "code" it is and which section within that code is referenced
	// Also a place for the number of reference counts
	// as well as "designated," a working variable that shows how "strong" the reference is
    private String code;
    private SectionNumber sectionNumber;
    private int refCount;
    private boolean designated;
    
    // This holds the CodeRefence to the 
    private StatutesBaseClass section;

    public SectionView() {}
    public SectionView(OpinionBase opinionBase, StatuteCitation citation) {
    	// this is constructed without a parent and that's added later
    	// when we build the hierarchy
//    	logger.fine("code:" + code + ":section:" + section);
        this.code = citation.getStatuteKey().getCode();
        sectionNumber = new SectionNumber(-1, citation.getStatuteKey().getSectionNumber());
        refCount = citation.getRefCount(opinionBase.getOpinionKey());
        designated = citation.getDesignated();
    }

    public boolean equals(SectionView dcs ) {
        if ( code == null && dcs.getCode() != null ) return false;
        if ( code != null && dcs.getCode() == null ) return false;

        if ( code.equals( dcs.getCode() ) && sectionNumber.equals( dcs.getSectionNumber() ) ) return true;

        return false;
    }
    
    public int compareTo(SectionView dcs ) {
        if ( code == null && dcs.getCode() != null ) return -1;
        if ( code != null && dcs.getCode() == null ) return 1;

        if ( code != null && dcs.getCode() != null ) {
            int ret = code.compareTo( dcs.getCode() );
            if (  ret != 0 ) return ret;
        }

//        return sectionNumber.compareTo(dcs.getSectionNumber());
//        return section.getCodeRange().getsNumber().getSectionNumber().compareTo(dcs.getStatutesBaseClass().getCodeRange().getsNumber().getSectionNumber());
        // do a string compare for now .. really meant to compare codes 
        return sectionNumber.getSectionNumber().compareTo(dcs.getSectionNumber().getSectionNumber());
    }

    public SectionNumber getSectionNumber() {
       return sectionNumber;
    }
/*
    // do this for the presentation layer
    public String getDisplaySectionNumber() {
    	String retString;
        if ( sectionNumber != null ) {
        	retString = "§ " + sectionNumber.toString();
        } else  {
        	retString = "§§ " + ((StatutesLeaf)section).getStatuteRange().toString();
        }
        return retString;
    }
*/    
	public String getCode() {
        return code;
    }
    public void setCode( String code) {
    	this.code = code;
    }
    public int getRefCount() {
        return refCount;
    }

    public int incRefCount(int amount) {
        refCount = refCount + amount;
        return refCount;
    }

    public void setRefCount(int count) {
        refCount = count;
    }

    public void setDesignated( boolean designated ) {
        this.designated = designated;
    }
    public boolean getDesignated() {
        return designated;
    }
    
    public void setStatutesBaseClass( StatutesBaseClass section ) {
    	this.section = section;
    }

    @Override
    public StatutesBaseClass getStatutesBaseClass( ) {
    	return section;
    }
	
	public void addReference(ViewReference reference) {
		// do nothing
	}

	public ArrayList<ViewReference> getChildReferences() {
		// nothing to return
		return null;
	}
	
	public void trimToLevelOfInterest(int levelOfInterest) {
		// nothing to do 
	}

	
	public boolean iterateSections(IterateSectionsHandler handler) {
		return handler.handleOpinionSection(this);
	}
	
	public void incorporateOpinionReference(ViewReference opReference, QueueUtility queue) {
	//	if ( queue.isCompressStatutesBaseClasss() ) {
	        incRefCount(opReference.getRefCount());
	        // basically, by ignoring the subcode it gets left out
	        // we should so incorporate the increment count into the section .. 
	        sectionNumber = null;
	//	}
	}
	
    public void addToChildren( QueueUtility queueUtility ) {
		// do nothing
	}

	
	public SectionView getSectionView() {
		return this;
	}

    public String toString() {
    	if ( sectionNumber == null ) {
    		return code + ":" + ((StatutesLeaf)section).getStatuteRange().toString() + ":" + refCount;
    	}
        return code + ":" + sectionNumber + ":" + refCount;
    }

	@Override
    // nothing left to do here
	public List<SectionView> getSections() {
		return null;
	}

	@Override
	// nothing left to do here
	public List<ViewReference> getSubcodes() {
		return null;
	}
	public List<String> getShortTitles() {
    	ArrayList<StatutesBaseClass> baseStatutes = new ArrayList<StatutesBaseClass>(); 
		getStatutesBaseClass().getParents(baseStatutes);
		List<String> shortTitles = new ArrayList<String>();
		Collections.reverse(baseStatutes);
		for ( StatutesBaseClass baseStatute: baseStatutes ) {
			shortTitles.add(baseStatute.getShortTitle());
		}
    	return shortTitles;
	}
}

