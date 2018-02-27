package statutes;

import java.io.Serializable;

/**
 * The string representation of a statutory section.
 * Also holds the position of this section, an internally generated 
 * integer that represents the relative positions of statutory
 * sections to each other. This is because it is impossible
 * to determine relative positions of sections by their values.
 * 
 * @author Karl Nicholas
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "sectionNumber", "position"})
public class SectionNumberPosition implements Comparable<SectionNumberPosition>, Serializable {
	private static final long serialVersionUID = 1L;
	@JsonInclude
	private String sectionNumber;
	@JsonInclude
	private int position;

	public SectionNumberPosition() {}
	/**
	 * Constructor. Set position to -1 if constructing temporarily for searching purposes only.
	 * @param position integer position of the section.
	 * @param sectionNumber String value of the section.
	 */
	public SectionNumberPosition(int position, String sectionNumber) {
		this.position = position;
		this.sectionNumber = sectionNumber;
	}
	public String getSectionNumber() {
		return sectionNumber;
	}
	public void setSectionNumber(String sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	@Override
	public boolean equals(Object o) {
		if ( o instanceof SectionNumberPosition ) {
			return sectionNumber.equals(((SectionNumberPosition)o).sectionNumber);
		} else if ( o instanceof String ) {
			return sectionNumber.equals(((String)o));
		}
		else return false;
	}
	@Override
	public String toString() {
		return sectionNumber;
	}
	
	@Override
	public int compareTo(SectionNumberPosition arg0) {
		if ( position == -1 || arg0.position == -1 ) throw new RuntimeException("compareTo not enabled: " + position + ":" + arg0.position);
		return position - arg0.position;
	}
	public boolean isSectionNumber(String sectionNumberValue) {
		return sectionNumber.equals(sectionNumberValue);
	}
	
}
