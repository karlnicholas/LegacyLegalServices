package opca.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.parser.ParsedOpinionCitationSet;
import opca.service.OpinionViewSingleton.OpinionViewData;
import opca.view.OpinionView;
import opca.view.OpinionViewBuilder;
import service.Client;

@SuppressWarnings("serial")
@Stateless
public class OpinionViewLoad  implements Serializable {
	@Inject private Logger logger;
	@Inject private EntityManager em;

	public OpinionViewLoad() {}
	
	public OpinionViewLoad(EntityManager em) {
		this.em = em;
		this.logger = Logger.getLogger(OpinionViewLoad.class.getName());
	}

	@Asynchronous
	public void load(OpinionViewData opinionViewData) {
		opinionViewData.setReady( false );
		logger.info("load start");
		// ignore synchronization issues
		// so maybe someone get report dates a little out of whack.
		initReportDates(opinionViewData);
		buildOpinionViews(opinionViewData);
		logger.info("load finish: " + opinionViewData.getOpinionViews().size());
		opinionViewData.setReady( true );
	}

	@Asynchronous
	public void loadNewOpinions(OpinionViewData opinionViewData, List<OpinionKey> opinionKeys) {
		opinionViewData.setReady( false );
		logger.info("loadNewOpinions start");
		// ignore synchronization issues
		// so maybe someone get report dates a little out of whack.
		initReportDates(opinionViewData);
		buildOpinionViews(opinionViewData);
		logger.info("loadNewOpinions finish: " + opinionViewData.getOpinionViews().size());
		opinionViewData.setReady( true );
	}

	private void initReportDates(OpinionViewData opinionViewData) {
		List<Date> dates = listPublishDates();
		List<Date[]> reportDates = new ArrayList<Date[]>();
		if ( dates.size() == 0 ) return;
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
		opinionViewData.setReportDates(reportDates);
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
	
	private void buildOpinionViews(OpinionViewData opinionViewData) {
		List<OpinionView> opinionViews = new ArrayList<>();
		List<SlipOpinion> opinions = loadAllSlipOpinions();
		List<OpinionBase> opinionOpinionCitations = new ArrayList<>();
		List<Integer> opinionIds = new ArrayList<>();
		TypedQuery<OpinionBase> fetchOpinionCitationsForOpinions = em.createNamedQuery("OpinionBase.fetchOpinionCitationsForOpinions", OpinionBase.class);
		int i = 0;
		for ( SlipOpinion slipOpinion: opinions ) {
			opinionIds.add(slipOpinion.getId());
			if ( ++i % 100 == 0 ) {
				opinionOpinionCitations.addAll( 
					fetchOpinionCitationsForOpinions.setParameter("opinionIds", opinionIds).getResultList()
				);
				opinionIds.clear();
			}
		}
		if ( opinionIds.size() != 0 ) {
			opinionOpinionCitations.addAll( 
				fetchOpinionCitationsForOpinions.setParameter("opinionIds", opinionIds).getResultList()
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
		opinionViewData.setOpinionViews(opinionViews);
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
