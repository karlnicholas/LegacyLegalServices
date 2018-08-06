package opca.memorydb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import opca.model.OpinionBase;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;
import opca.parser.ParsedOpinionCitationSet;

public class CitationStore {
    
    private TreeSet<OpinionBase> opinionTable;
    private TreeSet<StatuteCitation> statuteTable;
    
    private TreeSet<OpinionBase> getOpinionTable() {
        return opinionTable;
    }

    private TreeSet<StatuteCitation> getStatuteTable() {
        return statuteTable;
    }

    public void setStatuteTable(TreeSet<StatuteCitation> statuteTable) {
        this.statuteTable = statuteTable;
    }

    private CitationStore(){
        opinionTable = new TreeSet<OpinionBase>();
        statuteTable = new TreeSet<StatuteCitation>();
    }
    private static class SingletonHelper {
        private static final CitationStore INSTANCE = new CitationStore();
    }
    public static CitationStore getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
	public void clearDB() {
		getStatuteTable().clear();
		getOpinionTable().clear();
	}
    public int getCount() {
        return getStatuteTable().size();
    }

    public StatuteCitation findStatuteByCodeSection(String title, String sectionNumber) {
        return statuteExists(new StatuteCitation(new StatuteKey(title, sectionNumber)));
    }

	public StatuteCitation statuteExists(StatuteCitation statuteCitation) {
		return findStatuteByStatute(statuteCitation);
	}

	public StatuteCitation findStatuteByStatute(StatuteCitation statuteCitation) {
        StatuteCitation foundCitation = getStatuteTable().floor(statuteCitation);
        if ( statuteCitation.equals(foundCitation)) return foundCitation;
        return null;
	}    

	public OpinionBase findOpinionByOpinion(OpinionBase opinionBase) {
		OpinionBase foundOpinion = getOpinionTable().floor(opinionBase);
        if ( opinionBase.equals(foundOpinion)) return foundOpinion;
		return null;
	}


	public void persistStatute(StatuteCitation statuteCitation) {
		getStatuteTable().add(statuteCitation);
	}

	public void replaceStatute(StatuteCitation statuteCitation) {
		getStatuteTable().remove(statuteCitation);
		getStatuteTable().add(statuteCitation);
	}

	public void replaceOpinion(OpinionBase existingOpinion) {
		getOpinionTable().remove(existingOpinion);
		getOpinionTable().add(existingOpinion);
	}

	public OpinionBase opinionExists(OpinionBase opinionBase) {
//        OpinionSummary tempOpinion = new OpinionSummary(opinionBase);
        if ( getOpinionTable().contains(opinionBase))
        	return getOpinionTable().floor(opinionBase);
        else return null;
	}

	public void persistOpinion(OpinionBase opinionBase) {
		getOpinionTable().add(opinionBase);
	}

	public List<StatuteCitation> getStatutes(Collection<StatuteCitation> statuteCitations) {
		List<StatuteCitation> list = new ArrayList<StatuteCitation>();
		for (StatuteCitation statuteCitation: statuteCitations ) {
			StatuteCitation statute = statuteExists(statuteCitation);
			if ( statute != null ) list.add(statute);
		}
		return list;
	}

	public List<OpinionBase> getOpinions(Collection<OpinionBase> opinions) {
		List<OpinionBase> list = new ArrayList<OpinionBase>();
		for (OpinionBase opinion: opinions ) {
			OpinionBase tempOpinion = opinionExists(opinion);
			if ( tempOpinion != null ) list.add(tempOpinion);
		}
		return list;
	}
	public Set<OpinionBase> getAllOpinions() {
        return getOpinionTable();
    }
	public Set<StatuteCitation> getAllStatutes() {
        return getStatuteTable();
	}

    public void mergeParsedDocumentCitations(OpinionBase opinionBase, ParsedOpinionCitationSet parsedOpinionResults) {
    	for ( OpinionBase opinion: parsedOpinionResults.getOpinionTable() ) { 
    		OpinionBase existingOpinion = opinionExists(opinion);
            if (  existingOpinion == null ) {
            	// add citations where they don't already exist.
//                existingOpinion.mergeCitedOpinion(opinion);
//            } else {
            	persistOpinion(opinion);
            }
    	}
//TODO: WTF is all this about?    
    	for ( StatuteCitation statuteCitation: parsedOpinionResults.getStatuteTable() ) {
    		StatuteCitation existingStatute = statuteExists(statuteCitation);
    		if ( existingStatute == null) {
//    			OpinionStatuteCitation otherRef = statuteCitation.getOpinionStatuteReference(opinionBase);
//    			if( otherRef != null ) {
//        			existingStatute.incRefCount(opinionBase, otherRef.getCountReferences());
//    			}
//    		} else {
    			persistStatute(statuteCitation);
    		}
    	}
    }

}
