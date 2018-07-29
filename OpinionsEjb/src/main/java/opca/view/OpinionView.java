package opca.view;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import opca.model.SlipOpinion;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class OpinionView {
	private static final int MAX_INFO_LENGTH = 75;
	// reverse sorted by the constructor.
	private List<StatuteView> statutes;
	// reverse sorted by the constructor.
	private List<CaseView> cases;
	private String name;
	private String title;
	private Date opinionDate;
	private String fileName;
	private String disposition;
	private String summary;
	
	public OpinionView() {
		super();
	}
	public OpinionView(
		SlipOpinion slipOpinion, 
		String name, 
		List<StatuteView> statutes, 
		List<CaseView> cases
	) {
		this.name = name;
		this.statutes = statutes;
		this.title = slipOpinion.getTitle();
		this.fileName = slipOpinion.getFileName();
		this.opinionDate = slipOpinion.getOpinionDate();
		this.disposition = slipOpinion.getDisposition();
		this.summary = slipOpinion.getSummary();
		
//		Collections.sort(this.statutes);
//		Collections.reverse(this.statutes);
		this.cases = cases;
//		Collections.sort(this.cases);
//		Collections.reverse(this.cases);
	}
	
    @XmlElement
    public List<SectionView> getSectionViews() {
    	List<SectionView> sectionViews = new ArrayList<>();
    	for ( StatuteView statueView: statutes ) {
    		sectionViews.addAll( statueView.getSectionViews() );
    	}
    	return sectionViews;
    }
	// supporting methods for JSF pages
	public String getCondensedStatuteInfo() {
		StringBuilder sb = new StringBuilder();
		boolean shortened = false;
		for (StatuteView statuteView: statutes) {
			sb.append(statuteView.getShortTitle());
			sb.append("  [");
			sb.append(statuteView.getRefCount());
			sb.append("], ");
			if ( sb.length() > MAX_INFO_LENGTH ) {
				sb.delete(sb.length()-2, sb.length()-1);
				sb.append("...");
				shortened = true;
				break;
			}
		}
		if( sb.length() >= 3 && !shortened ) {
			sb.delete(sb.length()-2, sb.length()-1);
		}
		return sb.toString();
	}
	
	public String getCondensedCaseInfo() {
		StringBuilder sb = new StringBuilder();
		boolean shortened = false;
		for (CaseView caseView: cases) {
			if ( caseView.getTitle() == null ) {
				sb.append(caseView.getCitation());
			} else {
				sb.append(caseView.getTitle());
			}
			sb.append(", ");
			if ( sb.length() > MAX_INFO_LENGTH ) {
				sb.delete(sb.length()-2, sb.length()-1);
				sb.append("...");
				shortened = true;
				break;
			}
		}
		if( sb.length() >= 3 && !shortened ) {
			sb.delete(sb.length()-2, sb.length()-1);
		}
		return sb.toString();
	}
    @XmlTransient
    public List<StatuteView> getStatutes() {
        return statutes;
    }
    public void setStatutes(List<StatuteView> statutes) {
        this.statutes = statutes;
    }
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
    @XmlTransient
	public List<CaseView> getCases() {
		return cases;
	}
	public void setCases(List<CaseView> cases) {
		this.cases = cases;
	}
	@Override
    public String toString() {
    	return name + " " + this.getTitle();
    }
	@XmlElement
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getOpinionDate() {
		return opinionDate;
	}
	public void setOpinionDate(Date opinionDate) {
		this.opinionDate = opinionDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    @XmlTransient
	public String getDisposition() {
		return disposition;
	}
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
    @XmlTransient
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
}

