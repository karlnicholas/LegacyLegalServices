package opca.service;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import opca.memorydb.PersistenceLookup;
import opca.model.OpinionKey;
import opca.model.OpinionSummary;
import opca.model.SlipOpinion;
import opca.model.StatuteCitation;
import opca.model.StatuteKeyEntity;

@Stateless
public class SlipOpinionService {
    @Inject private EntityManager em;
	// viewModel items for display
	// function as caches for database calls
    
	// OpinionSummary

    public SlipOpinionService() {}
    // debug constructor
    public SlipOpinionService(EntityManager em) {
    	this.em = em;
//    	initNamedQueries();
	}
/*
    @PostConstruct
    public void initNamedQueries() {
    	opinionSummaryFindByOpinionKey = em.createNamedQuery("OpinionSummary.findByOpinionKey", OpinionSummary.class);
    	statuteCitationFindByCodeSection = em.createNamedQuery("StatuteCitation.findByCodeSection", StatuteCitation.class);
    }
*/    
	// used to lookup OpinionSummaries
	public OpinionSummary opinionExists(OpinionKey primaryKey) {
		TypedQuery<OpinionSummary> opinionSummaryFindByOpinionKey = em.createNamedQuery("OpinionSummary.findByOpinionKey", OpinionSummary.class);
		List<OpinionSummary> list = opinionSummaryFindByOpinionKey.setParameter("key", primaryKey).getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;

//		return em.find(OpinionSummary.class, primaryKey);
	}

	// StatuteCitation
	public StatuteCitation statuteExists(StatuteKeyEntity primaryKey) {
		TypedQuery<StatuteCitation> statuteCitationFindByCodeSection = em.createNamedQuery("StatuteCitation.findByCodeSection", StatuteCitation.class);
		List<StatuteCitation> list = 
				statuteCitationFindByCodeSection.setParameter("code", primaryKey.getCode())
				.setParameter("sectionNumber", primaryKey.getSectionNumber())
				.getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;
//		return em.find(StatuteCitation.class, primaryKey);
	}


	public SlipOpinion slipOpinionExists(OpinionKey opinionKey) {
		List<SlipOpinion> list = em.createNamedQuery("SlipOpinion.findByOpinionKey", SlipOpinion.class).setParameter("key", opinionKey).getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;
	}

	public List<OpinionSummary> getOpinions(Collection<OpinionKey> opinionKeys) {
		if ( opinionKeys.size() == 0 ) return new ArrayList<OpinionSummary>(); 
		return em.createNamedQuery("OpinionSummary.findOpinionsForKeys", OpinionSummary.class).setParameter("keys", opinionKeys).getResultList();
	}

	public void persistOpinion(OpinionSummary opinion) {
		em.persist(opinion);
	}

	public OpinionSummary mergeOpinion(OpinionSummary opinion) {
		return em.merge(opinion);
	}

	public List<StatuteCitation> getStatutes(Collection<StatuteKeyEntity> statuteKeys) {
		if ( statuteKeys.size() == 0 ) return new ArrayList<StatuteCitation>();
		return em.createNamedQuery("StatuteCitation.findStatutesForKeys", StatuteCitation.class).setParameter("keys", statuteKeys).getResultList();
	}

	public void persistStatute(StatuteCitation statute) {
		em.persist(statute);
	}

	public StatuteCitation mergeStatute(StatuteCitation statute) {
		return em.merge(statute);
	}
	public List<SlipOpinion> findByPublishDateRange(Date startDate, Date endDate) {
		return em.createNamedQuery("SlipOpinion.findByOpinionDateRange", SlipOpinion.class).setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList();
	}
	public List<Date> listPublishDates() {
		return em.createNamedQuery("SlipOpinion.listOpinionDates", Date.class).getResultList();
	}

	public StatuteCitation findStatute(StatuteKeyEntity key) {
		return em.createNamedQuery("StatuteCitation.findByCodeSection", StatuteCitation.class).setParameter("code", key.getCode()).setParameter("sectionNumber", key.getSectionNumber()).getSingleResult();
	}

	public OpinionSummary findOpinion(OpinionKey key) {
		return em.createNamedQuery("OpinionSummary.findByOpinionKey", OpinionSummary.class).setParameter("key", key).getSingleResult();
	}

	public List<SlipOpinion> listSlipOpinions() {
		return em.createQuery("select s from SlipOpinion s", SlipOpinion.class).getResultList();
	}

	public void removeSlipOpinions(List<SlipOpinion> oldOpinions) {
		for( SlipOpinion slipOpinion: oldOpinions ) {
			em.remove(slipOpinion);
		}
	}

	public void mergeAndPersistSlipOpinions(List<SlipOpinion> newOpinions) {
		Iterator<SlipOpinion> cit = newOpinions.iterator();
		while ( cit.hasNext() ) {
			SlipOpinion newOpinion = cit.next();
			em.persist(em.merge(newOpinion));
		}
	}
	
	class MyPersistenceLookup implements PersistenceLookup {
		protected SlipOpinionService slipOpinionRepository;
		public MyPersistenceLookup(SlipOpinionService slipOpinionRepository) {
			this.slipOpinionRepository = slipOpinionRepository;
		}
		@Override
		public StatuteCitation statuteExists(StatuteKeyEntity statuteKey) {			
			return slipOpinionRepository.statuteExists(statuteKey);
		}

		@Override
		public List<StatuteCitation> getStatutes(Collection<StatuteKeyEntity> statuteKeys) {
			return slipOpinionRepository.getStatutes(statuteKeys);
		}

		@Override
		public OpinionSummary opinionExists(OpinionKey opinionKey) {
			return slipOpinionRepository.opinionExists(opinionKey);
		}

		@Override
		public List<OpinionSummary> getOpinions(Collection<OpinionKey> opinionKeys) {
			return slipOpinionRepository.getOpinions(opinionKeys);
		}	
	}
	
	public PersistenceLookup getPersistenceLookup() {
		return new MyPersistenceLookup(this); 
	}
/*	
	class MyPersistenceInterface extends MyPersistenceLookup implements CitationStore  {
		public MyPersistenceInterface(SlipOpinionService slipOpinionRepository) {
			super(slipOpinionRepository);
		}

		@Override
		public void persistStatute(StatuteCitation statute) {
			slipOpinionRepository.persistStatute(statute);
		}

		@Override
		public StatuteCitation mergeStatute(StatuteCitation statute) {
			return slipOpinionRepository.mergeStatute(statute);
		}

		@Override
		public void persistOpinion(OpinionSummary opinion) {
			slipOpinionRepository.persistOpinion(opinion);
			
		}

		@Override
		public OpinionSummary mergeOpinion(OpinionSummary opinion) {
			return slipOpinionRepository.mergeOpinion(opinion);
		}
	}
	
	public CitationStore getPersistenceInterface() {
		return new MyPersistenceInterface(this); 
	}
*/	
    public Long getCount() {
        return em.createQuery("select count(*) from StatuteCitation", Long.class).getSingleResult();
    }
    public List<StatuteCitation> selectForCode(String code) {
    	return em.createNamedQuery("StatuteCitation.selectForCode", StatuteCitation.class).setParameter("code", '%'+code+'%').getResultList();
    }
    public StatuteCitation testStatuteByCodeSection(String code, String sectionNumber) {
    	List<StatuteCitation> list = em.createNamedQuery("StatuteCitation.findByCodeSection", StatuteCitation.class).setParameter("code", code).setParameter("sectionNumber", sectionNumber).getResultList();
    	if ( list.size() > 0 ) return list.get(0);
    	else return null;
    }
/*    
	public void fetchCitations(List<SlipOpinion> slipOpinions) {
		TypedQuery<StatuteKey> fetchStatuteCitations = em.createNamedQuery("SlipOpinion.fetchStatuteCitations", StatuteKey.class);
		TypedQuery<OpinionKey> fetchOpinionCitations = em.createNamedQuery("SlipOpinion.fetchOpinionCitations", OpinionKey.class);
		for ( SlipOpinion slipOpinion: slipOpinions) {
			slipOpinion.setStatuteCitations(new TreeSet<StatuteKey>(fetchStatuteCitations.setParameter("key", slipOpinion.getOpinionKey()).getResultList()));
			slipOpinion.setOpinionCitations(new TreeSet<OpinionKey>(fetchOpinionCitations.setParameter("key", slipOpinion.getOpinionKey()).getResultList()));
		}
	}
*/	
}
