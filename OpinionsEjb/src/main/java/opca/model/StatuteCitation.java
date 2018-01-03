package opca.model;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/27/12
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */

@NamedQueries({
	@NamedQuery(name="StatuteCitation.findByCodeSection", 
		query="select s from StatuteCitation s where s.statuteKey.code = :code and s.statuteKey.sectionNumber = :sectionNumber"),
	@NamedQuery(name="StatuteCitation.findStatutesForKeys", 
		query="select s from StatuteCitation s where s.statuteKey in :keys"),
	@NamedQuery(name="StatuteCitation.selectForCode", 
		query="select s from StatuteCitation s where s.statuteKey.code like :code"),
	@NamedQuery(name="StatuteCitationData.findStatutesForKeys", 
		query="select distinct(s) from StatuteCitation s join fetch s.referringOpinionCount where s.statuteKey in :keys"),
	@NamedQuery(name="StatuteCitationData.findStatutesForKeysWithChildren", 
		query="select distinct(s) from StatuteCitation s join fetch s.referringOpinionCount where s.statuteKey in :keys"),
		
})
@SuppressWarnings("serial")
@Entity
@Table(indexes = {@Index(columnList="code,sectionNumber")}) 
public class StatuteCitation implements Comparable<StatuteCitation>, Serializable { 
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Embedded
    private StatuteKeyEntity statuteKey;
    @ElementCollection
	private Map<OpinionKey, Integer> referringOpinionCount;
    
    private boolean designated;
    
    public StatuteCitation() {
        referringOpinionCount = new TreeMap<OpinionKey, Integer>();
    }
    
    public StatuteCitation(OpinionKey opinionKey, String code, String sectionNumber) {
    	// this is constructed without a parent and that's added later
    	// when we build the hierarchy
//    	logger.fine("code:" + code + ":section:" + section);
        statuteKey = new StatuteKeyEntity(code, sectionNumber);
        referringOpinionCount = new TreeMap<OpinionKey, Integer>();
        referringOpinionCount.put(opinionKey, new Integer(1));
        if ( code == null ) {
            designated = false;
        } else {
            designated = true;
        }
    }
    // dirty constructor for searching only
    public StatuteCitation(StatuteKeyEntity key) {
		this.statuteKey = key;
	}
    /**
     * The passed statute is created from loaded SlipOpinions, 
     * so the only referringOpinions will be slip opinions
     * therefore, there will be nothing to sum, just add
     * the referring opinions in ..
     * @param statute
     */
	public void mergeStatuteCitationFromSlipLoad(StatuteCitation statute) {
		for ( OpinionKey key: statute.getReferringOpinionCount().keySet() ) {
			if ( !referringOpinionCount.containsKey(key) ) {
				referringOpinionCount.put(key, statute.getReferringOpinionCount().get(key));
			} else {
				throw new RuntimeException("Cannot merge: key exists " + key);
			}
		}
	}
	public Long getId() {
    	return id;
    }
	public StatuteKeyEntity getStatuteKey() {
        return statuteKey;
    }
    @XmlElement
    public void setStatuteKey(StatuteKeyEntity statuteKey) {
        this.statuteKey = statuteKey;
    }
    public Map<OpinionKey, Integer> getReferringOpinionCount() {
        return referringOpinionCount;
    }
    @XmlElement
    public void setReferringOpinionCount(Map<OpinionKey, Integer> mapReferringOpinionCount) {
        this.referringOpinionCount = mapReferringOpinionCount;
    }
    public void setRefCount(OpinionKey opinionCitationKey, int count) {
        referringOpinionCount.put(opinionCitationKey, count);
    }
    public int getRefCount(OpinionKey opinionCitationKey) {
        Integer cInt = referringOpinionCount.get(opinionCitationKey);
        if ( cInt == null ) return 0;
        return cInt.intValue();
    }
    public void incRefCount(OpinionKey opinionCitationKey, int amount) {
        Integer cInt = referringOpinionCount.get(opinionCitationKey);
        if ( cInt == null ) cInt = new Integer(0);
        referringOpinionCount.put(opinionCitationKey, cInt + amount);
    }
    public boolean getDesignated() {
        return designated;
    }    
    @XmlElement
    public void setDesignated( boolean designated ) {
        this.designated = designated;
    }
    @Override
	public int compareTo(StatuteCitation o) {
        return statuteKey.compareTo(o.statuteKey);
	}
	@Override
	public int hashCode() {
	    return statuteKey.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatuteCitation other = (StatuteCitation) obj;
		return statuteKey.equals(other.statuteKey);
	}
    @Override
    public String toString() {
        return statuteKey.toString();
    }

}
