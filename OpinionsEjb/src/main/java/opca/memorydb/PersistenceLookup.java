package opca.memorydb;

import java.util.Collection;
import java.util.List;

import opca.model.OpinionKey;
import opca.model.OpinionSummary;
import opca.model.StatuteCitation;
import opca.model.StatuteKeyEntity;

public interface PersistenceLookup {
	StatuteCitation statuteExists(StatuteKeyEntity statuteKey);
	List<StatuteCitation> getStatutes(Collection<StatuteKeyEntity> statuteKeys);
	OpinionSummary opinionExists(OpinionKey opinionKey);
	List<OpinionSummary> getOpinions(Collection<OpinionKey> opinionKeys);
}

