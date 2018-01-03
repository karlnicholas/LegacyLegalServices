package opca.dto;

import statutes.SectionNumber;

public class SectionView { 
    private final String code;
    private final SectionNumber sectionNumber;
    private final int refCount;
    // This holds the CodeRefence to the 
    private final Section section;
    
    public SectionView(opca.view.SectionView section) {
    	this.code = section.getCode();
    	this.sectionNumber = section.getSectionNumber();
    	this.refCount = section.getRefCount();
    	this.section = new Section(section.getStatutesBaseClass().getStatutesLeaf());
    }
	public String getCode() {
		return code;
	}
	public SectionNumber getSectionNumber() {
		return sectionNumber;
	}
	public int getRefCount() {
		return refCount;
	}
	public Section getSection() {
		return section;
	}

}

