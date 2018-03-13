package opca.memorydb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import opca.model.OpinionBase;
import opca.model.OpinionStatuteCitation;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;
import opca.parser.ParsedOpinionCitationSet;

public class CitationStore implements PersistenceLookup {
    
    protected MemoryDB dataBase;

    private CitationStore(){
        dataBase = new MemoryDB();
    }
    private static class SingletonHelper {
        private static final CitationStore INSTANCE = new CitationStore();
    }
    public static CitationStore getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
	public void clearDB() {
		dataBase.getStatuteTable().clear();
		dataBase.getOpinionTable().clear();
	}
    public int getCount() {
        return dataBase.getStatuteTable().size();
    }
/*
    public List<StatuteCitation> selectForCode(String code) {
        List<StatuteCitation> statutesForCode = Collections.synchronizedList(new ArrayList<StatuteCitation>());
        dataBase.getStatuteTable().stream().filter(new Predicate<StatuteCitation>() {
            @Override
            public boolean test(StatuteCitation codeCitation) {
            	if ( codeCitation.getStatuteKey().getCode() == null ) return false;
                return codeCitation.getStatuteKey().getCode().contains(code);
            }
        }).forEach(new Consumer<StatuteCitation>() {
            @Override
            public void accept(StatuteCitation codeCitation) {
                statutesForCode.add(codeCitation);
            }
        });
        return statutesForCode;
    }
*/
    public StatuteCitation findStatuteByCodeSection(String title, String sectionNumber) {
        return statuteExists(new StatuteCitation(new StatuteKey(title, sectionNumber)));
    }

    @Override
	public StatuteCitation statuteExists(StatuteCitation statuteCitation) {
		return findStatuteByStatute(statuteCitation);
	}

	public StatuteCitation findStatuteByStatute(StatuteCitation statuteCitation) {
        StatuteCitation foundCitation = dataBase.getStatuteTable().floor(statuteCitation);
        if ( statuteCitation.equals(foundCitation)) return foundCitation;
        return null;
	}    

	public OpinionBase findOpinionByOpinion(OpinionBase opinionBase) {
		OpinionBase foundOpinion = dataBase.getOpinionTable().floor(opinionBase);
        if ( opinionBase.equals(foundOpinion)) return foundOpinion;
		return null;
	}


	public void persistStatute(StatuteCitation statuteCitation) {
		dataBase.getStatuteTable().add(statuteCitation);
	}

	public void replaceStatute(StatuteCitation existingCitation, StatuteCitation newCitation) {
		dataBase.getStatuteTable().remove(existingCitation);
		dataBase.getStatuteTable().add(newCitation);
	}

	@Override
	public OpinionBase opinionExists(OpinionBase opinionBase) {
//        OpinionSummary tempOpinion = new OpinionSummary(opinionBase);
        if ( dataBase.getOpinionTable().contains(opinionBase))
        	return dataBase.getOpinionTable().floor(opinionBase);
        else return null;
	}

	public void persistOpinion(OpinionBase opinionBase) {
		dataBase.getOpinionTable().add(opinionBase);
	}

	@Override
	public List<StatuteCitation> getStatutes(Collection<StatuteCitation> statuteCitations) {
		List<StatuteCitation> list = new ArrayList<StatuteCitation>();
		for (StatuteCitation statuteCitation: statuteCitations ) {
			StatuteCitation statute = statuteExists(statuteCitation);
			if ( statute != null ) list.add(statute);
		}
		return list;
	}

	@Override
	public List<OpinionBase> getOpinions(Collection<OpinionBase> opinions) {
		List<OpinionBase> list = new ArrayList<OpinionBase>();
		for (OpinionBase opinion: opinions ) {
			OpinionBase tempOpinion = opinionExists(opinion);
			if ( tempOpinion != null ) list.add(tempOpinion);
		}
		return list;
	}
	public Set<OpinionBase> getAllOpinions() {
        return dataBase.getOpinionTable();
    }
	public Set<StatuteCitation> getAllStatutes() {
        return dataBase.getStatuteTable();
	}

    public void mergeParsedDocumentCitations(OpinionBase opinionBase, ParsedOpinionCitationSet parsedOpinionResults) {
    	for ( OpinionBase opinion: parsedOpinionResults.getOpinionTable() ) { 
    		OpinionBase existingOpinion = opinionExists(opinion);
            if (  existingOpinion != null ) {
            	// add citations where they don't already exist.
                existingOpinion.mergeCitedOpinion(opinion);
            } else {
            	persistOpinion(opinion);
            }
    	}
//TODO: WTF is all this about?    
    	for ( StatuteCitation statuteCitation: parsedOpinionResults.getStatuteTable() ) {
    		StatuteCitation existingStatute = statuteExists(statuteCitation);
    		if ( existingStatute != null) {
    			OpinionStatuteCitation otherRef = statuteCitation.getOpinionStatuteReference(opinionBase);
//    			if( otherRef != null ) {
        			existingStatute.incRefCount(opinionBase, otherRef.getCountReferences());
//    			}
    		} else {
    			persistStatute(statuteCitation);
    		}
    	}
    }

}
