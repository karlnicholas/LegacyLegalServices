package opca.parser;

import java.util.TreeSet;

import opca.memorydb.PersistenceLookup;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;

public class ParsedOpinionCitationSet {
    private TreeSet<OpinionBase> opinionTable = new TreeSet<OpinionBase>();
    private TreeSet<StatuteCitation> statuteTable = new TreeSet<StatuteCitation>();
    
    public ParsedOpinionCitationSet() {}


    public ParsedOpinionCitationSet(OpinionBase opinionBase, PersistenceLookup persistence) {
//        if ( opinionBase.getOpinionSummaryKey() != null ) putOpinionSummary(opinionBase);
    	
//        for ( StatuteKey statuteKey: opinionBase.getStatuteCitationKeys()) {
//        	putStatuteCitation(persistence.statuteExists(statuteKey));
//        }
//        for ( OpinionKey opinionKey: opinionBase.getOpinionCitationKeys()) {
//        	putOpinionSummary(persistence.opinionExists(opinionKey));
//        }
///        
    	// Optimize this, use a named query to get all keys at once???
    	// This above comment makes no sense, there will never be any physical DB using
    	// this class?
    	// why are we copying from persistence into here. Where is that needed? 
        statuteTable.addAll( persistence.getStatutes(opinionBase.getOnlyStatuteCitations()));
        opinionTable.addAll( persistence.getOpinions(opinionBase.getOpinionCitations()));
    }

    /*

    public void mergeParsedDocumentCitationsToMemoryDB(OpinionBase opinionBase, CitationStore persistence) {
    	Iterator<OpinionSummary> opinionIterator = opinionIterator();
    	while ( opinionIterator.hasNext() ) {
    		OpinionSummary newOpinion = opinionIterator.next();
        	newOpinion.addReferringOpinion(opinionBase.getOpinionKey());
    		OpinionSummary existingOpinion = persistence.opinionExists(newOpinion.getOpinionKey());
            if (  existingOpinion != null ) {
                existingOpinion.addModifications(newOpinion, persistence);
                persistence.mergeOpinion(existingOpinion);
            } else {
            	persistence.persistOpinion(newOpinion);
            }
    	}
    
    	Iterator<StatuteCitation> statuteIterator = statuteIterator();
    	while ( statuteIterator.hasNext() ) {
    		StatuteCitation newStatute = statuteIterator.next();
    		StatuteCitation existingStatute = persistence.statuteExists(newStatute.getStatuteKey());
    		if ( existingStatute != null) {
    			existingStatute.setRefCount(opinionBase.getOpinionKey(), newStatute.getRefCount(opinionBase.getOpinionKey()));
    			persistence.mergeStatute(existingStatute);
    		} else {
        		newStatute.setRefCount(opinionBase.getOpinionKey(), 1);
    			persistence.persistStatute(newStatute);
    		}
    	}
    }


 */
    public StatuteCitation findStatute(StatuteKey key) {
		return findStatute(new StatuteCitation(key));
	}

	public void putStatuteCitation(StatuteCitation statuteCitation) {
		statuteTable.add(statuteCitation);
	}

	public StatuteCitation findStatute(StatuteCitation statuteCitation) {
        StatuteCitation foundCitation = statuteTable.floor(statuteCitation);
        if ( statuteCitation.equals(foundCitation)) return foundCitation;
        return null;
	}
    
	public OpinionBase findOpinion(OpinionKey key) {
		OpinionBase tempOpinion = new OpinionBase(key);
        if ( opinionTable.contains(tempOpinion))
        	return opinionTable.floor(tempOpinion);
        else return null;
	}

	public void putOpinionBase(OpinionBase opinionBase) {
		opinionTable.add(opinionBase);
	}

	public TreeSet<OpinionBase> getOpinionTable() {
		return opinionTable;
	}

	public TreeSet<StatuteCitation> getStatuteTable() {
		return statuteTable;
	}

}
