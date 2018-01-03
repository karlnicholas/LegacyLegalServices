package opca.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("serial")
@Embeddable
public class OpinionKey implements Serializable, Comparable<OpinionKey> { 
    public static final String[] appellateSets = {
    	"Slip.Op",		// 0
        "Cal.", 
        "Cal.2d", 
        "Cal.3d", 
        "Cal.4th", 
        "Cal.5th", 
        "Cal.6th", 
        "Cal.7th", 
        "Cal.App", 		// 8
        "Cal.App.Supp", 
        "Cal.App.2d", 		// 10
        "Cal.App.Supp.2d", 
        "Cal.App.3d", 		// 12
        "Cal.App.Supp.3d", 
        "Cal.App.4th", 		// 14
        "Cal.App.Supp.4th", 
        "Cal.App.5th", 
        "Cal.App.Supp.5th", 
        "Cal.App.6th", 
        "Cal.App.Supp.6th", 
        "Cal.App.7th", 
        "Cal.App.Supp.7th", 
    };
    private int volume; 
    private int vset;
    private long page;
    public OpinionKey() {}
    public OpinionKey(String volume, String vset, String page) {
        buildKey(volume, vset, page);
    }
    public OpinionKey(int volume, int vset, long page) {
		this.volume = volume;
		this.vset = vset;
		this.page = page;
	}
	public OpinionKey(String caseName) {
        String[] parts = caseName.split("[ ]");
        if ( parts.length != 3 ) throw new RuntimeException("Error parsing CaseCitationKey: " + caseName);
        buildKey(parts[0], parts[1], parts[2]);
    }
    private void buildKey(String volume, String vset, String page) {
        this.volume = Integer.parseInt(volume);
        this.vset = findSetPosition(vset);
        this.page = Long.parseLong(page);
    }
    private int findSetPosition(String set) {
        for ( int i=0; i<appellateSets.length; ++i ) {
            if ( appellateSets[i].equalsIgnoreCase(set)) return i;
        }
        throw new RuntimeException("Error parsing CaseCitationKey: No set found: " + set);
    }
    public int getVolume() {
        return volume;
    }
    @XmlElement
    public void setVolume(int volume) {
        this.volume = volume;
    }
    public int getVset() {
        return vset;
    }
    @XmlElement
    public void setVset(int vset) {
        this.vset = vset;
    }
    public long getPage() {
        return page;
    }
    @XmlElement
    public void setPage(long page) {
        this.page = page;
    }
    @Override
    public int compareTo(OpinionKey o) {
        if ( vset != o.vset) return vset - o.vset;
        else if ( volume != o.volume) return volume - o.volume;
        else 
        	return page>o.page?1:(page<o.page?-1:0);
    }
    @Override
    public int hashCode() {
    	return Objects.hash(page, volume, vset);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OpinionKey other = (OpinionKey) obj;
        if (page != other.page)
            return false;
        if (volume != other.volume)
            return false;
        if (vset != other.vset)
            return false;
        return true;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(volume);
        sb.append(' ');
        sb.append(appellateSets[vset]);
        sb.append(' ');
        sb.append(page);
        return sb.toString();
    }
	public boolean isSlipOpinion() {
		return vset == 0; 
	}
	public String getVSetAsString() {
		return appellateSets[vset];
	}
}

