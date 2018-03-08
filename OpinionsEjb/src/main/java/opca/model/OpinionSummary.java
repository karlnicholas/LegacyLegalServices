package opca.model;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import opca.memorydb.CitationStore;
import opca.parser.ParsedOpinionCitationSet;

@NamedQueries({
	@NamedQuery(name="OpinionSummary.findByOpinionKey", 
		query="select o from OpinionSummary o where o.opinionKey = :key"),
	@NamedQuery(name="OpinionSummary.findOpinionsForKeys", 
		query="select o from OpinionSummary o where o.opinionKey in :keys"),
	@NamedQuery(name="OpinionSummary.findOpinionsForKeysJoinStatuteCitations", 
		query="select distinct(o) from OpinionSummary o inner join fetch o.statuteCitations where o.opinionKey in :keys"),
})
@SuppressWarnings("serial")
@Entity
public class OpinionSummary extends OpinionBase {
//	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
	@Transient
	private boolean newlyLoadedOpinion;

    public OpinionSummary() {
    	super();
    }
	public OpinionSummary(OpinionSummary opinionSummary) {
		super(opinionSummary);
    }
    public OpinionSummary(OpinionKey key, String title, Date opinionDate, String court) {
		super(key, title, opinionDate, court);
    	this.newlyLoadedOpinion = true;
	}
	// making a new OpinionSummary from only a citation.
	public OpinionSummary(int volume, int vset, long page) {
		super(new OpinionKey(volume, vset, page), null, null, null);
	}
	// making a new OpinionSummary from only a citation.
    public OpinionSummary(OpinionBase opinionBase, String volume, String vset, String page) {
    	super(new OpinionKey(volume, vset, page));
    	addReferringOpinion(opinionBase);
    	this.newlyLoadedOpinion = false;
    }
	// quick construct for comparisons
	public OpinionSummary(OpinionKey key) {
		super(key);
	}
}
