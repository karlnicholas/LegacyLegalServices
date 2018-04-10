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
//    	initNamedQueries();
	}
/*
    @PostConstruct
    public void initNamedQueries() {
    	OpinionBaseFindByOpinionKey = em.createNamedQuery("OpinionBase.findByOpinionKey", OpinionBase.class);
    	statuteCitationFindByTitleSection = em.createNamedQuery("StatuteCitation.findByTitleSection", StatuteCitation.class);
    }
*/    
	// used to lookup OpinionSummaries
	public OpinionBase opinionExists(OpinionBase opinionBase) {
		TypedQuery<OpinionBase> OpinionBaseFindByOpinionKey = em.createNamedQuery("OpinionBase.findByOpinionKey", OpinionBase.class);
		List<OpinionBase> list = OpinionBaseFindByOpinionKey.setParameter("key", opinionBase.getOpinionKey()).getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;

//		return em.find(OpinionBase.class, primaryKey);
	}

	public OpinionBase opinionExistsWithReferringOpinions(OpinionBase opinionBase) {
		TypedQuery<OpinionBase> OpinionBaseFindByOpinionKey = em.createNamedQuery("OpinionBase.findOpinionByKeyFetchReferringOpinions", OpinionBase.class);
		List<OpinionBase> list = OpinionBaseFindByOpinionKey.setParameter("key", opinionBase.getOpinionKey()).getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;
	}	
	// StatuteCitation
	public StatuteCitation statuteExists(StatuteCitation statuteCitation) {
		TypedQuery<StatuteCitation> statuteCitationFindByTitleSection = em.createNamedQuery("StatuteCitation.findByStatuteKey", StatuteCitation.class);
		List<StatuteCitation> list = 
				statuteCitationFindByTitleSection.setParameter("statuteKey", statuteCitation.getStatuteKey())
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

	public List<OpinionBase> getOpinions(Collection<OpinionBase> opinions) {
		if ( opinions.size() == 0 ) return new ArrayList<OpinionBase>();
		List<OpinionKey> keys = new ArrayList<>();
		for ( OpinionBase opinion: opinions) {
			keys.add(opinion.getOpinionKey());
		}
		return em.createNamedQuery("OpinionBase.findOpinionsForKeys", OpinionBase.class).setParameter("keys", keys).getResultList();
	}

	public void persistOpinion(OpinionBase opinion) {
		em.persist(opinion);
	}

	public OpinionBase mergeOpinion(OpinionBase opinion) {
		return em.merge(opinion);
	}

	public List<StatuteCitation> getStatutes(Collection<StatuteCitation> statuteCitations) {
		if ( statuteCitations.size() == 0 ) return new ArrayList<StatuteCitation>();
		List<StatuteKey> keys = new ArrayList<>();
		for (StatuteCitation statuteCitation: statuteCitations) {
			keys.add(statuteCitation.getStatuteKey());
		}
		return em.createNamedQuery("StatuteCitation.findStatutesForKeys", StatuteCitation.class).setParameter("keys", keys).getResultList();
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

	public StatuteCitation findStatute(StatuteCitation statuteCitation) {
		return em.createNamedQuery("StatuteCitation.findByStatuteKey", StatuteCitation.class).setParameter("StatuteKey", statuteCitation.getStatuteKey()).getSingleResult();
	}

	public OpinionBase findOpinion(OpinionBase opinionBase) {
		return em.createNamedQuery("OpinionBase.findByOpinionKey", OpinionBase.class).setParameter("key", opinionBase.getOpinionKey()).getSingleResult();
	}

	public SlipOpinion loadOpinion(OpinionKey opinionKey) {
		return em.createNamedQuery("SlipOpinion.loadOpinion", SlipOpinion.class).setParameter("key", opinionKey).getSingleResult();
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
		public void persistOpinion(OpinionBase opinion) {
			slipOpinionRepository.persistOpinion(opinion);
			
		}

		@Override
		public OpinionBase mergeOpinion(OpinionBase opinion) {
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
    public List<StatuteCitation> selectForTitle(String title) {
    	return em.createNamedQuery("StatuteCitation.selectForTitle", StatuteCitation.class).setParameter("title", '%'+title+'%').getResultList();
    }
    public StatuteCitation testStatuteByTitleSection(String title, String sectionNumber) {
    	List<StatuteCitation> list = em.createNamedQuery("StatuteCitation.findByTitleSection", StatuteCitation.class).setParameter("title", title).setParameter("sectionNumber", sectionNumber).getResultList();
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
