package opca.view;

import java.util.Date;

public class CaseView implements Comparable<CaseView> {
	private final String title;
	private final String citation;
	private final Date opinionDate;
	private final int countReferringOpinions;
	private int score;
	private int importance;
	
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
}
