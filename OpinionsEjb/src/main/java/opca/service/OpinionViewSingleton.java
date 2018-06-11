package opca.service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;

import opca.view.OpinionView;

@Singleton
@SuppressWarnings("serial")
public class OpinionViewSingleton implements Serializable {
	@Inject Logger logger;
	@EJB private OpinionViewCache slipOpinionCache;
	private final Object lock = new Object();
	private Date lastBuildDate;
	
	private List<Date[]> reportDates;
    private List<OpinionView> allOpinionCases;
	
	public List<OpinionView> getSlipOpinions() {
		synchronized(lock) {
			return allOpinionCases;
		}
	}
	
	public List<Date[]> getReportDates() {
		synchronized(lock) {
			return reportDates;
		}
	}
	
	@PostConstruct
	public void postConstruct() {
		synchronized(lock) {
			logger.info("postConstruct()");
			slipOpinionCache.buildCache();
			// ignore synchronization issues
			// so maybe someone get report dates a little out of whack.
			reportDates = slipOpinionCache.getReportDates();
			allOpinionCases = slipOpinionCache.getAllOpinionCases();
			lastBuildDate = new Date();
		}
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
		for (OpinionView opinionCase: slipOpinionCache.getAllOpinionCases() ) {
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

}
