package opca.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import opca.service.OpinionViewSingleton;
import opca.service.ViewParameters;
import opca.view.OpinionView;

@ManagedBean
public class OpinionsController {
	@Inject private OpinionViewSingleton opinionViewSingleton;

    private int currentIndex;
    private String openOpinion;
    private String startDate;
	private String currentDate;
    
    public String getAnchorScript() {
    	return openOpinion!=null?"location.href='#anchor_"+openOpinion+"'":null;
    }
    
    @PostConstruct
    public void postConstruct() {
    	openOpinion = null;
    }
    
    public void checkStartDate() {
    	if ( !opinionViewSingleton.isReady() ) {
	    	setCurrentDate("Loading"); 
    		return;
    	}
    	if ( startDate != null ) {
        	currentIndex = opinionViewSingleton.currentDateIndex(startDate);
    	} else {
    		currentIndex = 0;
    	}
    	SimpleDateFormat sform = new SimpleDateFormat("MMM dd");
    	List<Date[]> reportDates = opinionViewSingleton.getReportDates();
    	if( reportDates.size() > 0 ) {
	    	setCurrentDate(String.format("%s - %s", 
				sform.format(reportDates.get(currentIndex)[0]),
				sform.format(reportDates.get(currentIndex)[1])
			));
    	}
    }
    public void openOpinion(String name) throws IOException {
    	openOpinion = name;
    }
    public void closeOpinion() throws IOException {
    	openOpinion = null;
    }
    public boolean opinionOpen(String name) {
    	return (openOpinion != null && openOpinion.indexOf(name)>=0);
    }

    public List<OpinionView> getOpinionViewList() {
		// done this way so that this information is not serialized in the viewScope
    	Date[] dates = opinionViewSingleton.getReportDates().get(currentIndex);
		ViewParameters viewInfo = new ViewParameters(dates[0],dates[1]);
		return opinionViewSingleton.getOpinionCasesForAccount(viewInfo);
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	public List<String[]> getDateList() {
		return opinionViewSingleton.getStringDateList();
	}
	public boolean isCacheReady() {
		return opinionViewSingleton.isReady();
	}
	//TODO: does this belong in JsfUtils?
	public List<Integer> repeatNTimes(int count) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i=0; i < count; ++i) {
			result.add(i);
		}
		return result;
	}
}
