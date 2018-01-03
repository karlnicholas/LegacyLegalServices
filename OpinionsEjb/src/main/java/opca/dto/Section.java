package opca.dto;

import statutes.StatuteRange;

public class Section {
    private final String part;
    private final String partNumber;
    private final String title;
//    private SectionRange sectionRange;
    private final StatuteRange statuteRange;
    // keep track of how deep we are ..
    // always > 0 for sections
    private final int depth;

	public Section(statutes.StatutesLeaf statutesLeaf) {
		this.part = statutesLeaf.getPart();
		this.partNumber = statutesLeaf.getPartNumber();
		this.title = statutesLeaf.getTitle();
		this.statuteRange = statutesLeaf.getStatuteRange();
		this.depth = statutesLeaf.getDepth();
	}

	public String getPart() {
		return part;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public String getTitle() {
		return title;
	}

	public StatuteRange getStatuteRange() {
		return statuteRange;
	}

	public int getDepth() {
		return depth;
	}

}
