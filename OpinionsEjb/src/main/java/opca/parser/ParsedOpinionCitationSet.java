package opca.parser;

import java.util.TreeSet;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;

public class ParsedOpinionCitationSet {
    private TreeSet<OpinionBase> opinionTable = new TreeSet<OpinionBase>();
    private TreeSet<StatuteCitation> statuteTable = new TreeSet<StatuteCitation>();
    
    public ParsedOpinionCitationSet() {}


    public ParsedOpinionCitationSet(OpinionBase opinionBase) {
        statuteTable.addAll( opinionBase.getOnlyStatuteCitations());
        opinionTable.addAll( opinionBase.getOpinionCitations());
    }

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
