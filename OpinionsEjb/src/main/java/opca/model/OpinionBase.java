package opca.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

@SuppressWarnings("serial")
@MappedSuperclass
public class OpinionBase implements Comparable<OpinionBase>, Serializable {
	@EmbeddedId
	protected OpinionKey opinionKey;
	@Column(columnDefinition="TEXT")
	protected String title;
    @Temporal(TemporalType.DATE)
    protected Date opinionDate;
	@Column(columnDefinition="TEXT")
	protected String court;
    @ElementCollection
    protected Set<StatuteKeyEntity> statuteCitations;
    @ElementCollection
    protected Set<OpinionKey> opinionCitations;
    @ElementCollection
    protected Set<OpinionKey> referringOpinions;
    // performance optimization equal to size of referringOpinions 
    protected int countReferringOpinions;

    public OpinionBase() {}
	public OpinionBase(OpinionBase opinionBase) {
		this.opinionKey = opinionBase.opinionKey;
    	this.title = opinionBase.title;
    	this.court = opinionBase.court;
    	this.opinionDate = opinionBase.opinionDate;
    	this.statuteCitations = opinionBase.statuteCitations;
    	this.opinionCitations = opinionBase.opinionCitations;
    	this.referringOpinions = opinionBase.referringOpinions;
    	this.countReferringOpinions = opinionBase.countReferringOpinions;
    }
	public OpinionBase(OpinionKey opinionKey, String title, Date opinionDate, String court) {
		this.opinionKey = opinionKey;
        this.title = title;
    	this.opinionDate = opinionDate;
        if ( court == null ) this.court = new String();
        else this.court = court;
    }
	/**
	 * Only meant for comparison purposes.
	 * @param opinionKey
	 */
    public OpinionBase(OpinionKey opinionKey) {
        this.opinionKey = opinionKey;
    }
	/**
	 * adds a new referringOpinion key if it doesn't already exist.
	 * @param opinionKey
	 */
    public void addReferringOpinion(OpinionKey opinionKey) {
    	if (referringOpinions == null ) {
    		setReferringOpinions(new TreeSet<OpinionKey>());
    	}
    	referringOpinions.add(opinionKey);
        // do it the paranoid way
        countReferringOpinions = referringOpinions.size();
    }
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getOpinionDate() {
		return opinionDate;
	}
	public void setOpinionDate(Date opinionDate) {
		this.opinionDate = opinionDate;
	}
	public String getCourt() {
		return court;
	}
	public void setCourt(String court) {
		this.court = court;
	}
	public Set<StatuteKeyEntity> getStatuteCitations() {
		return statuteCitations;
	}
	public void setStatuteCitations(Set<StatuteKeyEntity> statuteCitations) {
		this.statuteCitations = statuteCitations;
	}
	public Set<OpinionKey> getOpinionCitations() {
		return opinionCitations;
	}
	public void setOpinionCitations(Set<OpinionKey> opinionCitations) {
		this.opinionCitations = opinionCitations;
	}
	public Set<OpinionKey> getReferringOpinions() {
        return referringOpinions;
    }
    public void setReferringOpinions(Set<OpinionKey> referringOpinions) {
        this.referringOpinions = referringOpinions;
        countReferringOpinions = referringOpinions.size();
    }
    public int getCountReferringOpinions() {
    	return countReferringOpinions;
    }
	public void checkCountReferringOpinions() {
		countReferringOpinions = referringOpinions.size();
	}
	public void setCountReferringOpinions(int countReferringOpinions) {
		this.countReferringOpinions = countReferringOpinions;
	}
	public OpinionKey getOpinionKey() {
		return opinionKey;
	}
	public void setOpinionKey(OpinionKey opinionKey) {
		this.opinionKey = opinionKey ;
	}
	@Override
	public int hashCode() {
		return opinionKey.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		if ( !(obj instanceof OpinionBase) ) 
			return false;
		OpinionBase other = (OpinionBase) obj;
		return opinionKey.equals(other.opinionKey);
	}
	@Override
	public int compareTo(OpinionBase o) {
		return opinionKey.compareTo(o.opinionKey);
	}
}
