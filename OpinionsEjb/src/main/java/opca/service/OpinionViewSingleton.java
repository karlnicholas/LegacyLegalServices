package opca.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;

import opca.ejb.util.StatutesServiceFactory;
import opca.model.OpinionKey;
import opca.view.OpinionView;
import statutes.service.StatutesService;

@Singleton
public class OpinionViewSingleton {
	@EJB private OpinionViewLoad opinionViewLoad;
	private OpinionViewData opinionViewData;
	public class OpinionViewData {
	    private List<Date[]> reportDates;
	    private List<OpinionView> opinionViews;    
		private List<String[]> stringDateList = new ArrayList<>();
		private boolean ready = false;
		
		public synchronized List<Date[]> getReportDates() {
			return reportDates;
		}
		public synchronized void setReportDates(List<Date[]> reportDates) {
			this.reportDates = reportDates;
		}
		public synchronized List<OpinionView> getOpinionViews() {
			return opinionViews;
		}
		public synchronized void setOpinionViews(List<OpinionView> opinionViews) {
			this.opinionViews = opinionViews;
		}
		public synchronized boolean isReady() {
			return ready;
		}
		public synchronized void setReady(boolean ready) {
			this.ready = ready;
		}
		public void setStringDateList() {
			stringDateList.clear();
			SimpleDateFormat lform = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sform = new SimpleDateFormat("MMM dd");
			List<Date[]> reportDates = getReportDates();
			for ( Date[] dates: reportDates ) {
				//TODO fix this dates having null in the dates list
				if ( dates[0] == null || dates[1] == null ) continue;  
				String[] e = new String[2]; 
				e[0] = String.format("%s - %s", 
					sform.format(dates[0]),
					sform.format(dates[1]));
				e[1] = String.format("?startDate=%s", lform.format(dates[0]));
				stringDateList.add(e);	
			}
		}
	}
	
	public OpinionViewSingleton() {
		opinionViewData = new OpinionViewData();
	}
	public OpinionViewSingleton(EntityManager em) {
		this();
		opinionViewLoad = new OpinionViewLoad(em);
	}
	
	@PostConstruct
	public void postConstruct() {
		StatutesService statutesService = StatutesServiceFactory.getStatutesServiceClient();
		opinionViewLoad.load(opinionViewData, statutesService);
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
		List<OpinionView> opinionViewCopy = new ArrayList<OpinionView>();
		for (OpinionView opinionCase: opinionViewData.getOpinionViews() ) {
			if ( 
				opinionCase.getOpinionDate().compareTo(viewInfo.sd) >= 0  
				&& opinionCase.getOpinionDate().compareTo(viewInfo.ed) <= 0
			) {
				opinionViewCopy.add(opinionCase);
			}
		}
		return opinionViewCopy;
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
    	Date dateRecent = opinionViewData.getReportDates().get(0)[0];    	
    	for ( Date[] dates: opinionViewData.getReportDates() ) {
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

	public boolean isReady() {
		return opinionViewData.isReady();
	}

	public List<Date[]> getReportDates() {
		return opinionViewData.getReportDates();
	}

	public void updateOpinionViews(List<OpinionKey> opinionKeys, StatutesService statutesService) {
		if ( opinionViewData.isReady() ) {
			opinionViewLoad.loadNewOpinions(opinionViewData, opinionKeys, statutesService);
		}
	}
	public List<String[]> getStringDateList() {
		return opinionViewData.stringDateList;
	}
}
