package opca.memorydb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.OpinionSummary;
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
        return statuteExists(new StatuteKey(title, sectionNumber));
    }

    @Override
	public StatuteCitation statuteExists(StatuteKey key) {
		return findStatuteByStatute(new StatuteCitation(key));
	}

	public StatuteCitation findStatuteByStatute(StatuteCitation statuteCitation) {
        StatuteCitation foundCitation = dataBase.getStatuteTable().floor(statuteCitation);
        if ( statuteCitation.equals(foundCitation)) return foundCitation;
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
	public OpinionSummary opinionExists(OpinionKey key) {
        OpinionSummary tempOpinion = new OpinionSummary(key);
        if ( dataBase.getOpinionTable().contains(tempOpinion))
        	return dataBase.getOpinionTable().floor(tempOpinion);
        else return null;
	}

	public void persistOpinion(OpinionSummary opinionSummary) {
		dataBase.getOpinionTable().add(opinionSummary);
	}

	@Override
	public List<StatuteCitation> getStatutes(Collection<StatuteKey> statuteKeys) {
		List<StatuteCitation> list = new ArrayList<StatuteCitation>();
		for (StatuteKey key: statuteKeys ) {
			StatuteCitation statute = statuteExists(key);
			if ( statute != null ) list.add(statute);
		}
		return list;
	}

	@Override
	public List<OpinionSummary> getOpinions(Collection<OpinionKey> opinionKeys) {
		List<OpinionSummary> list = new ArrayList<OpinionSummary>();
		for (OpinionKey key: opinionKeys ) {
			OpinionSummary opinion = opinionExists(key);
			if ( opinion != null ) list.add(opinion);
		}
		return list;
	}
	public Set<OpinionSummary> getAllOpinions() {
        return dataBase.getOpinionTable();
    }
	public Set<StatuteCitation> getAllStatutes() {
        return dataBase.getStatuteTable();
	}

    public void mergeParsedDocumentCitations(OpinionBase opinionBase, ParsedOpinionCitationSet parsedOpinionResults) {
    	for ( OpinionSummary opinionSummary: parsedOpinionResults.getOpinionTable() ) { 
    		OpinionSummary existingOpinion = opinionExists(opinionSummary.getOpinionKey());
            if (  existingOpinion != null ) {
            	// add citations where they don't already exist.
                existingOpinion.mergeCitedOpinion(opinionSummary);
            } else {
            	persistOpinion(opinionSummary);
            }
    	}
    
    	for ( StatuteCitation statuteCitation: parsedOpinionResults.getStatuteTable() ) {
    		StatuteCitation existingStatute = statuteExists(statuteCitation.getStatuteKey());
    		if ( existingStatute != null) {
    			existingStatute.incRefCount(opinionBase.getOpinionKey(), statuteCitation.getRefCount(opinionBase.getOpinionKey()));
    		} else {
    			persistStatute(statuteCitation);
    		}
    	}
    }


}
