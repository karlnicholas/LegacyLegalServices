package opca.view;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CaseView implements Comparable<CaseView> {
	private String title;
	private String citation;
	private Date opinionDate;
	@XmlTransient
	private int countReferringOpinions;
	@XmlTransient
	private int score;
	private int importance;
	
	public CaseView() {}
	public CaseView(String title, String citation, Date opinionDate, int countReferringOpinions) {
		this.title = title;
		this.citation = citation;
		this.opinionDate = opinionDate;
		this.countReferringOpinions = countReferringOpinions;
		this.importance = 0;
	}
	public String getTitle() {
		return title;
	}
	public String getCitation() {
		return citation;
	}
	public Date getOpinionDate() {
		return opinionDate;
	}
	public int getCountReferringOpinions() {
		return countReferringOpinions;
	}
	public int getImportance() {
		return importance;
	}
	public void setImportance(int importance) {
		this.importance = importance;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	@Override
	public int compareTo(CaseView o) {
		return citation.compareTo(o.citation);
	}
	@Override
	public String toString() {
		return importance + " : " + citation + "(" + opinionDate + ")";
	}
}
