package opca.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class StatuteKey implements Serializable, Comparable<StatuteKey> {
    @Column(columnDefinition="char(32)")
    protected String sectionNumber;
    @Column(columnDefinition="char(3)")
    protected String title;

    public StatuteKey() {}
    
    public StatuteKey(String title, String sectionNumber) {
		this.sectionNumber = sectionNumber;
		this.title = title;
	}

	/**
     * Gets the value of the sectionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSectionNumber() {
        return sectionNumber;
    }

    /**
     * Sets the value of the sectionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSectionNumber(String value) {
        this.sectionNumber = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }
    public void setTitle(String value) {
        this.title = value;
    }
    @Override
    public int compareTo(StatuteKey o) {
        if ( title == null && o.title != null ) return -1;
        if ( title != null && o.title == null ) return 1;
        if ( title != null && o.title != null ) {
            int r = title.compareTo(o.title); 
            if (  r != 0 ) return r; 
        }  
        if ( sectionNumber == null && o.sectionNumber != null ) return -1;
        if ( sectionNumber != null && o.sectionNumber == null ) return 1;
        if ( sectionNumber != null && o.sectionNumber != null ) {
            int r = sectionNumber.compareTo(o.sectionNumber); 
            if (  r != 0 ) return r; 
        }  
        return sectionNumber.compareTo(o.sectionNumber);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((sectionNumber == null) ? 0 : sectionNumber.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StatuteKey other = (StatuteKey) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (sectionNumber == null) {
            if (other.sectionNumber != null)
                return false;
        } else if (!sectionNumber.equals(other.sectionNumber))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return title + ":" + sectionNumber;
    }
}
