package opca.memorydb;

import java.util.Collection;
import java.util.List;

import opca.model.OpinionKey;
import opca.model.OpinionSummary;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;

public interface PersistenceLookup {
	StatuteCitation statuteExists(StatuteKey statuteKey);
	List<StatuteCitation> getStatutes(Collection<StatuteKey> statuteKeys);
	OpinionSummary opinionExists(OpinionKey opinionKey);
	List<OpinionSummary> getOpinions(Collection<OpinionKey> opinionKeys);
}

