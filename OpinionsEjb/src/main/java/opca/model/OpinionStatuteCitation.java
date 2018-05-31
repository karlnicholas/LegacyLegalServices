package opca.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries( {
	@NamedQuery(name="OpinionStatuteCitation.findByOpinions", 
		query="select c from OpinionStatuteCitation c where c.opinionBase in :opinions order by statuteCitation, opinionBase"), 
	@NamedQuery(name="OpinionStatuteCitation.findByOpinion", 
		query="select c from OpinionStatuteCitation c where c.opinionBase = :opinion order by statuteCitation, opinionBase"), 
	@NamedQuery(name="OpinionStatuteCitation.deleteOpinionStatuteCitations", 
		query="delete from OpinionStatuteCitation c where c.opinionBase.id in :opinionIds"), 
})
@Entity
public class OpinionStatuteCitation implements Comparable<OpinionStatuteCitation> {
	@EmbeddedId
	private OpinionStatuteCitationId id;
    @ManyToOne @MapsId("opinionId")
    private OpinionBase opinionBase;
    @ManyToOne @MapsId("statuteId")
    private StatuteCitation statuteCitation;
    private int countReferences;
	public OpinionStatuteCitation() {
		this.id = new OpinionStatuteCitationId();
	}
	public OpinionStatuteCitation(StatuteCitation statuteCitation, OpinionBase opinionBase, int countReferences) {
		this.id = new OpinionStatuteCitationId();
		this.statuteCitation = statuteCitation;
		this.opinionBase = opinionBase;
		this.countReferences = countReferences;
	}
	public OpinionStatuteCitationId getId() {
		return id;
	}
	public void setId(OpinionStatuteCitationId opinionStatuteReferenceId) {
		this.id = opinionStatuteReferenceId;
	}
	public OpinionBase getOpinionBase() {
		return opinionBase;
	}
	public void setOpinionBase(OpinionBase opinionBase) {
		this.opinionBase = opinionBase;
	}
	public StatuteCitation getStatuteCitation() {
		return statuteCitation;
	}
	public void setStatuteCitation(StatuteCitation statuteCitation) {
		this.statuteCitation = statuteCitation;
	}
	public int getCountReferences() {
		return countReferences;
	}
	public void setCountReferences(int countReferences) {
		this.countReferences = countReferences;
	}
	@Override
	public int compareTo(OpinionStatuteCitation o) {
		int r = statuteCitation.compareTo(o.statuteCitation);
		if ( r != 0 ) {
			return r;
		} else {
			return opinionBase.compareTo(o.opinionBase);  
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((opinionBase == null) ? 0 : opinionBase.hashCode());
		result = prime * result + ((statuteCitation == null) ? 0 : statuteCitation.hashCode());
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
		OpinionStatuteCitation other = (OpinionStatuteCitation) obj;
		if (opinionBase == null) {
			if (other.opinionBase != null)
				return false;
		} else if (!opinionBase.equals(other.opinionBase))
			return false;
		if (statuteCitation == null) {
			if (other.statuteCitation != null)
				return false;
		} else if (!statuteCitation.equals(other.statuteCitation))
			return false;
		return true;
	}
	
	public String toString() {
		return opinionBase + ":" + statuteCitation;
	}
	
}
