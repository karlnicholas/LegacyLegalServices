package opca.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import opca.service.OpinionViewSingleton;
import opca.service.ViewParameters;
import opca.view.OpinionView;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class OpinionsController implements Serializable {
	@Inject private OpinionViewSingleton opinionViewSingleton;

    private int currentIndex;
    private String openOpinion;
    private String startDate;
	private String currentDate;
    private List<String[]> dateList;
    
    @PostConstruct
    private void postConstruct() {
		openOpinion = null; 
    	dateList = new ArrayList<String[]>();
/*    	
    	SimpleDateFormat lform = new SimpleDateFormat("yyyy-MM-dd");
    	SimpleDateFormat sform = new SimpleDateFormat("MMM dd");
    	List<Date[]> reportDates = opinionViewSingleton.getReportDates();
    	for ( Date[] dates: reportDates ) {
    		//TODO fix this dates having null in the dates list
    		if ( dates[0] == null || dates[1] == null ) continue;  
    		String[] e = new String[2]; 
    		e[0] = String.format("%s - %s", 
    			sform.format(dates[0]),
    			sform.format(dates[1]));
    		e[1] = String.format("?startDate=%s", lform.format(dates[0]));
    		dateList.add(e);	
    	}
*/    	
    }
    
    public String getAnchorScript() {
    	return openOpinion!=null?"location.href='#anchor_"+openOpinion+"'":null;
    }
    
    public void checkStartDate() {
/*    	
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
*/    	
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
		return dateList;
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
