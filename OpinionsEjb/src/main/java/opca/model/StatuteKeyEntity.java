package opca.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlElement;

import statutes.StatuteKey;

@SuppressWarnings("serial")
@Embeddable
public class StatuteKeyEntity extends StatuteKey implements Serializable, Comparable<StatuteKeyEntity> {
    private String code;
    private String sectionNumber;
    public StatuteKeyEntity() {}
    public StatuteKeyEntity(String code, String sectionNumber) {
        this.code = code;
        this.sectionNumber = sectionNumber;
    }
    public String getCode() {
        return code;
    }
    @XmlElement
    public void setCode(String code) {
        this.code = code;
    }
    public String getSectionNumber() {
        return sectionNumber;
    }
    @XmlElement
    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }
    @Override
    public int compareTo(StatuteKeyEntity o) {
        if ( code == null && o.code != null ) return -1;
        if ( code != null && o.code == null ) return 1;
        if ( code != null && o.code != null ) {
            int r = code.compareTo(o.code); 
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
        result = prime * result + ((code == null) ? 0 : code.hashCode());
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
        StatuteKeyEntity other = (StatuteKeyEntity) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
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
        return code + ":" + sectionNumber;
    }
}
