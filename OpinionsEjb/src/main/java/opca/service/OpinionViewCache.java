package opca.service;

import java.util.*;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;
import opca.parser.ParsedOpinionCitationSet;
import opca.view.*;
import service.Client;

@Stateless
public class OpinionViewCache {
    @Inject private EntityManager em;
    
	// viewModel items for display
	// function as caches for database calls
	private Date firstDate;
	private Date lastDate;
    private List<Date[]> reportDates;
    private List<OpinionView> allOpinionCases;
    
    public OpinionViewCache() {}
    public OpinionViewCache(EntityManager em,  Logger logger) {
    	this.em = em;
//    	this.logger = logger;
    }
    
    public Date getFirstDate() {
    	return firstDate;
    }
    public Date getLastDate() {
    	return lastDate;
    }
    
	public void buildCache() {
		reportDates = initReportDates();
		allOpinionCases = initOpinionCases();	// this depends on the initReportDates
	}

	public List<Date[]> getReportDates() {
		return reportDates;
	}

	public List<OpinionView> getAllOpinionCases() {
		return allOpinionCases;
	}

	private List<OpinionView> initOpinionCases() {
		ViewParameters viewInfo = new ViewParameters(firstDate, lastDate, true, 2);
		return getOpinionCases(viewInfo.sd, viewInfo.ed, viewInfo.compressCodeReferences, viewInfo.levelOfInterest);
	}
	
	public List<Date[]> initReportDates() {
		List<Date> dates = listPublishDates();
		List<Date[]> reportDates = new ArrayList<Date[]>();
		if ( dates.size() == 0 ) return reportDates;
		// set these internal variables.
		lastDate = dates.get(0);
		firstDate = dates.get(dates.size()-1);
		// do the work.
		Calendar firstDay = Calendar.getInstance();
		firstDay.setTime(dates.get(0));
		Calendar lastDay = Calendar.getInstance();
		lastDay.setTime(dates.get(0));
		bracketWeek( firstDay, lastDay );
		Date[] currentDates = new Date[2];
		for (Date date: dates) {
			if ( testBracket(date, firstDay, lastDay)) {
				addToCurrentDates(date, currentDates);
			} else {
				reportDates.add(currentDates);
				currentDates = new Date[2];
				firstDay.setTime(date);
				lastDay.setTime(date);
				bracketWeek(firstDay, lastDay);
				addToCurrentDates(date, currentDates);
			}
		}
		if ( currentDates[0] != null ) reportDates.add(currentDates);
		return reportDates;
	}
	
	private void addToCurrentDates(Date date, Date[] currentDates) {
		if (currentDates[0] == null ) {
			currentDates[0] = date;
			currentDates[1] = date;
			return;
		} else if ( currentDates[0].compareTo(date) > 0 ) {
			currentDates[0] = date;
			return;
		} else if ( currentDates[1].compareTo(date) < 0 ) {
			currentDates[1] = date;
			return;
		}
		return;
	}
	
	private boolean testBracket(Date date, Calendar firstDay, Calendar lastDay ) {
		boolean retVal = false;
		if ( firstDay.getTime().compareTo(date) < 0 && lastDay.getTime().compareTo(date) > 0 ) return true;
		return retVal;
	}
	/**
	 * This is round input dates to the first and last day of the week. The first and last day
	 * passed in should be the same. firstDay should be set backwards to getFirstDayOfWeek and 
	 * lastDay should be set forwards 1 week from the firstDay. 
	 * 
	 * @param firstDay return for firstDay
	 * @param lastDay return for lastDay
	 */
	public void bracketWeek(Calendar firstDay, Calendar lastDay ) {
		// get today and clear time of day
		firstDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		firstDay.clear(Calendar.MINUTE);
		firstDay.clear(Calendar.SECOND);
		firstDay.clear(Calendar.MILLISECOND);
		firstDay.set(Calendar.DAY_OF_WEEK, firstDay.getFirstDayOfWeek());
		firstDay.getTime();		// force recomputation. 

		// get today and clear time of day
		lastDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		lastDay.clear(Calendar.MINUTE);
		lastDay.clear(Calendar.SECOND);
		lastDay.clear(Calendar.MILLISECOND);
		lastDay.set(Calendar.DAY_OF_WEEK, lastDay.getFirstDayOfWeek());
		// start of the next week
		lastDay.add(Calendar.WEEK_OF_YEAR, 1);
	}
	
	public List<OpinionView> getOpinionCases(
		Date sd, 
		Date ed, 
		boolean compressCodeReferences, 
		int levelOfInterest
	) {
		List<OpinionView> opinionViews = new ArrayList<OpinionView>();
        Client statutesRs = new RestServicesFactory().connectStatutesRsService();
		//
		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesRs);
		List<SlipOpinion> opinions = findByPublishDateRange();
		List<OpinionBase> opinionOpinionCitations = new ArrayList<>();
		List<Integer> opinionIds = new ArrayList<>();
		int i = 0;
		for ( SlipOpinion slipOpinion: opinions ) {
			opinionIds.add(slipOpinion.getId());
			if ( ++i % 100 == 0 ) {
				opinionOpinionCitations.addAll( 
					em.createNamedQuery("OpinionBase.fetchOpinionCitationsForOpinions", OpinionBase.class).setParameter("opinionIds", opinionIds).getResultList()
				);
				opinionIds.clear();
			}
		}
		if ( opinionIds.size() != 0 ) {
			opinionOpinionCitations.addAll( 
				em.createNamedQuery("OpinionBase.fetchOpinionCitationsForOpinions", OpinionBase.class).setParameter("opinionIds", opinionIds).getResultList()
			);
		}
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setOpinionCitations( opinionOpinionCitations.get( opinionOpinionCitations.indexOf(slipOpinion)).getOpinionCitations() );
			ParsedOpinionCitationSet parserResults = new ParsedOpinionCitationSet(slipOpinion);
			OpinionView opinionView = opinionViewBuilder.buildOpinionView(slipOpinion, parserResults);
			opinionView.combineCommonSections();
			opinionView.trimToLevelOfInterest(levelOfInterest, true);
			opinionView.scoreCitations(opinionViewBuilder);
			
			opinionViews.add(opinionView);
		}
		return opinionViews;
	}

	// OpinionBase
	public OpinionBase opinionExists(OpinionBase opinion) {
		List<OpinionBase> list = em.createNamedQuery("OpinionBase.findByOpinionKey", OpinionBase.class).setParameter("key", opinion.getOpinionKey()).getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;
	}

	public List<OpinionBase> getOpinions(Collection<OpinionBase> opinions) {
		if ( opinions.size() == 0 ) return new ArrayList<OpinionBase>();
		List<OpinionKey> keys = new ArrayList<>();
		for(OpinionBase opinion: opinions) {
			keys.add(opinion.getOpinionKey());
		}
		return em.createNamedQuery("OpinionBase.findOpinionsForKeys", OpinionBase.class).setParameter("keys", keys).getResultList();
	}

	// StatuteCitation
	public StatuteCitation statuteExists(StatuteCitation statuteCitation) {
		List<StatuteCitation> list = em.createNamedQuery("StatuteCitation.findByStatuteKey", StatuteCitation.class)
			.setParameter("statuteKey", statuteCitation.getStatuteKey())
			.getResultList();
		if ( list.size() > 0 ) return list.get(0);
		return null;
	}

	public List<StatuteCitation> getStatutes(Collection<StatuteCitation> statuteCitations) {
		if ( statuteCitations.size() == 0 ) return new ArrayList<StatuteCitation>();
		List<StatuteKey> keys = new ArrayList<>();
		for(StatuteCitation statuteCitation: statuteCitations) {
			keys.add(statuteCitation.getStatuteKey());
		}
		return em.createNamedQuery("StatuteCitationData.findStatutesForKeys", StatuteCitation.class).setParameter("keys", keys).getResultList();
	}

	/**
	 * Going to do a two stage load I think. Enough to build view first stage, and then enough to do graph analysis second stage.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<SlipOpinion> findByPublishDateRange() {
		// just get all slip opinions
		List<SlipOpinion> opinions = em.createNamedQuery("SlipOpinion.loadOpinionsWithJoins", SlipOpinion.class).getResultList();

		// load slipOpinion properties from the database here ... ?
		List<SlipProperties> spl = em.createNamedQuery("SlipProperties.findAll", SlipProperties.class).getResultList();
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setSlipProperties(spl.get(spl.indexOf(new SlipProperties(slipOpinion))));
		}
		return opinions;
	}
	public List<Date> listPublishDates() {
		return em.createNamedQuery("SlipOpinion.listOpinionDates", Date.class).getResultList();
	}

	public StatuteCitation findStatute(StatuteKey key) {
		return (StatuteCitation) em.createNamedQuery("StatuteCitation.findByCodeSection").setParameter("code", key.getTitle()).setParameter("sectionNumber", key.getSectionNumber()).getResultList().get(0);
	}
	public OpinionBase findOpinion(OpinionKey key) {
		return (OpinionBase) em.createNamedQuery("OpinionBase.findByOpinionKey").setParameter("key", key).getResultList().get(0);
	}
	public List<SlipOpinion> listSlipOpinions() {
		return em.createQuery("select from SlipOpinion", SlipOpinion.class).getResultList();
	}

}
