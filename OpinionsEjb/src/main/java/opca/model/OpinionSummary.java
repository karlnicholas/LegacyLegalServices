package opca.model;

import java.util.Date;
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
@Table(indexes = {@Index(columnList="volume,vset,page")}) 
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
    public OpinionSummary(OpinionKey opinionSummaryKey, String volume, String vset, String page) {
    	super(new OpinionKey(volume, vset, page));
    	addReferringOpinion(opinionSummaryKey);
    	this.newlyLoadedOpinion = false;
    }
	// quick construct for comparisons
	public OpinionSummary(OpinionKey key) {
		super(key);
	}
	public void mergeCitedOpinion(OpinionSummary opinionSummary) {
		// debug
        if ( !opinionKey.equals(opinionSummary.getOpinionKey())) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionSummary.getOpinionKey());
        if ( opinionSummary.statuteCitations != null ) throw new RuntimeException("Can not add modifications: opinionBase.statuteCitations != null");
        if ( opinionSummary.opinionCitations != null ) throw new RuntimeException("Can not add modifications: opinionBase.opinionCitations != null");
        if ( opinionSummary.referringOpinions.size() != 1 ) throw new RuntimeException("Can not add modifications: " + opinionSummary.referringOpinions.size() + " != 1");
        //
//        mergeOpinions(opinionBase);
        // this is a newly cited opinion, so there will be only one referring opinion
    	if (referringOpinions == null ) {
    		setReferringOpinions(new TreeSet<OpinionKey>());
    	}
    	referringOpinions.addAll(opinionSummary.referringOpinions);
        // do it the paranoid way
        countReferringOpinions = referringOpinions.size();
    }
	public void mergePersistenceFromSlipLoad(OpinionSummary opinionSummary) {
		// debug
        if ( !opinionKey.equals(opinionSummary.getOpinionKey())) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionSummary.getOpinionKey());
        if ( opinionSummary.isNewlyLoadedOpinion() ) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionSummary.getOpinionKey());
        // 
        // opinionBase is either a published opinion (slip opinions) or a cited opinion 
        // at this point, it's coming from slipOpinions, so if it's a slipOpinion
        // this existingOpinion will NOT be a slip opinion .. since we don't merge slip opinions.
        // if the existing opinion is not a slip opinion, and opinionbase is not a slip opinion, then is 
        // is a case of merging cited opinions.
        if ( opinionSummary.getStatuteCitations() != null ) throw new RuntimeException("Can not add modifications: opinionBase.statuteCitations != null");
        if ( opinionSummary.getOpinionCitations() != null ) throw new RuntimeException("Can not add modifications: opinionBase.opinionCitations != null");
        //
    	if (referringOpinions == null ) {
    		setReferringOpinions(new TreeSet<OpinionKey>());
    	}
    	//TODO:Lazy initialization exception ... 
    	// these are newly created entities, so how did they get 'detached'?
    	referringOpinions.addAll(opinionSummary.getReferringOpinions());
        // do it the paranoid way
        countReferringOpinions = referringOpinions.size();
	}
	public void mergePublishedOpinion(OpinionSummary opinionSummary) {
		// debug
        if ( !opinionKey.equals(opinionSummary.getOpinionKey())) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionSummary.getOpinionKey());
        if ( opinionSummary.referringOpinions != null ) throw new RuntimeException("Can not add modifications: " + opinionKey + " != " + opinionSummary.getOpinionKey());
        //
        if ( title == null ) title = opinionSummary.title;
        if ( court == null ) court = opinionSummary.court;
        if ( opinionDate == null ) opinionDate = opinionSummary.opinionDate;
        
        copyNewOpinions(opinionSummary);
        // do statutes .. 
        if ( opinionSummary.getStatuteCitations() != null ) {
        	if ( statuteCitations == null ) 
        		statuteCitations = new TreeSet<StatuteKey>();
	        for ( StatuteKey addStatutekey: opinionSummary.getStatuteCitations() ) {
            	if ( addStatutekey.getTitle() == null ) 
            		continue;
	            if ( !statuteCitations.contains(addStatutekey) ) {
	            	statuteCitations.add(addStatutekey);
	            }
	        }
        }
        // do 
		if ( newlyLoadedOpinion || opinionSummary.newlyLoadedOpinion )
			newlyLoadedOpinion = true;
        // do referringOpinions
        // this is a "published" opinion, so there will be no referring opinions in opinionBase
	}

	private void copyNewOpinions(OpinionSummary opinionSummary) {
        // do opinions
        if ( opinionSummary.getOpinionCitations() != null ) {
        	if ( opinionCitations == null )
        		opinionCitations = new TreeSet<OpinionKey>(); 
	        for ( OpinionKey addOpinionKey: opinionSummary.getOpinionCitations()) {
	        	if ( !opinionCitations.contains(addOpinionKey) ) {
	        		opinionCitations.add(addOpinionKey);
	        	}
	        }
        }
	}
	public void mergeCourtRepublishedOpinion(OpinionSummary opinionSummary, ParsedOpinionCitationSet parserResults, CitationStore citationStore ) {
        if ( title == null ) title = opinionSummary.title;
        if ( court == null ) court = opinionSummary.court;
        if ( opinionDate == null ) opinionDate = opinionSummary.opinionDate;
		copyNewOpinions(opinionSummary);
        // do statutes .. 
        if ( opinionSummary.getStatuteCitations() != null ) {
        	if ( statuteCitations == null ) 
        		statuteCitations = new TreeSet<StatuteKey>();
	        for ( StatuteKey addStatutekey: opinionSummary.getStatuteCitations() ) {
            	if ( addStatutekey.getTitle() == null ) 
            		continue;
	            if ( !statuteCitations.contains(addStatutekey) ) {
	            	statuteCitations.add(addStatutekey);
	            } else {
	            	StatuteCitation newCitation = parserResults.findStatute(addStatutekey);
	            	StatuteCitation existingCitation = citationStore.statuteExists(addStatutekey);
	            	OpinionKey opKey = opinionSummary.getOpinionKey();
	            	if ( existingCitation.getRefCount(opKey) < newCitation.getRefCount(opKey) ) {
	            		existingCitation.setRefCount(opKey, newCitation.getRefCount(opKey));
	            	}
	            }
	        }
        }
	}
	public boolean isNewlyLoadedOpinion() {
		return newlyLoadedOpinion;
	}
	public void setNewlyLoadedOpinion(boolean newlyLoadedOpinion) {
		this.newlyLoadedOpinion = newlyLoadedOpinion;
	}
	@Override
	public String toString() {
        return String.format("%1$S : %2$tm/%2$td/%2$ty : %3$S", getOpinionKey().toString(), getOpinionDate(), getTitle() );
    }
}
