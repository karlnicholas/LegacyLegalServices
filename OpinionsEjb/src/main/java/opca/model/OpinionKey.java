package opca.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

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
        "Cal.App.5th",		// 16 
        "Cal.App.Supp.5th", 
        "Cal.App.6th", 		// 18
        "Cal.App.Supp.6th", 
        "Cal.App.7th", 		// 20
        "Cal.App.Supp.7th", 	// 21
    };
    
    private int volume; 
    private int vset;
    private int page;

    public OpinionKey() {}
    public OpinionKey(String volume, String vset, String page) {
        buildKey(volume, vset, page);
    }
    public OpinionKey(int volume, int vset, int page) {
    	setKey(volume, vset, page);
	}
    private void setKey(int volume, int vset, int page) {
    	this.volume = volume;
    	this.vset= vset;
    	this.page = page;
    }
	public OpinionKey(String caseName) {
        String[] parts = caseName.split("[ ]");
        if ( parts.length != 3 ) throw new RuntimeException("Error parsing CaseCitationKey: " + caseName);
        buildKey(parts[0], parts[1], parts[2]);
    }
    private void buildKey(String volume, String vset, String page) {
    	setKey(Integer.parseInt(volume), findSetPosition(vset), Integer.parseInt(page));
    }
    private int findSetPosition(String set) {
        for ( int i=0; i<appellateSets.length; ++i ) {
            if ( appellateSets[i].equalsIgnoreCase(set)) return i;
        }
        throw new RuntimeException("Error parsing CaseCitationKey: No set found: " + set);
    }
/*    
    public int getVolume() {
        return (int)(opinionKey >>> 48);
    }
    public int getVset() {
        return (int)((opinionKey >>> 32) & 0xffff);
    }
    public int getPage() {
        return (int)( opinionKey & 0xffffffff);
    }
*/    

    @Override
    public int compareTo(OpinionKey o) {
    	if ( page != o.page ) return page - o.page;
    	if ( volume != o.volume ) return volume - o.volume;
    	return vset - o.vset;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + page;
		result = prime * result + volume;
		result = prime * result + vset;
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
        StringBuilder sb = new StringBuilder();
        sb.append(volume);
        sb.append(' ');
        sb.append(getVSetAsString());
        sb.append(' ');
        sb.append(page);
        return sb.toString();
    }
    public static String printKey(OpinionKey opinionKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(opinionKey.volume);
        sb.append(' ');
        sb.append(appellateSets[opinionKey.vset]);
        sb.append(' ');
        sb.append(opinionKey.page);
        return sb.toString();
    }
	public boolean isSlipOpinion() {
		return vset == 0; 
	}
	public String getVSetAsString() {
		return appellateSets[vset];
	}
}

