package opca.dto;

import java.util.*;

public class OpinionView {
//	private final Long id;
	private final String name;
    private final String fileName;
    private final String disposition;
    private final String summary;
    private final String title;
    private final Date opinionDate;
    
    public OpinionView(opca.view.OpinionView opinionView) {
//    	this.id = opinionView.getId();
    	this.name = opinionView.getName();
    	this.fileName = opinionView.getFileName();
    	this.disposition = opinionView.getDisposition();
    	this.summary = opinionView.getSummary();
    	this.title = opinionView.getTitle();
    	this.opinionDate = opinionView.getOpinionDate();
    }

//	public Long getId() {
//		return id;
//	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	public String getDisposition() {
		return disposition;
	}

	public String getSummary() {
		return summary;
	}

	public String getTitle() {
		return title;
	}

	public Date getOpinionDate() {
		return opinionDate;
	}

}
