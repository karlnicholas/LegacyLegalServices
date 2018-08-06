package opca.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/27/12
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */

@NamedQueries({
	@NamedQuery(name="StatuteCitation.findStatutesForKeys", 
		query="select s from StatuteCitation s where s.statuteKey in :keys"),
	@NamedQuery(name="StatuteCitation.selectForTitle", 
		query="select s from StatuteCitation s where s.statuteKey.title like :title"),
	@NamedQuery(name="StatuteCitation.findByStatuteKeyJoinReferringOpinions", 
		query="select distinct(s) from StatuteCitation s left join fetch s.referringOpinions ro left join fetch ro.opinionBase where s.statuteKey = :statuteKey"),
	@NamedQuery(name="StatuteCitation.statutesWithReferringOpinions", 
		query="select distinct(s) from StatuteCitation s left join fetch s.referringOpinions ro left join fetch ro.opinionBase where s.statuteKey in :statuteKeys"),
})
@SuppressWarnings("serial")
@Entity
@Table(indexes = {@Index(columnList="title,sectionNumber")})
public class StatuteCitation implements Comparable<StatuteCitation>, Serializable { 
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
    private StatuteKey statuteKey;
	@OneToMany(mappedBy="statuteCitation")
	private Set<OpinionStatuteCitation> referringOpinions;
    
    private boolean designated;
    
    public StatuteCitation() {
        referringOpinions = new TreeSet<OpinionStatuteCitation>();
    }
    
    public StatuteCitation(OpinionBase opinionBase, String title, String sectionNumber) {
    	// this is constructed without a parent and that's added later
    	// when we build the hierarchy
//    	logger.fine("title:" + title + ":section:" + section);
        statuteKey = new StatuteKey(title, sectionNumber);
        referringOpinions = new TreeSet<OpinionStatuteCitation>();
        referringOpinions.add(new OpinionStatuteCitation(this, opinionBase, 1));
        if ( title == null ) {
            designated = false;
        } else {
            designated = true;
        }
    }
    // dirty constructor for searching only
    public StatuteCitation(StatuteKey key) {
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
    	Iterator<OpinionStatuteCitation> refOpIt = statute.referringOpinions.iterator();
    	while (refOpIt.hasNext()) {
    		OpinionStatuteCitation opinionStatuteReference = refOpIt.next();
    		// test for error condition
    		if ( referringOpinions.contains(opinionStatuteReference) ) {
				throw new RuntimeException("Cannot merge: key exists " + opinionStatuteReference);
    		}
    		// replace existing statuteCitation with the this one b/c it was loaded from the database
    		// and is being used to replace the statute one found in a new SlipOpinion
    		opinionStatuteReference.setStatuteCitation(this);
    		referringOpinions.add(opinionStatuteReference);
    	}
	}
	public void addOpinionCitation(OpinionStatuteCitation opinionStatuteCitation) {
	}
	public StatuteKey getStatuteKey() {
        return statuteKey;
    }
    public void setStatuteKey(StatuteKey statuteKey) {
        this.statuteKey = statuteKey;
    }
    public Set<OpinionStatuteCitation> getReferringOpinions() {
        return referringOpinions;
    }
    public void setReferringOpinions(Set<OpinionStatuteCitation> referringOpinions) {
        this.referringOpinions = referringOpinions;
    }
    public void setRefCount(OpinionBase opinionBase, int count) {    
    	OpinionStatuteCitation opinionStatuteCitation = getOpinionStatuteReference(opinionBase);
    	if ( opinionStatuteCitation != null ) {
    		opinionStatuteCitation.setCountReferences(count);
    	} else {
    		referringOpinions.add(opinionStatuteCitation);
    	}

    }
    public OpinionStatuteCitation getOpinionStatuteReference(OpinionBase opinionBase) {
    	Iterator<OpinionStatuteCitation> refOpIt = referringOpinions.iterator();
    	while (refOpIt.hasNext()) {
    		OpinionStatuteCitation opinionStatuteReference = refOpIt.next();
    		if ( opinionStatuteReference.getOpinionBase().equals(opinionBase) ) {
    			return opinionStatuteReference;
    		}
    	}
    	return null;
    }
    public void incRefCount(OpinionBase opinionBase, int amount) {
    	OpinionStatuteCitation opinionStatuteCitation = getOpinionStatuteReference(opinionBase);
        if ( opinionStatuteCitation == null ) {
        	referringOpinions.add(new OpinionStatuteCitation( this, opinionBase, amount));
        } else {
        	opinionStatuteCitation.setCountReferences(opinionStatuteCitation.getCountReferences() + amount);
        }
    }
	public OpinionStatuteCitation removeOpinionStatuteReference(SlipOpinion deleteOpinion) {
    	Iterator<OpinionStatuteCitation> refOpIt = referringOpinions.iterator();
    	while (refOpIt.hasNext()) {
    		OpinionStatuteCitation opinionStatuteReference = refOpIt.next();
    		if ( opinionStatuteReference.getOpinionBase().equals(deleteOpinion) ) {
    			refOpIt.remove();
    			return opinionStatuteReference;
    		}
    	}
    	return null;
	}

    public boolean getDesignated() {
        return designated;
    }    
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
