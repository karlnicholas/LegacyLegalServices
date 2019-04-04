package opca.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class StatuteKey implements Serializable, Comparable<StatuteKey> {
    @Column(columnDefinition="char(32)")
    protected String sectionNumber;
    @Column(columnDefinition="char(4)")
    protected String lawCode;

    public StatuteKey() {}
    
    public StatuteKey(String lawCode, String sectionNumber) {
		this.sectionNumber = sectionNumber;
		this.lawCode = lawCode;
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
    public String getLawCode() {
        return lawCode;
    }
    public void setTitle(String value) {
        this.lawCode = value;
    }
    @Override
    public int compareTo(StatuteKey o) {
        if ( lawCode == null && o.lawCode != null ) return -1;
        if ( lawCode != null && o.lawCode == null ) return 1;
        if ( lawCode != null && o.lawCode != null ) {
            int r = lawCode.compareTo(o.lawCode); 
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
        result = prime * result + ((lawCode == null) ? 0 : lawCode.hashCode());
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
        if (lawCode == null) {
            if (other.lawCode != null)
                return false;
        } else if (!lawCode.equals(other.lawCode))
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
        return lawCode + ":" + sectionNumber;
    }
}
