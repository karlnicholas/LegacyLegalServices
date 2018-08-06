package opca.service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import opca.model.OpinionBase;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.parser.ParsedOpinionCitationSet;
import opca.view.OpinionView;
import opca.view.OpinionViewBuilder;
import service.Client;

@Singleton
@SuppressWarnings("serial")
public class OpinionViewSingleton implements Serializable {
	@Inject Logger logger;
    @Inject private EntityManager em;
    private List<Date[]> reportDates;
    private List<OpinionView> allOpinionCases;    
	private Date lastBuildDate;
	
	public OpinionViewSingleton() {}
	public OpinionViewSingleton(EntityManager em) {
		this.em = em;
	}

	public List<OpinionView> getSlipOpinions() {
		return allOpinionCases;
	}
	
	@PostConstruct
	public void postConstruct() {
		logger.info("postConstruct()");
		buildCache();
		// ignore synchronization issues
		// so maybe someone get report dates a little out of whack.
		reportDates = getReportDates();
		allOpinionCases = getAllOpinionCases();
		lastBuildDate = new Date();
	}

	public Date getLastBuildDate() {
		return lastBuildDate;
	}

	/*
	 * Dynamic method (for now?)
	 */
	public List<OpinionView> getOpinionCasesForAccount(ViewParameters viewInfo) {
		List<OpinionView> opinionViewList = copyCasesForViewinfo(viewInfo);
		viewInfo.totalCaseCount = opinionViewList.size();
		viewInfo.accountCaseCount = opinionViewList.size();
		return opinionViewList;
	}
	
	private List<OpinionView> copyCasesForViewinfo(ViewParameters viewInfo) {
		List<OpinionView> opinionViews = new ArrayList<OpinionView>();
		for (OpinionView opinionCase: getAllOpinionCases() ) {
			if ( 
				opinionCase.getOpinionDate().compareTo(viewInfo.sd) >= 0  
				&& opinionCase.getOpinionDate().compareTo(viewInfo.ed) <= 0
			) {
				opinionViews.add(opinionCase);
			}
		}
		return opinionViews;
	}
	
	public Date dateParam(String startDate) {
    	SimpleDateFormat lform = new SimpleDateFormat("yyyy-MM-dd");
    	Date dateParam = null;
    	if ( startDate != null && !startDate.trim().isEmpty() ) {
    		try {
				dateParam = lform.parse(startDate);
			} catch (ParseException ignored) {
			}
    	}
    	return dateParam;
	}
	
	public int currentDateIndex(String startDate) {
		Date dateParam = dateParam(startDate);
    	int i=0;
    	int currentIndex = 0;
    	Date dateRecent = reportDates.get(0)[0];
    	
    	for ( Date[] dates: reportDates ) {
    		//TODO fix this dates having null in the dates list
    		if ( dates[0] == null || dates[1] == null ) continue;  
    		if ( dateParam != null ) {
	    		if ( dateParam.compareTo(dateRecent) < 0 ) {
	    			dateRecent = dates[0];
	    	    	currentIndex = i;
	    		} 
    		}
    		i++;
    	}
    	return currentIndex;
	}

	private void buildCache() {
		reportDates = initReportDates();
		allOpinionCases = getOpinionViews();
	}

	public List<Date[]> getReportDates() {
		return reportDates;
	}

	private List<OpinionView> getAllOpinionCases() {
		return allOpinionCases;
	}

	private List<Date[]> initReportDates() {
		List<Date> dates = listPublishDates();
		List<Date[]> reportDates = new ArrayList<Date[]>();
		if ( dates.size() == 0 ) return reportDates;
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
	private void bracketWeek(Calendar firstDay, Calendar lastDay ) {
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
	
	public List<OpinionView> getOpinionViews() {
		List<OpinionView> opinionViews = new ArrayList<OpinionView>();
		//
		List<SlipOpinion> opinions = loadAllSlipOpinions();
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
        Client statutesRs = new RestServicesFactory().connectStatutesRsService();
		OpinionViewBuilder opinionViewBuilder = new OpinionViewBuilder(statutesRs);
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setOpinionCitations( opinionOpinionCitations.get( opinionOpinionCitations.indexOf(slipOpinion)).getOpinionCitations() );
			ParsedOpinionCitationSet parserResults = new ParsedOpinionCitationSet(slipOpinion);
			OpinionView opinionView = opinionViewBuilder.buildOpinionView(slipOpinion, parserResults);
			opinionViews.add(opinionView);
		}
		return opinionViews;
	}

	/**
	 * Going to do a two stage load I think. Enough to build view first stage, and then enough to do graph analysis second stage.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<SlipOpinion> loadAllSlipOpinions() {
		// just get all slip opinions
		List<SlipOpinion> opinions = em.createNamedQuery("SlipOpinion.loadOpinionsWithJoins", SlipOpinion.class).getResultList();

		// load slipOpinion properties from the database here ... ?
		List<SlipProperties> spl = em.createNamedQuery("SlipProperties.findAll", SlipProperties.class).getResultList();
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setSlipProperties(spl.get(spl.indexOf(new SlipProperties(slipOpinion))));
		}
		return opinions;
	}

	private List<Date> listPublishDates() {
		return em.createNamedQuery("SlipOpinion.listOpinionDates", Date.class).getResultList();
	}
}
