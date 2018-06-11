package opca.service;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import opca.memorydb.PersistenceLookup;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;

@Stateless
public class SlipOpinionService {
    @Inject private EntityManager em;
	// viewModel items for display
	// function as caches for database calls
    
	// OpinionBase

    public SlipOpinionService() {}
    // debug constructor
    public SlipOpinionService(EntityManager em) {
    	this.em = em;
	}

    // used to lookup OpinionSummaries
	public OpinionBase opinionExists(OpinionBase opinionBase) {
		TypedQuery<OpinionBase> OpinionBaseFindByOpinionKey = em.createNamedQuery("OpinionBase.findByOpinionKey", OpinionBase.class);
		List<OpinionBase> list = OpinionBaseFindByOpinionKey.setParameter("key", opinionBase.getOpinionKey()).getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;
	}

	// StatuteCitation
	public StatuteCitation statuteExists(StatuteCitation statuteCitation) {
		TypedQuery<StatuteCitation> statuteCitationFindByTitleSection = em.createNamedQuery("StatuteCitation.findByStatuteKeyJoinReferringOpinions", StatuteCitation.class);
		List<StatuteCitation> list = 
				statuteCitationFindByTitleSection.setParameter("statuteKey", statuteCitation.getStatuteKey())
				.getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;
//		return em.find(StatuteCitation.class, primaryKey);
	}

	// StatuteCitation
	public List<StatuteCitation> statutesWithReferringOpinions(List<StatuteKey> statuteKeys) {
		return em.createNamedQuery("StatuteCitation.statutesWithReferringOpinions", StatuteCitation.class)
			.setParameter("statuteKeys", statuteKeys).getResultList();
	}

	public List<OpinionBase> getOpinions(Collection<OpinionBase> opinions) {
		if ( opinions.size() == 0 ) return new ArrayList<OpinionBase>();
		List<OpinionKey> keys = new ArrayList<>();
		for ( OpinionBase opinion: opinions) {
			keys.add(opinion.getOpinionKey());
		}
		return em.createNamedQuery("OpinionBase.findOpinionsForKeys", OpinionBase.class).setParameter("keys", keys).getResultList();
	}

	public List<StatuteCitation> getStatutes(Collection<StatuteCitation> statuteCitations) {
		if ( statuteCitations.size() == 0 ) return new ArrayList<StatuteCitation>();
		List<StatuteKey> keys = new ArrayList<>();
		for (StatuteCitation statuteCitation: statuteCitations) {
			keys.add(statuteCitation.getStatuteKey());
		}
		return em.createNamedQuery("StatuteCitation.findStatutesForKeys", StatuteCitation.class).setParameter("keys", keys).getResultList();
	}

	public StatuteCitation findStatute(StatuteCitation statuteCitation) {
		return em.createNamedQuery("StatuteCitation.findByStatuteKey", StatuteCitation.class).setParameter("statuteKey", statuteCitation.getStatuteKey()).getSingleResult();
	}

	public OpinionBase findOpinion(OpinionBase opinionBase) {
		return em.createNamedQuery("OpinionBase.findByOpinionKey", OpinionBase.class).setParameter("key", opinionBase.getOpinionKey()).getSingleResult();
	}

	public List<SlipOpinion> listSlipOpinions() {
		return em.createNamedQuery("SlipOpinion.findAll", SlipOpinion.class).getResultList();
	}	

	class MyPersistenceLookup implements PersistenceLookup {
		protected SlipOpinionService slipOpinionRepository;
		public MyPersistenceLookup(SlipOpinionService slipOpinionRepository) {
			this.slipOpinionRepository = slipOpinionRepository;
		}
		@Override
		public StatuteCitation statuteExists(StatuteCitation statuteCitation) {			
			return slipOpinionRepository.statuteExists(statuteCitation);
		}

		@Override
		public List<StatuteCitation> getStatutes(Collection<StatuteCitation> statuteCitations) {
			return slipOpinionRepository.getStatutes(statuteCitations);
		}

		@Override
		public OpinionBase opinionExists(OpinionBase opinionBase) {
			return slipOpinionRepository.opinionExists(opinionBase);
		}

		@Override
		public List<OpinionBase> getOpinions(Collection<OpinionBase> opinions) {
			return slipOpinionRepository.getOpinions(opinions);
		}	
	}
	
	public PersistenceLookup getPersistenceLookup() {
		return new MyPersistenceLookup(this); 
	}

	public Long getCount() {
        return em.createQuery("select count(*) from StatuteCitation", Long.class).getSingleResult();
    }

	public List<StatuteCitation> selectForTitle(String title) {
    	return em.createNamedQuery("StatuteCitation.selectForTitle", StatuteCitation.class).setParameter("title", '%'+title+'%').getResultList();
    }

	public StatuteCitation testStatuteByTitleSection(String title, String sectionNumber) {
    	List<StatuteCitation> list = em.createNamedQuery("StatuteCitation.findByTitleSection", StatuteCitation.class).setParameter("title", title).setParameter("sectionNumber", sectionNumber).getResultList();
    	if ( list.size() > 0 ) return list.get(0);
    	else return null;
    }

	public List<OpinionBase> opinionsWithReferringOpinions(List<OpinionKey> opinionKeys) {
    	return em.createNamedQuery("OpinionBase.opinionsWithReferringOpinions", OpinionBase.class).setParameter("opinionKeys", opinionKeys).getResultList();
	}
}
