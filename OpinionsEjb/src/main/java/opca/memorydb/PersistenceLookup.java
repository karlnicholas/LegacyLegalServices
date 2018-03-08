package opca.memorydb;

import java.util.Collection;
import java.util.List;

import opca.model.OpinionBase;
import opca.model.StatuteCitation;

public interface PersistenceLookup {
	StatuteCitation statuteExists(StatuteCitation statuteCitation);
	List<StatuteCitation> getStatutes(Collection<StatuteCitation> statuteCitations);
	OpinionBase opinionExists(OpinionBase opinionBase);
	List<OpinionBase> getOpinions(Collection<OpinionBase> opinions);
}

